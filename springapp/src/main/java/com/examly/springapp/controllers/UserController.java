
package com.examly.springapp.controllers;

import com.examly.springapp.entities.User;
import com.examly.springapp.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.status(201).body(userService.createUser(user));
    }
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}