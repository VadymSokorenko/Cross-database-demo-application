package com.example.demo.config;

import lombok.Data;

@Data
public class DatabaseOptions {

    private String name;
    private String strategy;
    private String url;
    private String table;
    private String user;
    private String password;
    private ColumnMappingProperties mapping;
}