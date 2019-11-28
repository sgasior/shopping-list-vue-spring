package pl.edu.kopalniakodu.exceptions;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(int number) {
        super("Product with this number: " + number + " does not exists.");
    }

}
