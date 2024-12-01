package app.backendservice.exception.handler;

import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import app.backendservice.exception.message.ApiError;
import app.backendservice.exception.ResourceNotValidatedException;

import lombok.extern.slf4j.Slf4j;



@Slf4j
@ControllerAdvice
public class ResourceNotValidatedExceptionHandler 
{

    
    @ExceptionHandler(ResourceNotValidatedException.class)
    protected ResponseEntity<Object> handleResourceNotValidatedExceptionHandler (ResourceNotValidatedException ex, WebRequest request)
    {
        HttpStatus    requestStatus = HttpStatus.UNPROCESSABLE_ENTITY;
        String        path          = request.getDescription(false).substring(4);
        ZonedDateTime timestamp     = ZonedDateTime.now();

        ApiError apiError = ApiError.builder()
            .timestamp(timestamp)
            .status(requestStatus.value())
            .error(ex.getMessage())
            .path(path)
            .build();

        log.warn(ex.getMessage());
        return new ResponseEntity<>(apiError, requestStatus);
    }


}
