package com.example.demo.controller;

import com.example.demo.api.UsersApi;
import com.example.demo.model.User;
import com.example.demo.service.UsersService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final UsersService usersService;

    public List<User> getUsers(String id, String username, String name, String surname) {
        return usersService.getUsers(id, username, name, surname);
    }
}
