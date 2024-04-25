package com.example.demo.controller;

import com.example.demo.api.UserApi;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    public List<User> getUsers(String id, String username, String name, String surname) {
        return userService.getUsers(id, username, name, surname);
    }
}
