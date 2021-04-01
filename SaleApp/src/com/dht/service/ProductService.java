/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dht.service;

import com.dht.pojo.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class ProductService {
    private Connection conn;
    
    public ProductService(Connection conn) {
        this.conn = conn;
    }
    
    public List<Product> getProducts(String kw) throws SQLException {
        if (kw == null)
            throw new SQLDataException();
        
        String sql = "SELECT * FROM product WHERE name like concat('%', ?, '%')";
        PreparedStatement stm = this.conn.prepareStatement(sql);
        stm.setString(1, kw);
        
        ResultSet rs = stm.executeQuery();
        List<Product> products = new ArrayList<>();
        while (rs.next()) {
            Product p = new Product();
            p.setId(rs.getInt("id"));
            p.setName(rs.getString("name"));
            p.setPrice(rs.getBigDecimal("price"));
            p.setCategoryId(rs.getInt("category_id"));
            
            products.add(p);
        }
        
        return products;
    }
    
    public Product getProductById(int productId) throws SQLException {
        String q = "SELECT * FROM product WHERE id=?";
        PreparedStatement stm = this.conn.prepareStatement(q);
        stm.setInt(1, productId);
        
        ResultSet rs = stm.executeQuery();
        
        Product p = null;
        while (rs.next()) {
            p = new Product();
            p.setId(rs.getInt("id"));
            p.setName(rs.getString("name"));
            break;
        }
        return p;
    }
    
    public boolean deleteProduct(int productId) {
        try {
            String sql = "DELETE FROM product WHERE id=?";
            PreparedStatement stm = this.conn.prepareStatement(sql);
            stm.setInt(1, productId);
            
            int row = stm.executeUpdate();
            
            return row > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(ProductService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    public boolean updateProduct(Product p) {
        try {
            String sql = "UPDATE product SET name=?, price=?, category_id=? WHERE id=?";
            PreparedStatement stm = this.conn.prepareStatement(sql);
            stm.setString(1, p.getName());
            stm.setBigDecimal(2, p.getPrice());
            stm.setInt(3, p.getCategoryId());
            stm.setInt(4, p.getId());
            
            int rows = stm.executeUpdate();
            
            return rows > 0;
        } catch (SQLException ex) {
            Logger.getLogger(ProductService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    public boolean addProduct(Product p) {
        try {
            String sql = "INSERT INTO product(name, price, category_id) VALUES(?, ?, ?)";
            PreparedStatement stm = this.conn.prepareStatement(sql);
            stm.setString(1, p.getName());
            stm.setBigDecimal(2, p.getPrice());
            stm.setInt(3, p.getCategoryId());
            
            int kq = stm.executeUpdate();
            
            return kq > 0;
        } catch (SQLException ex) {
            Logger.getLogger(ProductService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
}
