package com.codecool.shop.dao.implementation;

import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductDaoDB implements ProductDao, Queryhandler {

    private String connection_config_path = "src/main/resources/connection.properties";

    public ProductDaoDB() {}
    
    public ProductDaoDB(String configPath) {
        this.connection_config_path = configPath;
    }

    @Override
    public void add(Product product) {

        String query = "INSERT INTO products (name, description, default_price, default_currency, product_category, supplier)" +
                "VALUES (?, ?, ?, ?, ?, ?);";

        List<Object> parameters = new ArrayList<>();
        parameters.add(product.getName());
        parameters.add(product.getDescription());
        parameters.add(product.getDefaultPrice());
        parameters.add(product.getDefaultCurrency().toString());
        parameters.add(product.getProductCategory().getId());
        parameters.add(product.getSupplier().getId());

        executeDMLQuery(query, parameters);
    }

    @Override
    public Product find(int id) {

        String query = "SELECT id, name, description, default_price, default_currency, product_category, supplier " +
                "FROM products " +
                "WHERE id= ?;";
        List<Object> parameters = new ArrayList<>();
        parameters.add(id);
        List<Map<String, Object>> results = executeSelectQuery(query, parameters);
        return buildProductsList(results).get(0);

    }

    @Override
    public void remove(int id) {
        String query = "DELETE FROM products\n" +
                "WHERE id= ?;";
        List<Object> parameters = new ArrayList<>();
        parameters.add(id);
        executeDMLQuery(query, parameters);
    }

    @Override
    public List<Product> getAll() {
        String query = "SELECT id, name, description, default_price, default_currency, product_category, supplier " +
                "FROM products;";
        List<Map<String, Object>> results = executeSelectQuery(query);
        return buildProductsList(results);
    }

    @Override
    public List<Product> getBy(Supplier supplier) {
        String query = "SELECT id, name, description, default_price, default_currency, product_category, supplier " +
                "FROM products WHERE supplier = ?;";
        List<Object> parameters = new ArrayList<>();
        int supplierId = supplier.getId();
        parameters.add(supplierId);
        List<Map<String, Object>> results = executeSelectQuery(query, parameters);
        return buildProductsList(results);
    }

    @Override
    public List<Product> getBy(ProductCategory productCategory) {
        String query = "SELECT id, name, description, default_price, default_currency, product_category, supplier " +
                "FROM products WHERE product_category = ?;";
        List<Object> parameters = new ArrayList<>();
        int productCategoryId = productCategory.getId();
        parameters.add(productCategoryId);
        List<Map<String, Object>> results = executeSelectQuery(query, parameters);
        return buildProductsList(results);
    }

    public int numberOfProducts() {
        String query = "SELECT COUNT(id) as count FROM products;";
        List<Map<String, Object>> results = executeSelectQuery(query);
        return Integer.parseInt(results.get(0).get("count").toString());
    }

    public void removeAllProducts() {
        String query = "DELETE from products WHERE 1=1;";
        executeDMLQuery(query);
    }

    private List<Product> buildProductsList(List<Map<String, Object>> results) {
        List<Product> products = new ArrayList<>();
        for (Map<String, Object> result : results) {
            int id = (int) result.get("id");
            String name = (String) result.get("name");
            String description = (String) result.get("description");
            int defaultPrice = (int) result.get("default_price");
            String defaultCurrency = (String) result.get("default_currency");
            int productCategoryId = (int) result.get("product_category");
            int supplierId = (int) result.get("supplier");
            ProductCategory productCategory = new ProductCategoryDaoDB(connection_config_path).find(productCategoryId);
            Supplier supplier = new SupplierDaoDB().find(supplierId);
            Product product = new Product(name, defaultPrice, defaultCurrency, description, productCategory, supplier);
            product.setId(id);
            products.add(product);
        }
//        Field[] fields = Product.class.getDeclaredFields();
//        fields[0].getName()
        return products;
    }

    @Override
    public String getConnectionConfigPath() {
        return connection_config_path;
    }

}
