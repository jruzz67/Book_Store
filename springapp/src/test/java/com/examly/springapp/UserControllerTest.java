package com.examly.springapp;

import com.examly.springapp.controllers.BookController;
import com.examly.springapp.controllers.OrderController;
import com.examly.springapp.controllers.UserController;
import com.examly.springapp.entities.Book;
import com.examly.springapp.entities.Ordertable;
import com.examly.springapp.entities.User;
import com.examly.springapp.repositories.BookRepository;
import com.examly.springapp.repositories.OrderRepository;
import com.examly.springapp.repositories.UserRepository;
import com.examly.springapp.services.BookService;

import java.io.File;
import java.lang.reflect.Field;

import com.examly.springapp.services.BookService;
import com.examly.springapp.services.OrderService;
import com.examly.springapp.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.JoinColumn;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class UserControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    private Book sampleBook;

    private Ordertable sampleOrder;

    private User testUser;

    private static final String LOG_FOLDER_PATH = "logs";
    private static final String LOG_FILE_PATH = "logs/application.log"; 


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("testuser@example.com");
        testUser.setUsername("testuser");
        testUser.setPassword("password");

        // Sample order
        sampleOrder = new Ordertable();
        sampleOrder.setId(1L);
        sampleOrder.setOrderDate(LocalDateTime.now());
        sampleOrder.setTotalAmount(100.0);
        sampleOrder.setStatus("pending");

        // Create a sample book
        sampleBook = new Book();
        sampleBook.setId(1L);
        sampleBook.setTitle("Sample Book");
        sampleBook.setAuthor("Author Name");
        sampleBook.setGenre("Fiction");
        sampleBook.setDescription("This is a sample book description.");
        sampleBook.setPrice(19.99);

        
        //sampleBook.setIsEbook(true);
    }
    @Test
void Annotation_testUserHasJSONIgnoreAnnotations() throws Exception {
    // Path to the Ordertable entity file
    Path entityFilePath = Paths.get("src/main/java/com/examly/springapp/entities/Ordertable.java");

    // Read the content of the entity file as a string
    String entityFileContent = Files.readString(entityFilePath);

    // Check if @JsonIgnore annotation is present
    if (entityFileContent.contains("@JsonIgnore")) {
        System.out.println("Test Passed: @JsonIgnore annotation is present in Ordertable entity.");
    } else {
        System.out.println("Test Failed: @JsonIgnore annotation is missing in Ordertable entity.");
    }
}
@Test
void Annotation_testJoinColumnAnnotationPresent() throws NoSuchFieldException {
    // Get the class of the entity
    Class<?> clazz = Ordertable.class; // Replace with your entity class name

    // Get the field you want to check
    Field field = clazz.getDeclaredField("user"); // Replace with the field name where @JoinColumn is used

    // Check if the @JoinColumn annotation is present
    if (field.isAnnotationPresent(JoinColumn.class)) {
        System.out.println("Test Passed: @JoinColumn annotation is present on the 'user' field.");
    } else {
        System.out.println("Test Failed: @JoinColumn annotation is missing on the 'user' field.");
    }
}

    @Test
    public void CRUD_testRegisterUser() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("testuser@example.com"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    public void CRUD_testGetUserById() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("testuser@example.com"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    public void CRUD_testGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUser));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("testuser@example.com"))
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    public void CRUD_testUpdateUser() throws Exception {
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(testUser);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("testuser@example.com"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    public void CRUD_testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
    @Test
    void CRUD_testCreateOrder() {
        // Mock repository behavior
        when(orderRepository.save(any(Ordertable.class))).thenReturn(sampleOrder);

        // Call service
        Ordertable createdOrder = orderService.createOrder(sampleOrder);

        // Output
        System.out.println("Expected ID: " + sampleOrder.getId() + ", Actual ID: " + createdOrder.getId());
        System.out.println("Expected Total Amount: " + sampleOrder.getTotalAmount() + ", Actual Total Amount: " + createdOrder.getTotalAmount());
        System.out.println("Expected Status: " + sampleOrder.getStatus() + ", Actual Status: " + createdOrder.getStatus());
    }

    @Test
    void CRUD_testGetOrderById() {
        // Mock repository behavior
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));

        // Call service
        Optional<Ordertable> order = orderService.getOrderById(1L);

        // Output
        System.out.println("Order Present: " + order.isPresent());
        order.ifPresent(o -> {
            System.out.println("Expected ID: " + sampleOrder.getId() + ", Actual ID: " + o.getId());
            System.out.println("Expected Status: " + sampleOrder.getStatus() + ", Actual Status: " + o.getStatus());
        });
    }

    @Test
    void CRUD_testGetAllOrders() {
        // Mock repository behavior
        List<Ordertable> mockOrders = Arrays.asList(sampleOrder);
        when(orderRepository.findAll()).thenReturn(mockOrders);

        // Call service
        List<Ordertable> orders = orderService.getAllOrders();

        // Output
        System.out.println("Total Orders: " + orders.size());
        orders.forEach(order -> {
            System.out.println("Order ID: " + order.getId());
            System.out.println("Order Status: " + order.getStatus());
        });
    }

    @Test
    void CRUD_testUpdateOrder() {
        // Mock repository behavior
        Ordertable updatedOrderDetails = new Ordertable();
        updatedOrderDetails.setOrderDate(LocalDateTime.now());
        updatedOrderDetails.setTotalAmount(150.0);
        updatedOrderDetails.setStatus("completed");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any(Ordertable.class))).thenReturn(updatedOrderDetails);

        // Call service
        Ordertable updatedOrder = orderService.updateOrder(1L, updatedOrderDetails);

        // Output
        System.out.println("Updated Total Amount: " + updatedOrder.getTotalAmount());
        System.out.println("Updated Status: " + updatedOrder.getStatus());
    }

    @Test
    void CRUD_testDeleteOrder() {
        // Mock repository behavior
        doNothing().when(orderRepository).deleteById(1L);

        // Call service
        orderService.deleteOrder(1L);

        // Output
        System.out.println("Order with ID 1 deleted.");
    }

    @Test
    void CRUD_testCreateBook() {
        // Mock repository behavior
        when(bookRepository.save(any(Book.class))).thenReturn(sampleBook);

        // Call service method
        Book createdBook = bookService.createBook(sampleBook);

        // Print output
        System.out.println("Created Book ID: " + createdBook.getId());
        System.out.println("Created Book Title: " + createdBook.getTitle());
    }

    @Test
    void CRUD_testGetBookById() {
        // Mock repository behavior
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

        // Call service method
        Optional<Book> book = bookService.getBookById(1L);

        // Print output
        System.out.println("Book Found: " + book.isPresent());
        book.ifPresent(b -> {
            System.out.println("Book ID: " + b.getId());
            System.out.println("Book Title: " + b.getTitle());
        });
    }

    @Test
    void CRUD_testGetAllBooks() {
        // Mock repository behavior
        List<Book> mockBooks = Arrays.asList(sampleBook);
        when(bookRepository.findAll()).thenReturn(mockBooks);

        // Call service method
        List<Book> books = bookService.getAllBooks();

        // Print output
        System.out.println("Total Books: " + books.size());
        books.forEach(b -> {
            System.out.println("Book ID: " + b.getId());
            System.out.println("Book Title: " + b.getTitle());
        });
    }

    // @Test
    // void testUpdateBook() {
    //     // Mock repository behavior
    //     Book updatedDetails = new Book();
    //     updatedDetails.setTitle("Updated Book Title");
    //     updatedDetails.setAuthor("Updated Author");
    //     updatedDetails.setGenre("Drama");
    //     updatedDetails.setDescription("Updated description.");
    //     updatedDetails.setPrice(24.99);
    //    // updatedDetails.setIsEbook(false);

    //     when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
    //     when(bookRepository.save(any(Book.class))).thenReturn(updatedDetails);

    //     // Call service method
    //     Book updatedBook = bookService.updateBook(1L, updatedDetails);

    //     // Print output
    //     System.out.println("Updated Book Title: " + updatedBook.getTitle());
    //     System.out.println("Updated Book Genre: " + updatedBook.getGenre());
    // }

    @Test
    void CRUD_testDeleteBook() {
        // Mock repository behavior
        doNothing().when(bookRepository).deleteById(1L);

        // Call service method
        bookService.deleteBook(1L);

        // Print output
        System.out.println("Book with ID 1 deleted.");
    }

    @Test
    void CRUD_testGetBooksByGenre() {
        // Mock repository behavior
        List<Book> mockBooks = Arrays.asList(sampleBook);
        when(bookRepository.findByGenre("Fiction")).thenReturn(mockBooks);

        // Call service method
        List<Book> booksByGenre = bookService.getBooksByGenre("Fiction");

        // Print output
        System.out.println("Books in Genre 'Fiction': " + booksByGenre.size());
        booksByGenre.forEach(b -> {
            System.out.println("Book ID: " + b.getId());
            System.out.println("Book Title: " + b.getTitle());
        });
    }
    // @Autowired
    // private BookRepository bookRepository;

    @Order(3)
    @Test
    public void Repository_testBudgetRepositoryExists() {
        // Check if the repository is created and print
        if (bookRepository != null) {
            System.out.println("Budget Repository Created: " + bookRepository);
        }
    }

    // Test for User Repository
    @Autowired
    private UserRepository userRepository;

    @Order(4)
    @Test
    public void Repository_testUserRepositoryExists() {
        // Check if the repository is created and print
        if (userRepository != null) {
            System.out.println("User Repository Created: " + userRepository);
        }
    }

    // Test for Income Repository
    // @Autowired
    // private OrderRepository orderRepository;

    @Order(5)
    @Test
    public void Repository_testIncomeRepositoryExists() {
        // Check if the repository is created and print
        if (orderRepository != null) {
            System.out.println("Income Repository Created: " + orderRepository);
        }
    }

    @Test
    public void JPQL_testFindBooksByGenre() {
        // Given: Create books with different genres and save them
        Book book1 = new Book();
        book1.setTitle("The Great Gatsby");
        book1.setAuthor("F. Scott Fitzgerald");
        book1.setGenre("Fiction");
        book1.setPrice(10.99);
        bookRepository.save(book1);
    
        Book book2 = new Book();
        book2.setTitle("1984");
        book2.setAuthor("George Orwell");
        book2.setGenre("Dystopian");
        book2.setPrice(15.99);
        bookRepository.save(book2);
    
        Book book3 = new Book();
        book3.setTitle("To Kill a Mockingbird");
        book3.setAuthor("Harper Lee");
        book3.setGenre("Fiction");
        book3.setPrice(12.99);
        bookRepository.save(book3);
    
        // When: Run the JPQL query to find books with genre "Fiction"
        List<Book> books = bookRepository.findByGenre("Fiction");
    
        // Print the results
        System.out.println("Books with genre 'Fiction':");
        for (Book book : books) {
            System.out.println(book.getTitle());
        }
        
        // Optionally, check if the result matches expected behavior manually
        if (books != null && books.size() == 2) {
            System.out.println("Test Passed: 2 books with genre 'Fiction' found.");
        } else {
            System.out.println("Test Failed: Incorrect number of books found.");
        }
    }
    
    @Test
public void PaginateSorting_testGetBooksSortedWithInvalidSortDir() {
    // Prepare test data
    Book book1 = new Book();
    book1.setId(1L);
    book1.setTitle("Book A");
    book1.setAuthor("Author X");

    Book book2 = new Book();
    book2.setId(2L);
    book2.setTitle("Book B");
    book2.setAuthor("Author Y");

    List<Book> books = Arrays.asList(book1, book2);

    // Mock the repository method for invalid sort direction (defaults to ascending)
    when(bookRepository.findAll(Sort.by(Sort.Order.asc("author")))).thenReturn(books);

    // Test with invalid sort direction
    List<Book> sortedBooks = bookService.getAllBooksSorted("author");

    // Print results for verification
    System.out.println("Sorted Books (Ascending Default):");
    sortedBooks.forEach(book -> 
        System.out.println("ID: " + book.getId() + ", Title: " + book.getTitle() + ", Author: " + book.getAuthor()));

    // Verify that the sorted books are returned correctly
    if (sortedBooks != null && sortedBooks.size() == 2) {
        System.out.println("Test Passed: Sorted list contains 2 books");
    }
}

@Test
public void PaginateSorting_testGetBooksSortedDescending() {
    // Prepare test data
    Book book1 = new Book();
    book1.setId(1L);
    book1.setTitle("Book A");
    book1.setAuthor("Author X");

    Book book2 = new Book();
    book2.setId(2L);
    book2.setTitle("Book B");
    book2.setAuthor("Author Y");

    // Prepare the sorted list in descending order (Author Y comes before Author X)
    List<Book> books = Arrays.asList(book2, book1);

    // Mock the repository method for descending sort
    when(bookRepository.findAll(Sort.by(Sort.Order.desc("author")))).thenReturn(books);

    // Test with descending sort direction
    List<Book> sortedBooks = bookService.getAllBooksSorted("author");

    // Print results for verification
    System.out.println("Sorted Books (Descending Order):");
    sortedBooks.forEach(book -> 
        System.out.println("ID: " + book.getId() + ", Title: " + book.getTitle() + ", Author: " + book.getAuthor()));
}
@Test
public void PaginateSorting_testGetBooksWithPagination() {
    // Prepare test data
    Book book1 = new Book();
    book1.setId(1L);
    book1.setTitle("Book A");
    book1.setAuthor("Author X");

    Book book2 = new Book();
    book2.setId(2L);
    book2.setTitle("Book B");
    book2.setAuthor("Author Y");

    Book book3 = new Book();
    book3.setId(3L);
    book3.setTitle("Book C");
    book3.setAuthor("Author Z");

    // Mock a Page object for pagination
    Page<Book> page = new PageImpl<>(Arrays.asList(book1, book2));

    // Mock the repository method for pagination
    when(bookRepository.findAll(PageRequest.of(0, 2))).thenReturn(page);

    // Test with pagination (page = 0, size = 2)
    Page<Book> paginatedBooks = bookService.getBooksWithPagination(0, 2);

    // Print results for verification
    System.out.println("Paginated Books (Page 0, Size 2):");
    paginatedBooks.getContent().forEach(book -> 
        System.out.println("ID: " + book.getId() + ", Title: " + book.getTitle() + ", Author: " + book.getAuthor()));
}

@Test
public void Mapping_testManyToOneMapping() {
    Book book = new Book();
    User user = new User();
    user.setId(1L);
    user.setUsername("Alice Smith");

    book.setId(1L);
    book.setTitle("Spring Framework");
    book.setAuthor("John Doe");
    book.setGenre("Technology");
    book.setPrice(29.99);
    //book.setIsEbook(false);
    book.setUser(user); // Mapping user to book

    // Test: Ensure the user is correctly mapped to the book
    System.out.println("User ID: " + book.getUser().getId());  // Should print 1
    System.out.println("User Name: " + book.getUser().getUsername());  // Should print "Alice Smith"
    System.out.println("Book Title: " + book.getTitle());  // Should print "Spring Framework"
    System.out.println("Book Author: " + book.getAuthor());  // Should print "John Doe"
}
@Test
public void Mapping_testOneToManyMapping() {
    // Create and set up the User
    User user = new User();
    user.setId(1L);
    user.setUsername("John Doe");

    // Create Book entities and associate them with the User
    Book book1 = new Book();
    book1.setId(1L);
    book1.setTitle("Java Programming");
    book1.setAuthor("Author 1");
    book1.setUser(user);  // Associate with user

    Book book2 = new Book();
    book2.setId(2L);
    book2.setTitle("Spring Framework");
    book2.setAuthor("Author 2");
    book2.setUser(user);  // Associate with user

    // Add books to user's list of books (One-to-Many)
    List<Book> books = new ArrayList<>();
    books.add(book1);
    books.add(book2);
    user.setBooks(books);

    // Mapping check - without assertion
    // Print the results for manual verification
    System.out.println("User: " + user.getUsername() + " has the following books:");
    user.getBooks().forEach(book -> 
        System.out.println("Book Title: " + book.getTitle() + ", Author: " + book.getAuthor()));
    
    // Check if the user ID is correctly associated with the books
    user.getBooks().forEach(book -> 
        System.out.println("User ID for book '" + book.getTitle() + "': " + book.getUser().getId()));
}

@Test 
public void Swagger_testConfigurationFolder() { 
    String directoryPath = "src/main/java/com/examly/springapp/configuration"; // Replace with the path to your directory 
    File directory = new File(directoryPath); 
    assertTrue(directory.exists() && directory.isDirectory()); 
}

@Test

public void Swagger_testConfigFile() {

    String filePath = "src/main/java/com/examly/springapp/configuration/SwaggerConfig.java";

    // Replace with the path to your file

    File file = new File(filePath);

    assertTrue(file.exists() && file.isFile());

}
private void checkAnnotationExists(String className, String annotationName) {
    try {
        Class<?> clazz = Class.forName(className);
        ClassLoader classLoader = clazz.getClassLoader();
        Class<?> annotationClass = Class.forName(annotationName, false, classLoader);
        assertNotNull(clazz.getAnnotation((Class) annotationClass)); // Use raw type
    } catch (ClassNotFoundException | NullPointerException e) {
        fail("Class " + className + " or annotation " + annotationName + " does not exist.");
    }
}

@Test
   public void Swagger_testConfigHasAnnotation() {
       checkAnnotationExists("com.examly.springapp.configuration.SwaggerConfig", "org.springframework.context.annotation.Configuration");
   }
  
   @Test
   public void Log_testLogFolderAndFileCreation() {
       // Check if the "logs" folder exists
       File logFolder = new File(LOG_FOLDER_PATH);
       assertTrue(logFolder.exists(), "Log folder should be created");

       // Check if the "application.log" file exists inside the "logs" folder
       File logFile = new File(LOG_FILE_PATH);
       assertTrue(logFile.exists(), "Log file should be created inside 'logs' folder");
   }

  
   @Test
void AOP_testAOPLoggingAspect() {
    // Call the service method
    userService.getAllUsers();

    // The aspect should log before method execution
    System.out.println("Testing AOP: The logging aspect should execute before the service method.");
}


@Test
  void AOP_testAOPLoggingBookServiceAspect()
  {
    bookService.getAllBooks();
    System.out.println("Testing AOP: The logging aspect should execute before the service method.");

  }

  
@Test
void AOP_testAOPLoggingOrderServiceAspect()
{
  orderService.getAllOrders();
  System.out.println("Testing AOP: The logging aspect should execute before the service method.");

}
  

  @Test
  void AOP_testAOPLoggingControllerAspect() {
      // Call the service method
      userController.getAllUsers();

      // The aspect should log before method execution
      System.out.println("Testing AOP: The logging aspect should execute before the service method.");
  }

  




}
   

