package app.backendservice.exception.handler;

import app.backendservice.exception.message.ApiErrors;
import app.backendservice.exception.message.ValidateMessageError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.ZonedDateTime;
import java.util.Set;

@Slf4j
@ControllerAdvice
public class ConstraintViolationExceptionHandler
{
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolationExceptionHandler(ConstraintViolationException ex, WebRequest request)
    {
        HttpStatus requestStatus = HttpStatus.BAD_REQUEST;
        String        path       = request.getDescription(false).substring(4);
        ZonedDateTime timestamp  = ZonedDateTime.now();

        ApiErrors apiError = ApiErrors.builder()
                .timestamp(timestamp)
                .status(requestStatus.value())
                .errors(ex.getConstraintViolations()
                        .stream()
                        .map(constraintViolation ->
                                new ValidateMessageError(
                                        constraintViolation.getPropertyPath().toString(),
                                        constraintViolation.getMessage()))
                        .toList())
                .path(path)
                .build();
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        constraintViolations.forEach(constraintViolation ->
                log.warn(
                    "Field='{}' incorrect='{}' msg send='{}'",
                    constraintViolation.getPropertyPath(),
                    constraintViolation.getInvalidValue(),
                    constraintViolation.getMessage()
                )
        );
        return new ResponseEntity<>(apiError, requestStatus);
    }
}
