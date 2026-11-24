package com.aisupport.service;

import com.aisupport.model.User;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void testCustomerRegistrationAndLogin() throws Exception {

        UserService userService = new UserService();

        // Generate a unique email for every test execution
        String testEmail = "testcustomer-"
                + UUID.randomUUID()
                + "@example.com";

        String testPassword = "TestPassword123!";

        // Create customer account
        User user = new User();

        user.setFullName("Test Customer");
        user.setEmail(testEmail);
        user.setPasswordHash(testPassword);
        user.setRole("CUSTOMER");
        user.setPhone("9876543210");

        // Register customer
        boolean registered = userService.registerUser(user);

        assertTrue(registered, "Customer registration should succeed.");

        // Verify that the customer can log in
        User loggedInUser = userService.login(
                testEmail,
                testPassword
        );

        assertNotNull(
                loggedInUser,
                "Login should succeed with correct credentials."
        );

        assertEquals("Test Customer", loggedInUser.getFullName());
        assertEquals(testEmail, loggedInUser.getEmail());
        assertEquals("CUSTOMER", loggedInUser.getRole());

        // Password hash should not be exposed after login
        assertNull(
                loggedInUser.getPasswordHash(),
                "Password hash should be cleared after login."
        );

        // Verify incorrect password is rejected
        User invalidLogin = userService.login(
                testEmail,
                "WrongPassword123!"
        );

        assertNull(
                invalidLogin,
                "Login should fail with an incorrect password."
        );

        System.out.println("Customer registration successful!");
        System.out.println("Customer Email: " + testEmail);
        System.out.println("Customer login successful!");
        System.out.println("Incorrect password rejected successfully!");
    }
}
