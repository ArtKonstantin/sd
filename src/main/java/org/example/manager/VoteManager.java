package org.example.manager;

import lombok.AllArgsConstructor;
import org.example.authentication.Authentication;
import org.example.authenticator.Authenticator;
import org.example.dto.TicketStatus;
import org.example.dto.VoteResponceDTO;
import org.example.dto.VoteStatsResponceDTO;
import org.example.exception.*;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class VoteManager {
    private NamedParameterJdbcTemplate template;
    private Authenticator authenticator;

    public VoteResponceDTO make(long id, int rating) throws NotAuthenticatedException, PasswordNotMatchesException, InvalidRatingException, InvalidVoteException, VoteAlreadyMadeException {
        long appointed;
        if (rating < 1 || rating > 5) {
            throw new InvalidRatingException();
        }
        Authentication authentication = authenticator.authenticate();
        try {
                appointed = template.queryForObject(
                    // language=PostgreSQL
                    """
                            SELECT appointed_id FROM tickets 
                            WHERE status = :status AND id = :tickets_id AND customer_id = :customer_id;
                            """, Map.of(
                            "status", TicketStatus.TICKET_ARCHIVED,
                            "tickets_id", id,
                            "customer_id", authentication.getId()
                    ),
                    Long.class
            );
        } catch (EmptyResultDataAccessException e) {
            throw new InvalidVoteException();
        }

        try {
            return template.queryForObject(
                    // language=PostgreSQL
                    """    
                            INSERT INTO votes(tickets_id, customer_id, appointed_id, rating)
                            VALUES (:tickets_id, :customer_id, :appointed, :rating)
                            RETURNING tickets_id ticketId, rating
                            """,
                    Map.of(
                            "tickets_id", id,
                            "appointed", appointed,
                            "customer_id", authentication.getId(),
                            "rating", rating
                    ), BeanPropertyRowMapper.newInstance(VoteResponceDTO.class)
            );
        } catch (DuplicateKeyException e) {
            throw new VoteAlreadyMadeException();
        }
    }

    public List<VoteStatsResponceDTO> stats(long limit, long offset) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException {
        Authentication authentication = authenticator.authenticate();
        if (!authentication.getRole().equals(Authentication.ROLE_ADMIN)) {
            throw new ForbiddenException();
        }
        return template.query(
                // language=PostgreSQL
                """
                SELECT u.login appointed, MIN(v.rating) minRating, MAX(v.rating)maxRating, AVG(v.rating)avgRating 
                FROM votes v  
                JOIN users u ON v.appointed_id = u.id
                GROUP BY u.login
                LIMIT :limit OFFSET :offset
                """,
                Map.of(
                        "limit", limit,
                        "offset", offset
                ),
                BeanPropertyRowMapper.newInstance(VoteStatsResponceDTO.class)
        );
    }
}
