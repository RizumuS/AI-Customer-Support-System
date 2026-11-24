package com.aisupport.util;

import com.aisupport.service.UserService;

import java.util.Scanner;

public class CreateAgent {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        UserService userService = new UserService();

        try {
            System.out.println("=== CREATE AGENT ACCOUNT ===");

            System.out.print("Full Name: ");
            String fullName = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("Password (minimum 8 characters): ");
            String password = scanner.nextLine();

            System.out.print("Phone: ");
            String phone = scanner.nextLine();

            System.out.print("Department: ");
            String department = scanner.nextLine();

            boolean created = userService.createAgent(
                fullName,
                email,
                password,
                phone,
                department
            );

            if (created) {
                System.out.println(
                    "\nAgent account created successfully!"
                );
            }

        } catch (Exception e) {
            System.out.println(
                "\nUnable to create agent: " + e.getMessage()
            );
        }
    }
}
