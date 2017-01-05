package com.van.data.syncer.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    //在Spring容器中，我们手工注解@Bean 将被优先加载，
    // 框架不会重新实例化其他的 DataSource 实现类。

    @Bean(name = "sourceDataSource")
    @Qualifier("sourceDataSource")
    @ConfigurationProperties(prefix="spring.datasource.src")
    public DataSource sourceDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "destinationDataSource")
    @Qualifier("destinationDataSource")
    @Primary
    @ConfigurationProperties(prefix="spring.datasource.des")
    public DataSource destinationDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "sourceJdbcTemplate")
    @Qualifier("sourceJdbcTemplate")
    public JdbcTemplate sourceJdbcTemplate(
            @Qualifier("sourceDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "destinationJdbcTemplate")
    @Qualifier("destinationJdbcTemplate")
    public JdbcTemplate destinationJdbcTemplate(
            @Qualifier("destinationDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}