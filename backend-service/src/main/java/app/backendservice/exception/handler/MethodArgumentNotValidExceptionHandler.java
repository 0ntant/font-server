package app.backendservice.exception.handler;

import app.backendservice.exception.message.ApiErrors;
import app.backendservice.exception.message.ValidateMessageError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@ControllerAdvice
public class MethodArgumentNotValidExceptionHandler
{
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request)
    {
        HttpStatus requestStatus = HttpStatus.BAD_REQUEST;
        String        path       = request.getDescription(false).substring(4);
        ZonedDateTime timestamp  = ZonedDateTime.now();

        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrorList = result.getFieldErrors();

        ApiErrors apiError = ApiErrors.builder()
                .timestamp(timestamp)
                .status(requestStatus.value())
                .errors(fieldErrorList
                        .stream()
                        .map(fieldError ->
                                new ValidateMessageError(
                                        fieldError.getField(),
                                        fieldError.getDefaultMessage()))
                        .toList())
                .path(path)
                .build();

        fieldErrorList.forEach(fieldError ->
                log.warn(
                        "Field='{}' incorrect='{}' msg send='{}'",
                        fieldError.getField(),
                        fieldError.getRejectedValue(),
                        fieldError.getDefaultMessage()
                )
        );
        return new ResponseEntity<>(apiError, requestStatus);
    }
}
