package com.examly.springapp.services;

import com.examly.springapp.entities.Book;
import com.examly.springapp.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    public Book createBook(Book book) {
        if (book.getReviews() == null || book.getReviews().isEmpty()) {
            book.setNumberOfReviews(0);
            book.setAverageRating(0.0);
        } else {
            book.recalculateMetrics();
        }
        return bookRepository.save(book);
    }

    public Optional<Book> getBookById(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        book.ifPresent(Book::recalculateMetrics);
        return book;
    }

    public List<Book> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        books.forEach(Book::recalculateMetrics);
        return books;
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id); // Author check is now in the controller
    }

    public List<Book> getBooksByGenre(String genre) {
        List<Book> books = bookRepository.findByGenresContaining(genre);
        books.forEach(Book::recalculateMetrics);
        return books;
    }

    public List<Book> getAllBooksSorted(String sortBy) {
        List<Book> books = bookRepository.findAll(Sort.by(Sort.Order.asc(sortBy)));
        books.forEach(Book::recalculateMetrics);
        return books;
    }

    public Page<Book> getBooksWithPagination(int page, int size) {
        Page<Book> bookPage = bookRepository.findAll(PageRequest.of(page, size));
        bookPage.getContent().forEach(Book::recalculateMetrics);
        return bookPage;
    }

    public List<Book> searchByTitle(String title) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);
        books.forEach(Book::recalculateMetrics);
        return books;
    }

    public List<Book> searchByAuthor(String author) {
        List<Book> books = bookRepository.findByAuthorContainingIgnoreCase(author);
        books.forEach(Book::recalculateMetrics);
        return books;
    }

    public List<Book> filterByPriceRange(Double minPrice, Double maxPrice) {
        List<Book> books = bookRepository.findByPriceBetween(minPrice, maxPrice);
        books.forEach(Book::recalculateMetrics);
        return books;
    }
}