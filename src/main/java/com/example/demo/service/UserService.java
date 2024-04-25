package com.example.demo.service;

import com.example.demo.config.DataSourcePropertiesConfiguration;
import com.example.demo.config.DatabaseOptions;
import com.example.demo.entity.UserEntity;
import com.example.demo.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    public static final String DATABASE_ERROR_MESSAGE = "Exception occurred while querying database name: {}, url: {}. Error message: [{}]";

    private final DataSourcePropertiesConfiguration dataSourcePropertiesConfiguration;

    public List<User> getUsers(String id, String username, String name, String surname) {
        List<UserEntity> users = new ArrayList<>();

        dataSourcePropertiesConfiguration.getDataSources().forEach(databaseOptions -> {
            StringBuilder queryTemplate = new StringBuilder();
            MapSqlParameterSource queryParams = new MapSqlParameterSource();
            String query = prepareQuery(id, username, name, surname, queryTemplate, databaseOptions, queryParams);
            List<UserEntity> result = queryDatabase(databaseOptions, query, queryParams);
            users.addAll(result);
        });

        return users.stream().map(this::map).collect(Collectors.toList());
    }

    private String prepareQuery(String id, String username, String name, String surname, StringBuilder query,
            DatabaseOptions databaseOptions, MapSqlParameterSource queryParams) {
        query.append("SELECT * FROM ");
        query.append(databaseOptions.getTable());
        query.append(" WHERE 1=1");

        if (Objects.nonNull(id) || Objects.nonNull(username) || Objects.nonNull(name) || Objects.nonNull(surname)) {
            if (Objects.nonNull(id)) {
                query.append(" AND ")
                        .append(databaseOptions.getMapping().getId())
                        .append("= :id");
                queryParams.addValue("id", id);
            }
            if (Objects.nonNull(username)) {
                query.append(" AND ")
                        .append(databaseOptions.getMapping().getUsername())
                        .append("= :username");
                queryParams.addValue("username", username);
            }
            if (Objects.nonNull(name)) {
                query.append(" AND ")
                        .append(databaseOptions.getMapping().getName())
                        .append("= :name");
                queryParams.addValue("name", name);
            }
            if (Objects.nonNull(surname)) {
                query.append(" AND ")
                        .append(databaseOptions.getMapping().getSurname())
                        .append("= :surname");
                queryParams.addValue("surname", surname);
            }
        }

        return query.toString();
    }

    private List<UserEntity> queryDatabase(DatabaseOptions databaseOptions, String query, MapSqlParameterSource queryParams) {
        List<UserEntity> queryResult = new ArrayList<>();
        try {
            queryResult = getJdbcTemplate(databaseOptions).query(
                    query,
                    queryParams,
                    (rs, rowNum) -> new UserEntity(
                            rs.getString(databaseOptions.getMapping().getId()),
                            rs.getString(databaseOptions.getMapping().getUsername()),
                            rs.getString(databaseOptions.getMapping().getName()),
                            rs.getString(databaseOptions.getMapping().getSurname())));
        } catch (BadSqlGrammarException ignore) {
        } catch (Exception e) {
            log.info(DATABASE_ERROR_MESSAGE, databaseOptions.getName(),
                    databaseOptions.getUrl(), e.getMessage());
        }

        return queryResult;
    }

    private NamedParameterJdbcTemplate getJdbcTemplate(DatabaseOptions databaseOptions) {
        return new NamedParameterJdbcTemplate(populateDatasource(databaseOptions));
    }

    private DriverManagerDataSource populateDatasource(DatabaseOptions databaseOptions) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(databaseOptions.getUrl());
        dataSource.setUsername(databaseOptions.getUser());
        dataSource.setPassword(databaseOptions.getPassword());

        return dataSource;
    }

    private User map(UserEntity userEntity) {
        User user = new User();
        user.setId(userEntity.id());
        user.setUsername(userEntity.username());
        user.setName(userEntity.name());
        user.setSurname(userEntity.surname());

        return user;
    }
}
