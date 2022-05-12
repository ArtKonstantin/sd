package org.example.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Authentication {
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_CLIENT = "CLIENT";
    public static final String ROLE_ENGINEER = "ENGINEER";
    private long id;
    private String login;
    private String password;
    private String role;
}
