package com.examly.springapp.services;

import com.examly.springapp.entities.Cart;
import com.examly.springapp.entities.User;
import com.examly.springapp.repositories.UserRepository;
import com.examly.springapp.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public User createUser(User user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        System.out.println("Registering user: " + user.getUsername() + ", Original password: " + user.getPassword());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        System.out.println("Registering user: " + user.getUsername() + ", Hashed password: " + user.getPassword());
        Cart defaultCart = new Cart();
        defaultCart.setTotalCost(0.0);
        defaultCart.setCartItems(new ArrayList<>());
        user.setCart(defaultCart);
        User savedUser = userRepository.save(user);
        System.out.println("User saved: " + savedUser.getUsername() + ", Final hashed password: " + savedUser.getPassword());
        return savedUser;
    }

    public String loginUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        System.out.println("Login attempt for: " + username + ", Provided password: " + password + ", Stored hash: " + user.getPassword());
        boolean passwordMatch = bCryptPasswordEncoder.matches(password, user.getPassword());
        System.out.println("Password match result for " + username + ": " + passwordMatch);
        if (!passwordMatch) {
            System.out.println("Password match failed for user: " + username + ", Provided: " + password + ", Stored: " + user.getPassword());
            throw new UsernameNotFoundException("Invalid password for username: " + username);
        }
        return generateTokenForUser(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

    public String generateTokenForUser(User user) {
        return jwtUtil.generateToken(loadUserByUsername(user.getUsername()));
    }
}