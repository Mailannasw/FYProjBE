package com.example.fyprojbe.model.response;

// When a JWT is set, I want it to be immutable until it expires or logs out (Albano, 2024)(Trandafir, 2024)
// Similar to Lombok in that it replaces boilerplate code (getter/setter/etc) for JWTs.
// https://www.baeldung.com/java-record-keyword
// https://www.baeldung.com/java-record-vs-lombok
public record JwtResponse(String jwt) { }
