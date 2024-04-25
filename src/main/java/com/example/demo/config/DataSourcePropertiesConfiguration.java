package com.example.demo.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties()
public class DataSourcePropertiesConfiguration {

    private List<DatabaseOptions> dataSources;
}
