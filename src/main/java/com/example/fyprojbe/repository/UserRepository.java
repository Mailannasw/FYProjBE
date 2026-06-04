package com.example.fyprojbe.repository;

import com.example.fyprojbe.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

// Uses Spring Data for MongoDB, which generates queries based on method names
// Reduces boilerplate code
public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);
}
