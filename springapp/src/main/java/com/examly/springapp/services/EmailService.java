package com.examly.springapp.services;

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

    public void sendOrderConfirmation(String to, Long orderId, Double totalAmount) {
        try {
            logger.info("Sending order confirmation email to: {}", to);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom("jairusgold0112@gmail.com");
            message.setSubject("Order Confirmation - Dynamic Online Book Store");
            message.setText("Thank you for your order!\n\nOrder ID: " + orderId + "\nTotal Amount: $" + totalAmount);
            mailSender.send(message);
            logger.info("Order confirmation email sent successfully to: {}", to);
        } catch (MailException e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}