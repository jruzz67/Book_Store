    package com.examly.springapp.controllers;

    import com.examly.springapp.entities.Book;
    import com.examly.springapp.services.BookService;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.Parameter;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.domain.Page;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.Optional;

    @RestController
    @RequestMapping("/books")
    @Tag(name = "Book Management", description = "Operations for managing books")
    public class BookController {

        @Autowired
        private BookService bookService;

        @PostMapping
        @Operation(summary = "Create a new book", description = "Creates a new book and returns the created book")
        public ResponseEntity<Book> createBook(@RequestBody Book book) {
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
        @Operation(summary = "Delete a book by ID", description = "Deletes a book by its ID")
        public ResponseEntity<Void> deleteBook(
                @PathVariable @Parameter(description = "ID of the book to delete", required = true) Long id) {
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
        @Operation(summary = "Get books by title", description = "Returns a list of books by title")
        public ResponseEntity<List<Book>> getBooksByTitle(
                @PathVariable @Parameter(description = "Title to search for", required = true) String title) {
            List<Book> books = bookService.getBooksByTitle(title);
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
    }