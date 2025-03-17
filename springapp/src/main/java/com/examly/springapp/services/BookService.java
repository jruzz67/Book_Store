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
        return bookRepository.save(book);
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public List<Book> getBooksByGenre(String genre) {
        return bookRepository.findByGenresContaining(genre); // Updated method
    }

    public List<Book> getAllBooksSorted(String sortBy) {
        return bookRepository.findAll(Sort.by(Sort.Order.asc(sortBy)));
    }

    public Page<Book> getBooksWithPagination(int page, int size) {
        return bookRepository.findAll(PageRequest.of(page, size));
    }

    // Add method to search by title (optional, based on repository)
    public List<Book> getBooksByTitle(String title) {
        return bookRepository.findBooksByTitle(title);
    }
}