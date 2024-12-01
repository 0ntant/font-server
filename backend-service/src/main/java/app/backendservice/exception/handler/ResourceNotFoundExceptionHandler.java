package app.backendservice.exception.handler;

import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import app.backendservice.exception.message.ApiError;
import app.backendservice.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.NoHandlerFoundException;


//без кастомного обработчика 
// {
//     "timestamp": "2023-07-06T08:35:03.700+00:00",
//     "status": 400,
//     "error": "Bad Request",
//     "path": "/api/v1/font-family"
// }

// {
//     "type": "about:blank",
//     "title": "Bad Request",
//     "status": 400,
//     "detail": "Failed to read request",
//     "instance": "/api/v1/font-family"
// }


@Slf4j
@ControllerAdvice
public class ResourceNotFoundExceptionHandler 
{
    @ExceptionHandler({ResourceNotFoundException.class, NoHandlerFoundException.class})
    protected ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) 
    {
        HttpStatus    requestStatus = HttpStatus.NOT_FOUND;
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
