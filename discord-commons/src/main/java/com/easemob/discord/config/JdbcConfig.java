package com.easemob.discord.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class JdbcConfig {
    @ConfigurationProperties(prefix = "spring.datasource.entity", ignoreInvalidFields = true)
    @Bean(name = "discordJdbcConfigProperties")
    public JdbcConfigProperties discordJdbcConfigProperties() {
        return new JdbcConfigProperties();
    }

    @Bean(name = "discordDataSource")
    public HikariDataSource discordDataSource(@Qualifier("discordJdbcConfigProperties")
            JdbcConfigProperties jdbcProperties) {
        return manageDataSource(jdbcProperties);
    }

    @ConditionalOnMissingBean(name = "discordJdbcTemplate")
    @Bean(name = "discordJdbcTemplate")
    public JdbcTemplate discordJdbcTemplate(
            @Qualifier("discordDataSource") HikariDataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "discordTransactionManager")
    public PlatformTransactionManager discordTransactionManager(
            @Qualifier("discordDataSource") HikariDataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    private HikariDataSource manageDataSource(
            JdbcConfigProperties jdbcProperties) {

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(jdbcProperties.getDriverClassName());
        dataSource.setJdbcUrl(jdbcProperties.getUrl());
        dataSource.setUsername(jdbcProperties.getUsername());
        dataSource.setPassword(jdbcProperties.getPassword());
        dataSource.setMinimumIdle(jdbcProperties.getMinimumIdle());
        dataSource.setMaximumPoolSize(jdbcProperties.getMaximumPoolSize());
        return dataSource;
    }
}
