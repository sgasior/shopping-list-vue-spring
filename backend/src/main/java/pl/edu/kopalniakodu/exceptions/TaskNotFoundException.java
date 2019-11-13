package pl.edu.kopalniakodu.exceptions;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(int number) {
        super("Task with this number: " + number + " does not exists.");
    }

}
