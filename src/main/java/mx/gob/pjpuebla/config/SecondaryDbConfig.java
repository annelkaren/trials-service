package mx.gob.pjpuebla.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "mx.gob.pjpuebla.migracion", // 📍 Apunta a los repositorys de la DB secundaria
        entityManagerFactoryRef = "secondaryEntityManagerFactory", transactionManagerRef = "secondaryTransactionManager")
public class SecondaryDbConfig {

    @Bean(name = "secondaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.secondary")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "secondaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("secondaryDataSource") DataSource dataSource) {

        // Propiedades específicas para el dialecto de MySQL
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

        return builder
                .dataSource(dataSource)
                .packages("mx.gob.pjpuebla.migracion") // 📍 Apunta a las entidades de la DB secundaria
                .persistenceUnit("secondary")
                .properties(properties)
                .build();
    }

    @Bean(name = "secondaryTransactionManager")
    public PlatformTransactionManager secondaryTransactionManager(
            @Qualifier("secondaryEntityManagerFactory") EntityManagerFactory  emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean(name = "secondaryNamedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate secondaryNamedParameterJdbcTemplate(
            @Qualifier("secondaryDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}