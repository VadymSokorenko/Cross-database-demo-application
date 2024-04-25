package com.example.demo.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Data
public class ColumnMappingProperties {

    private String id;
    private String username;
    private String name;
    private String surname;
}
