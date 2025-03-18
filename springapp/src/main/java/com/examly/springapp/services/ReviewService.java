package com.examly.springapp.services;

import com.examly.springapp.entities.Book;
import com.examly.springapp.entities.Review;
import com.examly.springapp.entities.User;
import com.examly.springapp.repositories.BookRepository;
import com.examly.springapp.repositories.ReviewRepository;
import com.examly.springapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    public Review createReview(Long bookId, String username, int rating, String comment) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Review review = new Review();
        review.setBook(book);
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);
        review.setApproved(false);
        review.setCreatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    public Page<Review> getReviewsByBookId(Long bookId, String username, int page, int size, String sortBy) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Sort sort = Sort.unsorted();
        if ("rating".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "rating");
        } else if ("date".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Review> allReviews = reviewRepository.findByBook(book, pageable);

        List<Review> filteredReviews = new ArrayList<>();
        for (Review review : allReviews.getContent()) {
            // Show approved reviews to all users
            if (review.isApproved()) {
                filteredReviews.add(review);
                continue;
            }
            // Show unapproved reviews to the reviewer or book author
            User reviewer = review.getUser();
            User bookAuthor = book.getUser();
            if (reviewer != null && bookAuthor != null &&
                (reviewer.getId().equals(currentUser.getId()) || bookAuthor.getId().equals(currentUser.getId()))) {
                filteredReviews.add(review);
            }
        }

        return new PageImpl<>(filteredReviews, pageable, allReviews.getTotalElements());
    }

    public Review approveReview(Long reviewId, Long bookId, String username) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        Book book = review.getBook();
        if (!book.getId().equals(bookId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Review does not belong to the specified book");
        }
        User author = book.getUser();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!author.getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the book's author can approve reviews");
        }

        review.setApproved(true);
        return reviewRepository.save(review);
    }

    public void deleteReview(Long reviewId, Long bookId, String username) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        Book book = review.getBook();
        if (!book.getId().equals(bookId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Review does not belong to the specified book");
        }
        User reviewAuthor = review.getUser();
        User bookAuthor = book.getUser();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!reviewAuthor.getId().equals(currentUser.getId()) && !bookAuthor.getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the review author or book's author can delete this review");
        }

        reviewRepository.delete(review);
    }
}