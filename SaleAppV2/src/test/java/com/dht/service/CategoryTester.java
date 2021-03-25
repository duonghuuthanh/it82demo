/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dht.service;

import com.dht.pojo.Category;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Admin
 */
public class CategoryTester {
    @Test
    public void testQuantity() throws SQLException {
        List<Category> cates = new CategoryService().getCates();
        Assertion.assertTrue(cates.size() >= 3);
    }
//    
//    public void testUnique() throws SQLException {
//        List<Category> cates = new CategoryService().getCates();
//        
//        List<String> names = new ArrayList<>();
//        cates.forEach(c -> names.add(c.getName()));
//        
//        Set<String> temp = new HashSet<>(names);
//        
//        Assertion.assertEquals(names.size(), temp.size());
//    }
}
