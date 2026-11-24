package com.aisupport.util;

import com.aisupport.model.User;
import com.aisupport.service.UserService;

import java.util.Scanner;

public class CreateAdmin {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        UserService userService = new UserService();

        try {

            System.out.println("========== CREATE ADMIN ACCOUNT ==========");

            System.out.print("Full Name: ");
            String fullName = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            System.out.print("Phone: ");
            String phone = scanner.nextLine();

            User admin = new User();

            admin.setFullName(fullName);
            admin.setEmail(email);
            admin.setPasswordHash(password);
            admin.setPhone(phone);
            admin.setRole("ADMIN");
            admin.setActive(true);

            userService.createAdmin(admin);

            System.out.println("\nAdmin account created successfully!");

        } catch (Exception e) {

            System.out.println("\nUnable to create admin account.");
            System.out.println(e.getMessage());

        } finally {
            scanner.close();
        }
    }
}
