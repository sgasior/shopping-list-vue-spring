package pl.edu.kopalniakodu.exceptions;

public class OwnerNotFoundException extends RuntimeException {

    public OwnerNotFoundException(String id) {
        super("Owner with this id: " + id + " does not exists.");
    }

}
