package com.example.CRMTicketing.config;

import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@RequiredArgsConstructor(onConstructor =@__(@Autowired) )
public class HibernateConfig {

    private final DataSource dataSource;

    @Bean
    public LocalSessionFactoryBean sessionFactory() {

        LocalSessionFactoryBean factory =
                new LocalSessionFactoryBean();

        factory.setDataSource(dataSource);

        factory.setPackagesToScan(
                "com.example.CRMTicketing.Entity");

        Properties props =
                new Properties();

        props.put(
                "hibernate.dialect",
                "org.hibernate.dialect.MySQLDialect");

        props.put(
                "hibernate.show_sql",
                "true");

        props.put(
                "hibernate.hbm2ddl.auto",
                "update");

        factory.setHibernateProperties(props);

        return factory;
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(
                sessionFactory);
    }
}