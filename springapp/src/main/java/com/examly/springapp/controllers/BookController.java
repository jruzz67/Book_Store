package com.examly.springapp.controllers;

import com.examly.springapp.entities.Book;
import com.examly.springapp.entities.Cart;
import com.examly.springapp.entities.User;
import com.examly.springapp.services.BookService;
import com.examly.springapp.services.CartService;
import com.examly.springapp.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Book Management", description = "Operations for managing books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Create a new book", description = "Creates a new book and returns the created book")
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User loggedInUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        book.setUser(loggedInUser);
        Book savedBook = bookService.createBook(book);
        return ResponseEntity.status(201).body(savedBook);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a book by ID", description = "Returns a book by its ID")
    public ResponseEntity<Book> getBookById(
            @PathVariable @Parameter(description = "ID of the book", required = true) Long id) {
        Optional<Book> book = bookService.getBookById(id);
        return book.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all books", description = "Returns a list of all books")
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book by ID", description = "Deletes a book by its ID (only by the author)")
    public ResponseEntity<Void> deleteBook(
            @PathVariable @Parameter(description = "ID of the book to delete", required = true) Long id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User loggedInUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        if (!book.getUser().getId().equals(loggedInUser.getId())) {
            throw new RuntimeException("Only the author can delete this book");
        }
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/genre/{genre}")
    @Operation(summary = "Get books by genre", description = "Returns a list of books by genre")
    public ResponseEntity<List<Book>> getBooksByGenre(
            @PathVariable @Parameter(description = "Genre to search for", required = true) String genre) {
        List<Book> books = bookService.getBooksByGenre(genre);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/title/{title}")
    @Operation(summary = "Search books by title", description = "Returns a list of books with titles matching the search term")
    public ResponseEntity<List<Book>> searchBooksByTitle(
            @PathVariable @Parameter(description = "Title to search for", required = true) String title) {
        List<Book> books = bookService.searchByTitle(title);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/author/{author}")
    @Operation(summary = "Search books by author", description = "Returns a list of books by author")
    public ResponseEntity<List<Book>> searchBooksByAuthor(
            @PathVariable @Parameter(description = "Author to search for", required = true) String author) {
        List<Book> books = bookService.searchByAuthor(author);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/price-range")
    @Operation(summary = "Filter books by price range", description = "Returns a list of books within a price range")
    public ResponseEntity<List<Book>> filterByPriceRange(
            @RequestParam @Parameter(description = "Minimum price", required = true) Double minPrice,
            @RequestParam @Parameter(description = "Maximum price", required = true) Double maxPrice) {
        List<Book> books = bookService.filterByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/sorted")
    @Operation(summary = "Get all books sorted by a field", description = "Returns a list of books sorted by a specified field")
    public ResponseEntity<List<Book>> getAllBooksSorted(
            @RequestParam @Parameter(description = "Field to sort by (e.g., price, title)", required = true) String sortBy) {
        List<Book> books = bookService.getAllBooksSorted(sortBy);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/pagination")
    @Operation(summary = "Get books with pagination", description = "Returns a paginated list of books")
    public ResponseEntity<Page<Book>> getBooksWithPagination(
            @RequestParam @Parameter(description = "Page number", example = "0") int page,
            @RequestParam @Parameter(description = "Page size", example = "10") int size) {
        Page<Book> books = bookService.getBooksWithPagination(page, size);
        return ResponseEntity.ok(books);
    }

    @PostMapping("/buy/{bookId}")
    @Operation(summary = "Add a book to cart", description = "Adds a book to the logged-in user's cart")
    public ResponseEntity<Cart> buyBook(
            @PathVariable @Parameter(description = "ID of the book to buy", required = true) Long bookId,
            @RequestParam @Parameter(description = "Quantity to add", required = true) int quantity) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User loggedInUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart updatedCart = cartService.addBookToCart(loggedInUser, bookId, quantity);
        return ResponseEntity.ok(updatedCart);
    }
}