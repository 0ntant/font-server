package app.backendservice.exception;


public class ResourceNotValidatedException extends RuntimeException
{

    public ResourceNotValidatedException(String message)
    {
        super(message);
    }

    
    public ResourceNotValidatedException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
