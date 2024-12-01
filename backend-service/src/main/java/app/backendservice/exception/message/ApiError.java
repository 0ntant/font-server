package app.backendservice.exception.message;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@AllArgsConstructor
@Builder
public class ApiError
{

    // {
    //     "timestamp": "2023-07-06T08:35:03.700+00:00",
    //     "status": 400,
    //     "error": "Bad Request",
    //     "path": "/api/v1/font-family"
    // }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime timestamp;  
    private int status;
    private String error;
    private String path;   
}
