package app.backendservice.exception.message;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ValidateMessageError
{
    private final String field;

    private final String text;
}
