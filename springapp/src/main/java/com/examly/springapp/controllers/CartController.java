package com.examly.springapp.controllers;

import com.examly.springapp.entities.Cart;
import com.examly.springapp.entities.Ordertable;
import com.examly.springapp.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
public class CartController {

    @Autowired
    private CartService cartService;

    // Default Cart Operations

    @GetMapping("/cart")
    public ResponseEntity<Cart> getDefaultCart(@PathVariable Long userId) {
        Cart cart = cartService.getOrCreateDefaultCart(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/cart/add")
    public ResponseEntity<Cart> addBookToDefaultCart(
            @PathVariable Long userId,
            @RequestParam Long bookId,
            @RequestParam Integer quantity) {
        Cart cart = cartService.addBookToCart(userId, null, bookId, quantity, true);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/cart/remove")
    public ResponseEntity<Cart> removeBookFromDefaultCart(
            @PathVariable Long userId,
            @RequestParam Long bookId,
            @RequestParam Integer quantity) {
        Cart cart = cartService.removeBookFromCart(userId, null, bookId, quantity, true);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/cart/checkout")
    public ResponseEntity<Ordertable> checkoutDefaultCart(@PathVariable Long userId) {
        Ordertable order = cartService.checkoutCart(userId, null, true);
        return ResponseEntity.status(201).body(order);
    }

    @DeleteMapping("/cart/clear")
    public ResponseEntity<Cart> clearDefaultCart(@PathVariable Long userId) {
        Cart cart = cartService.clearCart(userId, null, true);
        return ResponseEntity.ok(cart);
    }

    // Additional Cart Operations

    @PostMapping("/cart/create")
    public ResponseEntity<Cart> createCart(
            @PathVariable Long userId,
            @RequestParam String name) {
        Cart cart = cartService.createCart(userId, name);
        return ResponseEntity.status(201).body(cart);
    }

    @GetMapping("/cart/{cartId}")
    public ResponseEntity<Cart> getCartById(
            @PathVariable Long userId,
            @PathVariable Long cartId) {
        Cart cart = cartService.getCartById(userId, cartId);
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<Cart>> getAllCarts(@PathVariable Long userId) {
        List<Cart> carts = cartService.getCartsByUser(userId);
        return ResponseEntity.ok(carts);
    }

    @PostMapping("/cart/{cartId}/add")
    public ResponseEntity<Cart> addBookToCart(
            @PathVariable Long userId,
            @PathVariable Long cartId,
            @RequestParam Long bookId,
            @RequestParam Integer quantity) {
        Cart cart = cartService.addBookToCart(userId, cartId, bookId, quantity, false);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/cart/{cartId}/remove")
    public ResponseEntity<Cart> removeBookFromCart(
            @PathVariable Long userId,
            @PathVariable Long cartId,
            @RequestParam Long bookId,
            @RequestParam Integer quantity) {
        Cart cart = cartService.removeBookFromCart(userId, cartId, bookId, quantity, false);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/cart/{cartId}/checkout")
    public ResponseEntity<Ordertable> checkoutCart(
            @PathVariable Long userId,
            @PathVariable Long cartId) {
        Ordertable order = cartService.checkoutCart(userId, cartId, false);
        return ResponseEntity.status(201).body(order);
    }

    @DeleteMapping("/cart/{cartId}/clear")
    public ResponseEntity<Cart> clearCart(
            @PathVariable Long userId,
            @PathVariable Long cartId) {
        Cart cart = cartService.clearCart(userId, cartId, false);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/cart/{cartId}")
    public ResponseEntity<Void> deleteCart(
            @PathVariable Long userId,
            @PathVariable Long cartId) {
        cartService.deleteCart(userId, cartId);
        return ResponseEntity.noContent().build();
    }
}