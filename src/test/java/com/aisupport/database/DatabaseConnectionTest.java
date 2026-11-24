package com.aisupport.database;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectionTest {

    @Test
    public void testDatabaseConnection() throws Exception {

        try (Connection connection = DBConnection.getConnection()) {

            assertNotNull(connection);

            System.out.println("Database connection successful!");
            System.out.println(
                "Database: " +
                connection.getCatalog()
            );

            Statement statement = connection.createStatement();

            ResultSet result = statement.executeQuery(
                "SELECT DATABASE()"
            );

            assertTrue(result.next());

            String databaseName = result.getString(1);

            assertEquals(
                "customer_support_db",
                databaseName
            );

            System.out.println(
                "Connected Database: " + databaseName
            );

            result.close();
            statement.close();
        }
    }
}
