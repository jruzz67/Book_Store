package com.examly.springapp.controllers;

import com.examly.springapp.entities.Review;
import com.examly.springapp.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books/{bookId}/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> createReview(
            @PathVariable Long bookId,
            @RequestParam int rating,
            @RequestParam String comment) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        Review createdReview = reviewService.createReview(bookId, username, rating, comment);
        return ResponseEntity.status(201).body(createdReview);
    }

    @GetMapping
    public ResponseEntity<Page<Review>> getReviewsByBookId(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        Page<Review> reviews = reviewService.getReviewsByBookId(bookId, username, page, size, sortBy);
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/{reviewId}/approve")
    public ResponseEntity<Review> approveReview(
            @PathVariable Long bookId,
            @PathVariable Long reviewId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        Review review = reviewService.approveReview(reviewId, bookId, username);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long bookId,
            @PathVariable Long reviewId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        reviewService.deleteReview(reviewId, bookId, username);
        return ResponseEntity.noContent().build();
    }
}