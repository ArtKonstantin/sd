package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.dto.*;
import org.example.exception.*;
import org.example.manager.UserManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@AllArgsConstructor
public class UserController {
    private UserManager manager;

    @RequestMapping("/users.getAll")
    public List<UserGetAllResponceDTO> getAll(int limit, long offset) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException {
        return manager.getAll(limit, offset);
    }

    @RequestMapping("/users.getById")
    public UserGetByIdResponceDTO getById(long id) throws UserNotFoundException {
        return manager.getById(id);
    }

    @RequestMapping("/users.register")
    public UserRegisterResponceDTO register(UserRegisterRequestDTO requestDTO) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException, InvalidRoleException {
        return manager.register(requestDTO);
    }

    @RequestMapping("/users.me")
    public UserMeResponceDTO me() throws NotAuthenticatedException, PasswordNotMatchesException {
        return manager.me();
    }

    @RequestMapping("/users.removeById")
    public UserRemoveByIdResponceDTO removeById(long id) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException, UserNotFoundException {
        return manager.removeById(id);
    }

    @RequestMapping("/users.restoreById")
    public UserRestoreByIdResponceDTO restoreById(long id) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException, UserNotFoundException {
        return manager.restoreById(id);
    }
}
