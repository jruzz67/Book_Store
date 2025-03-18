package com.examly.springapp.controllers;

import com.examly.springapp.entities.Cart;
import com.examly.springapp.entities.User;
import com.examly.springapp.services.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.examly.springapp.repositories.UserRepository;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart Management", description = "Operations for managing the user's cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get the current user's cart.
     */
    @GetMapping
    @Operation(summary = "Get the user's cart", description = "Returns the current user's default cart")
    public ResponseEntity<Cart> getCart() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartService.getOrCreateCart(user);
        return ResponseEntity.ok(cart);
    }

    /**
     * Add a book to the user's cart.
     */
    @PostMapping("/add/{bookId}")
    @Operation(summary = "Add a book to the cart", description = "Adds a specified quantity of a book to the user's cart")
    public ResponseEntity<Cart> addBookToCart(
            @PathVariable @Parameter(description = "ID of the book to add", required = true) Long bookId,
            @RequestParam @Parameter(description = "Quantity to add", required = true) Integer quantity) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart updatedCart = cartService.addBookToCart(user, bookId, quantity);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * Remove a book from the user's cart.
     */
    @PostMapping("/remove/{bookId}")
    @Operation(summary = "Remove a book from the cart", description = "Removes a specified quantity of a book from the user's cart")
    public ResponseEntity<Cart> removeBookFromCart(
            @PathVariable @Parameter(description = "ID of the book to remove", required = true) Long bookId,
            @RequestParam @Parameter(description = "Quantity to remove", required = true) Integer quantity) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart updatedCart = cartService.removeBookFromCart(user, bookId, quantity);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * Checkout the user's cart.
     */
    @PostMapping("/checkout")
    @Operation(summary = "Checkout the cart", description = "Processes the cart and creates an order")
    public ResponseEntity<String> checkoutCart() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        cartService.checkoutCart(user);
        return ResponseEntity.ok("Checkout successful");
    }

    /**
     * Clear the user's cart.
     */
    @PostMapping("/clear")
    @Operation(summary = "Clear the cart", description = "Removes all items from the user's cart")
    public ResponseEntity<Cart> clearCart() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart clearedCart = cartService.clearCart(user);
        return ResponseEntity.ok(clearedCart);
    }
}