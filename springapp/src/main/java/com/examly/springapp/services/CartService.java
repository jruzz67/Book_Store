package com.examly.springapp.services;

import com.examly.springapp.entities.Book;
import com.examly.springapp.entities.Cart;
import com.examly.springapp.entities.CartItem;
import com.examly.springapp.entities.Ordertable;
import com.examly.springapp.entities.OrderItem;
import com.examly.springapp.entities.User;
import com.examly.springapp.repositories.BookRepository;
import com.examly.springapp.repositories.CartRepository;
import com.examly.springapp.repositories.OrderRepository;
import com.examly.springapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EmailService emailService;

    private static final int MAX_TOTAL_QUANTITY = 10;

    public Cart getOrCreateCart(User user) {
        if (user.getCart() == null) {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setTotalCost(0.0);
            cart.setCartItems(new ArrayList<>());
            user.setCart(cart);
            cartRepository.save(cart);
        }
        return user.getCart();
    }

    public Cart addBookToCart(User user, Long bookId, Integer quantity) {
        if (quantity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be positive");
        }

        Cart cart = getOrCreateCart(user);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        int currentTotalQuantity = cart.getCartItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        int newTotalQuantity = currentTotalQuantity + quantity;

        if (newTotalQuantity > MAX_TOTAL_QUANTITY) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot add more items. Maximum total quantity exceeded: " + MAX_TOTAL_QUANTITY);
        }

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(bookId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setBook(book);
            newItem.setQuantity(quantity);
            cart.getCartItems().add(newItem);
            // Ensure bidirectional relationship
            newItem.setCart(cart);
        }

        updateTotalCost(cart);
        Cart savedCart = cartRepository.save(cart);
        System.out.println("Cart saved with ID: " + savedCart.getId() + ", Items: " + savedCart.getCartItems().size());
        return savedCart;
    }

    public Cart removeBookFromCart(User user, Long bookId, Integer quantity) {
        if (quantity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity to remove must be positive");
        }

        Cart cart = getOrCreateCart(user);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        CartItem item = cart.getCartItems().stream()
                .filter(i -> i.getBook().getId().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found in cart"));

        int currentQuantity = item.getQuantity();
        if (quantity > currentQuantity) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot remove more quantity than present in the cart");
        }

        int newQuantity = currentQuantity - quantity;
        if (newQuantity > 0) {
            item.setQuantity(newQuantity);
        } else {
            cart.getCartItems().remove(item);
        }

        updateTotalCost(cart);
        return cartRepository.save(cart);
    }

    @Transactional
    public Ordertable checkoutCart(User user) {
        Cart cart = getOrCreateCart(user);
        if (cart.getCartItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        Ordertable order = new Ordertable();
        order.setUser(cart.getUser());
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cart.getTotalCost());
        order.setStatus("PENDING");
        order.setOrderItems(new ArrayList<>());
        order = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getBook().getPrice());
            orderItems.add(orderItem);
        }

        order.getOrderItems().addAll(orderItems);
        orderRepository.save(order);

        // Send email notification with detailed order information
        emailService.sendOrderConfirmation(user.getEmail(), order, user.getUsername());

        cart.getCartItems().clear();
        cart.setTotalCost(0.0);
        cartRepository.save(cart);

        return order;
    }

    @Transactional
    public Cart clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cart.getCartItems().clear();
        cart.setTotalCost(0.0);
        return cartRepository.save(cart);
    }

    private void updateTotalCost(Cart cart) {
        double total = cart.getCartItems().stream()
                .mapToDouble(item -> item.getBook().getPrice() * item.getQuantity())
                .sum();
        cart.setTotalCost(total);
    }
}