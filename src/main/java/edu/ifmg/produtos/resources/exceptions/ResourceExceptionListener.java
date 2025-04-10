package edu.ifmg.produtos.resources.exceptions;

import edu.ifmg.produtos.services.exceptions.DatabaseException;
import edu.ifmg.produtos.services.exceptions.ResourceNotFound;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ResourceExceptionListener {

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<StandartError> resourceNotFound(ResourceNotFound e, HttpServletRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandartError error = new StandartError();
        error.setStatus(status.value());
        error.setMessage(e.getMessage());
        error.setError("Resource not found");
        error.setTimestamp(Instant.now());
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<StandartError> databaseException(DatabaseException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandartError error = new StandartError();
        error.setStatus(status.value());
        error.setMessage(e.getMessage());
        error.setError("Database exception");
        error.setTimestamp(Instant.now());
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }
}
