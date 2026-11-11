package org.tucn.pt.business_logic.validators;

import org.tucn.pt.model.Product;

/**
 * Validates that a product's stock and price are not negative values.
 */
public class StockValidator implements Validator<Product> {

    /**
     * Validates the stock and price of the given product.
     *
     * @param p The product to validate.
     * @throws IllegalArgumentException if the stock or price is negative.
     */
    public void validate(Product p){
        if (p.getStock() < 0){
            throw new IllegalArgumentException("Product stock cannot be negative");
        }

        if(p.getPrice() < 0){
            throw new IllegalArgumentException("Product price cannot be negative");
        }
    }
}
