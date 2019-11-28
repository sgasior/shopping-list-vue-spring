package pl.edu.kopalniakodu.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class MvcExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ApiError> validationErrorHandler(ConstraintViolationException ex) {
        return ex.getConstraintViolations()
                .stream()
                .map(err -> new ApiError(err.getPropertyPath().toString(), err.getMessage()))
                .collect(Collectors.toList());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                .getAllErrors().stream()
                .map(err -> new ApiError(err.getCodes(), err.getDefaultMessage()))
                .collect(Collectors.toList());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public List<ApiError> handleOtherException(Exception ex) {
        return Collections.singletonList(new ApiError(ex.getClass().getCanonicalName(), ex.getMessage()));
    }

    @ExceptionHandler(OwnerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<ApiError> ownerNotFoundExceptionHandler(OwnerNotFoundException ex) {
        return Collections.singletonList(new ApiError("owner.notfound", ex.getMessage()));
    }

    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<ApiError> taskNotFoundExceptionHandler(TaskNotFoundException ex) {
        return Collections.singletonList(new ApiError("task.notfound", ex.getMessage()));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<ApiError> productNotFoundExceptionHandler(ProductNotFoundException ex) {
        return Collections.singletonList(new ApiError("product.notfound", ex.getMessage()));
    }
}
