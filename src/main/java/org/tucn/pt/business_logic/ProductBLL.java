package org.tucn.pt.business_logic;

import org.tucn.pt.business_logic.validators.StockValidator;
import org.tucn.pt.business_logic.validators.Validator;
import org.tucn.pt.data_access.ProductDAO;
import org.tucn.pt.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Encapsulates the business logic and validation rules for Product operations.
 */
public class ProductBLL {
    private List<Validator<Product>> validators;
    private ProductDAO productDAO;

    public ProductBLL(){
        validators = new ArrayList<Validator<Product>>();
        validators.add(new StockValidator());
        productDAO = new ProductDAO();
    }

    /**
     * Retrieves a specific product from the database by its ID.
     *
     * @param id The unique identifier of the product.
     * @return The Product object containing the retrieved data.
     * @throws NoSuchElementException if the product is not found.
     */
    public Product findProductById(int id){
        Product prod = productDAO.findById(id);
        if(prod == null){
            throw new NoSuchElementException("Product with id: " + id + " was not found");
        }
        return prod;
    }

    /**
     * Retrieves a list of all products stored in the database.
     *
     * @return A list of Product objects.
     */
    public List<Product> findAllProducts() { return productDAO.findAll(); }

    /**
     * Validates the provided product data and inserts it as a new record in the database.
     *
     * @param prod The product to insert.
     * @return The inserted product object.
     * @throws IllegalArgumentException if validation fails.
     */
    public Product insertProduct(Product prod){
        validators.forEach(v -> v.validate(prod));
        return productDAO.insert(prod);
    }

    /**
     * Validates the provided product data and updates its existing record in the database.
     *
     * @param prod The product containing updated information.
     * @return The updated product object.
     * @throws IllegalArgumentException if validation fails.
     */
    public Product updateProduct(Product prod){
        validators.forEach(v -> v.validate(prod));
        return productDAO.update(prod);
    }

    /**
     * Deletes a product from the database.
     *
     * @param id The unique identifier of the product to delete.
     */
    public void deleteProduct(int id) { productDAO.delete(id); }

    /**
     * Retrieves a list of all products that currently have a stock greater than zero,
     * sorted by price in ascending order.
     *
     * @return A filtered and sorted list of available products.
     */
    public List<Product> getProductsInStock(){
        return productDAO.findAll().stream()
                .filter(p -> p.getStock() > 0)
                .sorted((a, b) -> Double.compare(a.getPrice(), b.getPrice()))
                .collect(Collectors.toList());
    }
}


