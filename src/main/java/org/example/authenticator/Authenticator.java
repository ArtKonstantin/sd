package org.example.authenticator;

import lombok.AllArgsConstructor;
import org.example.authentication.Authentication;
import org.example.exception.NotAuthenticatedException;
import org.example.exception.PasswordNotMatchesException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
@AllArgsConstructor
public class Authenticator {
    private HttpServletRequest request;
    private NamedParameterJdbcTemplate template;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Authentication authenticate() throws NotAuthenticatedException, PasswordNotMatchesException {
        String login = request.getHeader("X-Login");
        if (login == null) {
            throw new NotAuthenticatedException();
        }
        String password = request.getHeader("X-Password");
        if (password == null) {
            throw new NotAuthenticatedException();
        }
        Authentication authentication = template.queryForObject(
                // language=PostgreSQL
                """
                        SELECT id, login, password, role FROM users
                        WHERE login = :login AND removed = FALSE
                        """,
                Map.of(
                        "login", login
                ),
                BeanPropertyRowMapper.newInstance(Authentication.class)
        );

        if (!passwordEncoder.matches(password, authentication.getPassword())) {
            throw new PasswordNotMatchesException();
        }

        authentication.setPassword("***");
        return authentication;
    }
}
