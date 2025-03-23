package com.examly.springapp.services;

import com.examly.springapp.entities.Ordertable;
import com.examly.springapp.entities.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendOrderConfirmation(String to, Ordertable order, String username) {
        try {
            logger.info("Sending order confirmation email to: {}", to);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom("jairusgold0112@gmail.com");
            message.setSubject("Your Order Confirmation - Dynamic Online Book Store");

            // Construct the email body
            StringBuilder emailBody = new StringBuilder();
            
            // Greeting
            emailBody.append("Dear ").append(username).append(",\n\n");

            // Thank-you message
            emailBody.append("Thank you for ordering with Dynamic Online Book Store! We’re excited to confirm your purchase.\n\n");

            // Order details
            emailBody.append("Order Details:\n");
            emailBody.append("Order ID: ").append(order.getId()).append("\n");
            emailBody.append("Items Ordered:\n");
            for (OrderItem item : order.getOrderItems()) {
                emailBody.append("- ")
                        .append(item.getBook().getTitle())
                        .append(" (Qty: ")
                        .append(item.getQuantity())
                        .append(", Price: $")
                        .append(item.getPrice())
                        .append(")\n");
            }
            emailBody.append("Total Amount: $").append(order.getTotalAmount()).append("\n\n");

            // Payment details
            emailBody.append("Payment Details:\n");
            emailBody.append("Payment Method: Paid via Credit Card\n"); // Placeholder, can be dynamic if payment method is stored
            emailBody.append("Amount Paid: $").append(order.getTotalAmount()).append("\n\n");

            // Company information
            emailBody.append("About Us:\n");
            emailBody.append("Dynamic Online Book Store - Your trusted source for books since 2025. " +
                            "We offer a wide range of books to cater to all your reading needs.\n\n");

            // Closing
            emailBody.append("We’ll notify you once your order is shipped.\n\n");
            emailBody.append("Best regards,\n");
            emailBody.append("Dynamic Book Store Team");

            message.setText(emailBody.toString());
            mailSender.send(message);
            logger.info("Order confirmation email sent successfully to: {}", to);
        } catch (MailException e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}