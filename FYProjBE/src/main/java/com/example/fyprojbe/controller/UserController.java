package com.example.fyprojbe.controller;

import com.example.fyprojbe.model.User;
import com.example.fyprojbe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    // Get a specific user by ID
    @GetMapping("/user/{userId}")
    public User getUser(@PathVariable String userId) {
        return userService.getUser(userId);
    }

    // Get all users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Create a new user
    @PostMapping("/user/create")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
}
