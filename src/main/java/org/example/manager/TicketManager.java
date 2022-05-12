package org.example.manager;

import org.apache.tika.Tika;
import org.example.authentication.Authentication;
import org.example.authenticator.Authenticator;
import org.example.dto.*;
import org.example.exception.*;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;

@Component
public class TicketManager {
    private NamedParameterJdbcTemplate template;
    private Authenticator authenticator;
    private Tika tika = new Tika();

    public TicketManager(NamedParameterJdbcTemplate template, Authenticator authenticator) throws IOException {
        this.template = template;
        this.authenticator = authenticator;
        Files.createDirectories(Paths.get("media"));
    }

    public List<TicketGetAllResponceDTO> getAll(long limit, long offset) throws NotAuthenticatedException, PasswordNotMatchesException {
        Authentication authentication = authenticator.authenticate();
        if (authentication.getRole().equals(Authentication.ROLE_CLIENT)) {
            return template.query(
                    // language=PostgreSQL
                    """
                            SELECT id, status, description, file, solution FROM tickets
                            WHERE customer_id = :customer_id
                            ORDER BY id
                            LIMIT :limit OFFSET :offset
                            """,
                    Map.of(
                            "customer_id", authentication.getId(),
                            "limit", limit,
                            "offset", offset
                    ),
                    BeanPropertyRowMapper.newInstance(TicketGetAllResponceDTO.class)
            );
        }
        if (authentication.getRole().equals(Authentication.ROLE_ENGINEER)) {
            return template.query(
                    // language=PostgreSQL
                    """
                            SELECT id, status, description, file, solution FROM tickets
                            WHERE appointed_id = :appointed_id OR status = :status
                            ORDER BY id
                            LIMIT :limit OFFSET :offset
                            """,
                    Map.of(
                            "appointed_id", authentication.getId(),
                            "status", TicketStatus.TICKET_NEW,
                            "limit", limit,
                            "offset", offset
                    ),
                    BeanPropertyRowMapper.newInstance(TicketGetAllResponceDTO.class)
            );
        }
        return template.query(
                // language=PostgreSQL
                """
                        SELECT id, status, description, file, solution FROM tickets
                        ORDER BY id
                        LIMIT :limit OFFSET :offset
                        """,
                Map.of(
                        "limit", limit,
                        "offset", offset
                ),
                BeanPropertyRowMapper.newInstance(TicketGetAllResponceDTO.class)
        );
    }

    public TicketGetByIdResponceDTO getById(long id) throws TicketNotFoundException {
        try {
            return template.queryForObject(
                    // language=PostgreSQL
                    """
                            SELECT id, status, description, file, solution FROM tickets
                            WHERE id = :id
                            """,
                    Map.of("id", id),
                    BeanPropertyRowMapper.newInstance(TicketGetByIdResponceDTO.class)
            );
        } catch (EmptyResultDataAccessException e) {
            throw new TicketNotFoundException(e);
        }
    }

    public TicketCreateResponceDTO create(TicketCreateRequestDTO requestDTO) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException, InvalidDescriptionException {
        Authentication authentication = authenticator.authenticate();
        if (!authentication.getRole().equals(Authentication.ROLE_CLIENT)) {
            throw new ForbiddenException();
        }
        if (requestDTO.getDescription() == null) {
            throw new InvalidDescriptionException();
        }
        return template.queryForObject(
                // language=PostgreSQL
                """
                        INSERT INTO tickets(customer_id, status, description, file) 
                        VALUES (:customer_id, :status, :description, :file)
                        RETURNING id, status, description, file
                        """,
                Map.of(
                        "customer_id", authentication.getId(),
                        "status", TicketStatus.TICKET_NEW,
                        "description", requestDTO.getDescription(),
                        "file", requestDTO.getFile()
                ), BeanPropertyRowMapper.newInstance(TicketCreateResponceDTO.class)
        );
    }

    public TicketUpdateAcceptedResponceDTO updateAccepted(TicketUpdateAcceptedRequestDTO requestDTO) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException, InvalidSlaException, TicketNotFoundException {
        Authentication authentication = authenticator.authenticate();
        if (!authentication.getRole().equals(Authentication.ROLE_ENGINEER)) {
            throw new ForbiddenException();
        }
        long total = template.queryForObject(
                // language=PostgreSQL
                """
                        SELECT COUNT(*) FROM sla
                        """, Map.of(),
                Long.class
        );
        if (requestDTO.getSla_id() <= 0 || requestDTO.getSla_id() > total) {
            throw new InvalidSlaException();
        }
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        try {
            return template.queryForObject(
                    // language=PostgreSQL
                    """
                            UPDATE tickets
                            SET status = :new_status, appointed_id = :appointed_id, accepted = :accepted, sla_id = :sla_id
                            WHERE id = :id AND status = :status
                            RETURNING id, appointed_id, status, description, file
                            """,
                    Map.of(
                            "id", requestDTO.getId(),
                            "status", TicketStatus.TICKET_NEW,
                            "appointed_id", authentication.getId(),
                            "new_status", TicketStatus.TICKET_ACCEPTED,
                            "accepted", timestamp,
                            "sla_id", requestDTO.getSla_id()
                    ), BeanPropertyRowMapper.newInstance(TicketUpdateAcceptedResponceDTO.class)
            );
        } catch (EmptyResultDataAccessException e) {
            throw new TicketNotFoundException(e);
        }
    }

    public TicketUpdateDecidedResponceDTO updateDecided(TicketUpdateDecidedRequestDTO requestDTO) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException, TicketNotFoundException, InvalidSolutionException {
        Authentication authentication = authenticator.authenticate();
        if (!authentication.getRole().equals(Authentication.ROLE_ENGINEER)) {
            throw new ForbiddenException();
        }
        if (requestDTO.getSolution() == null) {
            throw new InvalidSolutionException();
        }
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        try {
            return template.queryForObject(
                    // language=PostgreSQL
                    """
                        UPDATE tickets
                        SET status = :new_status, solution = :solution, closed = :closed
                        WHERE id = :id AND status = :status AND appointed_id = :appointed_id
                        RETURNING id, status, description, file, solution
                        """,
                Map.of(
                        "id", requestDTO.getId(),
                        "status", TicketStatus.TICKET_ACCEPTED,
                        "appointed_id", authentication.getId(),
                        "new_status", TicketStatus.TICKET_DECIDED,
                        "solution", requestDTO.getSolution(),
                        "closed", timestamp
                ), BeanPropertyRowMapper.newInstance(TicketUpdateDecidedResponceDTO.class)
        );
        } catch (EmptyResultDataAccessException e) {
            throw new TicketNotFoundException(e);
        }
    }

    public TicketUpdateArchivedByIdResponceDTO updateArchivedById(long id) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException, TicketNotFoundException {
        Authentication authentication = authenticator.authenticate();
        if (!authentication.getRole().equals(Authentication.ROLE_ADMIN)) {
            throw new ForbiddenException();
        }
        try {
            return template.queryForObject(
                    // language=PostgreSQL
                    """
                            UPDATE tickets
                            SET status = :new_status
                            WHERE id = :id AND status = :status
                            RETURNING id, status, description, file
                            """,
                    Map.of(
                            "id", id,
                            "status", TicketStatus.TICKET_DECIDED,
                            "new_status", TicketStatus.TICKET_ARCHIVED
                    ), BeanPropertyRowMapper.newInstance(TicketUpdateArchivedByIdResponceDTO.class)
            );
        } catch (EmptyResultDataAccessException e) {
            throw new TicketNotFoundException(e);
        }
    }

    public MediaUploadResponseDTO upload(byte[] data) throws IOException, ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException {
        Authentication authentication = authenticator.authenticate();
        if (authentication.getRole().equals(Authentication.ROLE_CLIENT)) {
        String name = UUID.randomUUID().toString();
        String mime = tika.detect(data);
        name = withExtension(name, mime);
        Path path = path(name);
        Files.write(path, data);
        return new MediaUploadResponseDTO(name);
    }
        else {
            throw new ForbiddenException();
        }
    }

    private String withExtension(String name, String mime) {
        if (mime.equals("image/png")) {
            return name + ".png";
        }
        return name;
    }

    private Path path(String name) {
        return Paths.get("media", name);
    }

    public List<TicketSelectByTypeSlaResponceDTO> selectByTypeSla(int sla_id, long limit, long offset) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException, InvalidSlaException {
        Authentication authentication = authenticator.authenticate();
        if (!authentication.getRole().equals(Authentication.ROLE_ADMIN)) {
            throw new ForbiddenException();
        }
        long total = template.queryForObject(
                // language=PostgreSQL
                """
                        SELECT COUNT(*) FROM sla
                        """, Map.of(),
                Long.class
        );
        if (sla_id <= 0 || sla_id > total) {
            throw new InvalidSlaException();
        }
        return template.query(
                    // language=PostgreSQL
                    """
                            SELECT s.criticality, tk.status, uc.login customer, tk.description, tk.file, ua.login appointed, tk.solution
                            FROM tickets tk
                            JOIN users uc ON tk.customer_id = uc.id
                            JOIN users ua ON tk.appointed_id = ua.id
                            JOIN sla s ON tk.sla_id = s.id
                            WHERE sla_id = :sla_id
                            LIMIT :limit OFFSET :offset
                            """, Map.of(
                            "sla_id", sla_id,
                            "limit", limit,
                            "offset", offset
                    ), BeanPropertyRowMapper.newInstance(TicketSelectByTypeSlaResponceDTO.class)
        );
    }

    public List<TicketSelectAcceptedAllResponceDTO> selectAcceptedAll(long limit, long offset) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException {
        Authentication authentication = authenticator.authenticate();
        if (!authentication.getRole().equals(Authentication.ROLE_ADMIN)) {
            throw new ForbiddenException();
        }
        return template.query(
                // language=PostgreSQL
                """
                        SELECT s.criticality, tk.status, uc.login customer, tk.description, tk.file, ua.login appointed, tk.solution
                        FROM tickets tk 
                        JOIN users uc ON tk.customer_id = uc.id
                        JOIN users ua ON tk.appointed_id = ua.id
                        JOIN sla s ON tk.sla_id = s.id
                        WHERE tk.status = :status 
                        ORDER BY s.id
                        LIMIT :limit OFFSET :offset
                        """, Map.of(
                        "status", TicketStatus.TICKET_ACCEPTED,
                        "limit", limit,
                        "offset", offset
                ), BeanPropertyRowMapper.newInstance(TicketSelectAcceptedAllResponceDTO.class)
        );
    }

    public List<TicketSelectDecidedResponceDTO> selectDecided(long limit, long offset) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException {
        Authentication authentication = authenticator.authenticate();
        if (!authentication.getRole().equals(Authentication.ROLE_ADMIN)) {
            throw new ForbiddenException();
        }
        return template.query(
                // language=PostgreSQL
                """
                        SELECT u.login appointed, COUNT(*) total
                        FROM tickets tk
                        JOIN users u ON tk.appointed_id = u.id
                        WHERE status = :status
                        GROUP BY u.login
                        LIMIT :limit OFFSET :offset
                        """, Map.of(
                        "status", TicketStatus.TICKET_DECIDED,
                        "limit", limit,
                        "offset", offset
                ), BeanPropertyRowMapper.newInstance(TicketSelectDecidedResponceDTO.class)
        );
    }

    public List<TicketSelectOverdueResponceDTO> selectOverdue(long limit, long offset) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException {
        Authentication authentication = authenticator.authenticate();
        if (!authentication.getRole().equals(Authentication.ROLE_ADMIN)) {
            throw new ForbiddenException();
        }
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        return template.query(
                // language=PostgreSQL
                """
                        SELECT s.criticality, tk.status, uc.login customer, tk.description, tk.file, ua.login appointed, tk.solution
                        FROM tickets tk 
                        JOIN users uc ON tk.customer_id = uc.id
                        JOIN users ua ON tk.appointed_id = ua.id
                        JOIN sla s ON tk.sla_id = s.id
                        WHERE tk.status = :status AND tk.accepted + s.lead_time < :timestamp 
                        LIMIT :limit OFFSET :offset
                        """, Map.of(
                        "status", TicketStatus.TICKET_ACCEPTED,
                        "timestamp", timestamp,
                        "limit", limit,
                        "offset", offset
                ), BeanPropertyRowMapper.newInstance(TicketSelectOverdueResponceDTO.class)
        );
    }

    public TicketResolutionRateResponceDTO resolutionRate() throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException {
        Authentication authentication = authenticator.authenticate();
        if (!authentication.getRole().equals(Authentication.ROLE_ADMIN)) {
            throw new ForbiddenException();
        }
        long total = template.queryForObject(
                // language=PostgreSQL
                """
                        SELECT COUNT(status) FROM tickets
                        """, Map.of(),
                Long.class
        );

        long decided = template.queryForObject(
                // language=PostgreSQL
                """
                        SELECT COUNT(status) FROM tickets
                        WHERE status = :status_decided OR status = :status_archived
                        """, Map.of(
                                "status_decided", TicketStatus.TICKET_DECIDED,
                                "status_archived", TicketStatus.TICKET_ARCHIVED
                ),
                Long.class
        );

        TicketResolutionRateResponceDTO result = new TicketResolutionRateResponceDTO();
        result.setResolutionRate((double)decided / total);
        return result;
    }
}
