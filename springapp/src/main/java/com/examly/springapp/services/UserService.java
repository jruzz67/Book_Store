package com.examly.springapp.services;

import com.examly.springapp.entities.User;
import com.examly.springapp.repositories.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        }
        public User createUser(User user) {
            return userRepository.save(user);
        }
        public List<User> getAllUsers() {
            return userRepository.findAll();
        }
        public Optional<User> getUserById(Long id) {
            return userRepository.findById(id);
        }
        public void deleteUser(Long id) {
            userRepository.deleteById(id);
        }
        public User updateUser(Long id, User userDetails) {
            Optional<User> existingUser = userRepository.findById(id);
            if (existingUser.isPresent()) {
                User user = existingUser.get();
                user.setUsername(userDetails.getUsername());
                user.setEmail(userDetails.getEmail());
                user.setPassword(userDetails.getPassword());
                return userRepository.save(user);
            }
            return null;
        }
    }
            