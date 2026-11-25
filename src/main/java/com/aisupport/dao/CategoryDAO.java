package com.aisupport.dao;

import com.aisupport.database.DBConnection;
import com.aisupport.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public List<Category> getAllCategories() throws SQLException {

        String sql = """
                SELECT category_id, category_name
                FROM categories
                ORDER BY category_id
                """;

        List<Category> categories = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                categories.add(new Category(
                        result.getInt("category_id"),
                        result.getString("category_name")
                ));
            }
        }

        return categories;
    }
}