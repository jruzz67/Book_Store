package com.examly.springapp.repositories;

import com.examly.springapp.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // Remove or comment out the problematic method
    // List<Book> findByGenre(String genre); // This causes the error

    // Updated method to search within the genres collection
    @Query("SELECT b FROM Book b JOIN b.genres g WHERE g = :genre")
    List<Book> findByGenresContaining(@Param("genre") String genre);

    // Existing method
    @Query("SELECT b FROM Book b WHERE b.title = :title")
    List<Book> findBooksByTitle(@Param("title") String title);
}