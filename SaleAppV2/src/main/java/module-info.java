module com.dht.saleappv2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    
    opens com.dht.saleappv2 to javafx.fxml;
    exports com.dht.saleappv2;
    exports com.dht.pojo;
}
