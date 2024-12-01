package app.backendservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;


@Data
@Component
@ConfigurationProperties(prefix = "dir")
public class DirConfiguration
{
    private String temp;

    private String font;    
}
