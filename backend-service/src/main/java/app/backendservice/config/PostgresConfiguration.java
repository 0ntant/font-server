package app.backendservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;


@Configuration
public class PostgresConfiguration
{
    @Value("${datasource.maximumPoolSize}")
    private int maximumPoolSize;
    @Value("${datasource.driverClassName}")
    private String driverClassName;

    @Value("${datasource.jdbcUrl}")
    private String jdbcUrl;

    @Value("${datasource.username}")
    private String username;

    @Value("${datasource.password}")
    private String password;

    @Bean
    public DataSource dataSource()
    {
        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setMaximumPoolSize(maximumPoolSize);
        dataSource.setDriverClassName(driverClassName);
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }
}

