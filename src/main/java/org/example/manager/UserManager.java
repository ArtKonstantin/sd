package org.example.manager;

import lombok.AllArgsConstructor;
import org.example.authentication.Authentication;
import org.example.authenticator.Authenticator;
import org.example.dto.*;
import org.example.exception.*;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class UserManager {
    private NamedParameterJdbcTemplate template;
    private Authenticator authenticator;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<UserGetAllResponceDTO> getAll(int limit, long offset) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException {
            Authentication authentication = authenticator.authenticate();
            if (!authentication.getRole().equals(Authentication.ROLE_ADMIN)) {
                throw new ForbiddenException();
            }
        return template.query(
                // language=PostgreSQL
                """
                SELECT id, login, role FROM users
                WHERE removed = FALSE
                ORDER BY id
                LIMIT :limit OFFSET :offset       
                """,
                Map.of(
                        "limit", limit,
                        "offset", offset
                ),
                BeanPropertyRowMapper.newInstance(UserGetAllResponceDTO.class)
        );
    }

    public UserGetByIdResponceDTO getById(long id) throws UserNotFoundException {
        try {
            return template.queryForObject(
                    // language=PostgreSQL
                    """
                            SELECT id, login, role FROM users
                            WHERE id = :id    
                            """,
                    Map.of(
                            "id", id
                    ),
                    BeanPropertyRowMapper.newInstance(UserGetByIdResponceDTO.class)
            );
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException();
        }
    }

    public UserRegisterResponceDTO register(UserRegisterRequestDTO requestDTO) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException, InvalidRoleException {
        Authentication authentication = authenticator.authenticate();
        if (!authentication.getRole().equals(Authentication.ROLE_ADMIN)) {
            throw new ForbiddenException();
        }

        if (!(requestDTO.getRole().equals(Authentication.ROLE_ADMIN) || requestDTO.getRole().equals(Authentication.ROLE_CLIENT) || requestDTO.getRole().equals(Authentication.ROLE_ENGINEER)))
            throw new InvalidRoleException();

        String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());
        return template.queryForObject(
                // language=PostgreSQL
                        """
                        INSERT INTO users(login, password, role) 
                        VALUES (:login, :password, :role)
                        RETURNING id, login, role
                        """,
                Map.of(
                        "login",requestDTO.getLogin(),
                        "password", encodedPassword,
                        "role", requestDTO.getRole()
                ), BeanPropertyRowMapper.newInstance(UserRegisterResponceDTO.class)
        );
    }

    public UserMeResponceDTO me() throws NotAuthenticatedException, PasswordNotMatchesException {
        Authentication authentication = authenticator.authenticate();
        return new UserMeResponceDTO(
                authentication.getId(),
                authentication.getLogin(),
                authentication.getRole()
        );
    }

    public UserRemoveByIdResponceDTO removeById(long id) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException, UserNotFoundException {
        Authentication authentication = authenticator.authenticate();
        if (authentication.getRole().equals(Authentication.ROLE_ADMIN)) {
            try {
                return template.queryForObject(
                        // language=PostgreSQL
                        """
                        UPDATE users SET removed = TRUE WHERE id = :id
                        RETURNING id, login, role
                        """,
                        Map.of("id", id),
                        BeanPropertyRowMapper.newInstance(UserRemoveByIdResponceDTO.class)
                );
                } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException();
                }
        }
        if (authentication.getId() == id) {
            try {
                return template.queryForObject(
                        // language=PostgreSQL
                        """
                        UPDATE users SET removed = TRUE WHERE id = :id
                        RETURNING id, login, role
                        """,
                        Map.of("id", id),
                        BeanPropertyRowMapper.newInstance(UserRemoveByIdResponceDTO.class)
                );
                } catch (EmptyResultDataAccessException e) {
                throw new UserNotFoundException();
            }
        }
        throw new ForbiddenException();
    }

    public UserRestoreByIdResponceDTO restoreById(long id) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException, UserNotFoundException {
        Authentication authentication = authenticator.authenticate();
        if (!authentication.getRole().equals(Authentication.ROLE_ADMIN)) {
            throw new ForbiddenException();
        }
        try {
            return template.queryForObject(
                    // language=PostgreSQL
                    """
                    UPDATE users SET removed = FALSE WHERE id = :id
                    RETURNING id, login, role
                    """,
                    Map.of("id", id),
                    BeanPropertyRowMapper.newInstance(UserRestoreByIdResponceDTO.class)
            );
            } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException();
        }
    }
}
