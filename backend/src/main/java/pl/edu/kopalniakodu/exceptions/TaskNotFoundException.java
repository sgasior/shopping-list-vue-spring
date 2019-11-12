package pl.edu.kopalniakodu.exceptions;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String id) {
        super("Task with this id: " + id + " does not exists.");
    }

}
