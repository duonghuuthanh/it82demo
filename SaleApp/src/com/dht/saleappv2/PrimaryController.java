package com.dht.saleappv2;

import com.dht.pojo.Category;
import com.dht.pojo.Product;
import com.dht.service.CategoryService;
import com.dht.service.JdbcUtils;
import com.dht.service.ProductService;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.crypto.AEADBadTagException;
import saleapp.Utils;

public class PrimaryController implements Initializable {
    @FXML private ComboBox<Category> cbCates;
    @FXML private TextField txtName;
    @FXML private TextField txtPrice;
    @FXML private TableView<Product> tbProducts;
    @FXML private TextField txtKeyWord;

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CategoryService s = new CategoryService();
        try {
            cbCates.setItems(FXCollections.observableList(s.getCates()));
        } catch (SQLException ex) {
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        loadColumns();
        try {
            loadProducts("");
        } catch (SQLException ex) {
            System.out.println("ERROR" + ex);
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        txtKeyWord.textProperty().addListener((obj) -> {
            try {
                loadProducts(txtKeyWord.getText());
            } catch (SQLException ex) {
                Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        this.tbProducts.setRowFactory(evt -> {
            TableRow row = new TableRow();
            row.setOnMouseClicked(e -> {
                try {
                    Product p = this.tbProducts.getSelectionModel().getSelectedItem();
                    txtName.setText(p.getName());
                    txtPrice.setText(p.getPrice().toString());
                    
                    CategoryService ser = new CategoryService();
                    cbCates.getSelectionModel().select(ser.getCategoryById(p.getCategoryId()));
                    
                } catch (SQLException ex) {
                    Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            return row;
        });
    }
    
    public void addHandler(ActionEvent evt) {
        try {
            Connection conn = JdbcUtils.getConn();
            ProductService p = new ProductService(conn);
            
            Product pro = new Product();
            pro.setName(txtName.getText());
            pro.setPrice(new BigDecimal(txtPrice.getText()));
            pro.setCategoryId(cbCates.getSelectionModel().getSelectedItem().getId());
            
            
            Alert a =new Alert(Alert.AlertType.INFORMATION);
            if (p.addProduct(pro) == true) 
                a.setContentText("SUCCESSFUL");
            else
                a.setContentText("FAILED");
            
            a.show();
            
        } catch (SQLException ex) {
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void updateHandler(ActionEvent evt) throws SQLException {
        Product p = this.tbProducts.getSelectionModel().getSelectedItem();
        p.setName(txtName.getText());
        p.setPrice(new BigDecimal(txtPrice.getText()));
        p.setCategoryId(this.cbCates.getSelectionModel().getSelectedItem().getId());
        
        Connection conn = JdbcUtils.getConn();
        ProductService s = new ProductService(conn);
        if (s.updateProduct(p) == true) {
            Utils.getBox("SUCCESSFUL", Alert.AlertType.INFORMATION).show();
            loadProducts("");
        } else
            Utils.getBox("FAILED", Alert.AlertType.ERROR).show();
        conn.close();
    }
    
    private void loadColumns() {
        TableColumn colId = new TableColumn("Id");
        colId.setCellValueFactory(new PropertyValueFactory("id"));

        TableColumn colName = new TableColumn("Name");
        colName.setCellValueFactory(new PropertyValueFactory("name"));
        
        TableColumn colPrice = new TableColumn("Price");
        colPrice.setCellValueFactory(new PropertyValueFactory("price"));
        
        TableColumn colAction = new TableColumn("ACTIONS");
        colAction.setCellFactory(obj -> {
            Button btn = new Button("Xoa");
            btn.setOnAction(evt -> {
                Utils.getBox("Ban chac chan xoa khong?", Alert.AlertType.CONFIRMATION)
                     .showAndWait().ifPresent(b -> {
                         if (b == ButtonType.OK) {
                             Button bt = (Button) evt.getSource();
                             TableCell cell = (TableCell) bt.getParent();
                             Product q = (Product)cell.getTableRow().getItem();
                             
                             Connection conn;
                             try {
                                 conn = JdbcUtils.getConn();
                                 ProductService s = new ProductService(conn);
                                 
                                 if (s.deleteProduct(q.getId())) {
                                     Utils.getBox("SUCCESSFUL", Alert.AlertType.INFORMATION).show();
                                     loadProducts("");
                                 } else
                                     Utils.getBox("FAILED", Alert.AlertType.ERROR).show();
                                 
                                 conn.close();
                             } catch (SQLException ex) {
                                 Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
                             }
                             
                             
                             
                         }
                    });
            });
            
            TableCell cell = new TableCell();
            cell.setGraphic(btn);
            return cell;
        });
        
        this.tbProducts.getColumns().addAll(colId, colName, colPrice, colAction);
    }
    
    private void loadProducts(String kw) throws SQLException {
        Connection conn = JdbcUtils.getConn();
        
        ProductService s = new ProductService(conn);
        
        tbProducts.setItems(FXCollections.observableArrayList(s.getProducts(kw)));
        
        conn.close();
    }
}
