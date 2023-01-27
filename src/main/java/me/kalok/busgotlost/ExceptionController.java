package me.kalok.busgotlost;

import me.kalok.busgotlost.exception.InvalidCoordinateException;
import me.kalok.busgotlost.model.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(value = InvalidCoordinateException.class)
    public ResponseEntity<GenericResponse> invalidCoordinateException(InvalidCoordinateException exception) {
        return new ResponseEntity<>(new GenericResponse("Invalid number format"), HttpStatus.BAD_REQUEST);
    }
}
