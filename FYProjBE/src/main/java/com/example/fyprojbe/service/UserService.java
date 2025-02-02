package com.example.fyprojbe.service;

import com.example.fyprojbe.model.User;
import com.example.fyprojbe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User getUser(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
