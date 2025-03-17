package com.examly.springapp.services;

import com.examly.springapp.entities.Book;
import com.examly.springapp.entities.Cart;
import com.examly.springapp.entities.CartItem;
import com.examly.springapp.entities.Ordertable;
import com.examly.springapp.entities.User;
import com.examly.springapp.repositories.BookRepository;
import com.examly.springapp.repositories.CartRepository;
import com.examly.springapp.repositories.OrderRepository;
import com.examly.springapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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

    // Maximum total quantity allowed in cart
    private static final int MAX_TOTAL_QUANTITY = 10;

    /**
     * Get or create the default cart for a user.
     */
    public Cart getOrCreateDefaultCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return cartRepository.findByUserAndIsDefaultTrue(user)
                .orElseGet(() -> {
                    Cart defaultCart = new Cart();
                    defaultCart.setUser(user);
                    defaultCart.setTotalCost(0.0);
                    defaultCart.setCartItems(new ArrayList<>());
                    defaultCart.setDefault(true);
                    defaultCart.setName(null);
                    return cartRepository.save(defaultCart);
                });
    }

    /**
     * Get a specific cart for a user by cartId.
     */
    public Cart getCartById(Long userId, Long cartId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        if (!cart.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cart does not belong to user");
        }

        updateTotalCost(cart);
        return cart;
    }

    /**
     * Get all carts for a user.
     */
    public List<Cart> getCartsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return cartRepository.findByUser(user);
    }

    /**
     * Create a new additional cart for a user.
     */
    public Cart createCart(Long userId, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart name cannot be empty");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (cartRepository.findByUserAndName(user, name).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart name already exists for this user");
        }

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setTotalCost(0.0);
        cart.setCartItems(new ArrayList<>());
        cart.setDefault(false);
        cart.setName(name);

        return cartRepository.save(cart);
    }

    /**
     * Add a book to a cart.
     */
    public Cart addBookToCart(Long userId, Long cartId, Long bookId, Integer quantity, boolean isDefaultCart) {
        if (quantity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be positive");
        }

        Cart cart = isDefaultCart ? getOrCreateDefaultCart(userId) : getCartById(userId, cartId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        int currentTotalQuantity = cart.getCartItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        int newTotalQuantity = currentTotalQuantity + quantity;

        if (newTotalQuantity > MAX_TOTAL_QUANTITY) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot add more items. Maximum total quantity exceeded: " + MAX_TOTAL_QUANTITY);
        }

        // Check if the book is already in the cart
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
        }

        updateTotalCost(cart);
        return cartRepository.save(cart);
    }

    /**
     * Remove a book from a cart.
     */
    public Cart removeBookFromCart(Long userId, Long cartId, Long bookId, Integer quantity, boolean isDefaultCart) {
        if (quantity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity to remove must be positive");
        }

        Cart cart = isDefaultCart ? getOrCreateDefaultCart(userId) : getCartById(userId, cartId);
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

    /**
     * Checkout a cart.
     */
    public Ordertable checkoutCart(Long userId, Long cartId, boolean isDefaultCart) {
        Cart cart = isDefaultCart ? getOrCreateDefaultCart(userId) : getCartById(userId, cartId);
        if (cart.getCartItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        Ordertable order = new Ordertable();
        order.setUser(cart.getUser());
        // Convert CartItems to Books for now (simplified)
        order.setBooks(cart.getCartItems().stream()
                .map(CartItem::getBook)
                .toList());
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cart.getTotalCost());
        order.setStatus("PENDING");

        cart.setCartItems(new ArrayList<>());
        cart.setTotalCost(0.0);
        cartRepository.save(cart);

        return orderRepository.save(order);
    }

    /**
     * Clear a cart.
     */
    public Cart clearCart(Long userId, Long cartId, boolean isDefaultCart) {
        Cart cart = isDefaultCart ? getOrCreateDefaultCart(userId) : getCartById(userId, cartId);
        cart.setCartItems(new ArrayList<>());
        cart.setTotalCost(0.0);
        return cartRepository.save(cart);
    }

    /**
     * Delete a cart (only for additional carts).
     */
    public void deleteCart(Long userId, Long cartId) {
        Cart cart = getCartById(userId, cartId);
        if (cart.isDefault()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete the default cart");
        }

        cartRepository.delete(cart);
    }

    /**
     * Update the total cost of a cart.
     */
    private void updateTotalCost(Cart cart) {
        double total = cart.getCartItems().stream()
                .mapToDouble(item -> item.getBook().getPrice() * item.getQuantity())
                .sum();
        cart.setTotalCost(total);
    }
}