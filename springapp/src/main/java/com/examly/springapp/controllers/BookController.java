package com.examly.springapp.controllers;

import com.examly.springapp.entities.Book;
import com.examly.springapp.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/books")

public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book createdBook = bookService.createBook(book);
        return ResponseEntity.ok(createdBook);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Book>> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Book>> getBooksByGenre(@PathVariable String genre) {
        List<Book> books = bookService.getBooksByGenre(genre);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<Book>> getAllBooksSorted(@RequestParam String sortBy) {
        List<Book> books = bookService.getAllBooksSorted(sortBy);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<Book>> getBooksWithPagination(@RequestParam int page, @RequestParam int size) {
        Page<Book> books = bookService.getBooksWithPagination(page, size);
        return ResponseEntity.ok(books);
    }                               
}

