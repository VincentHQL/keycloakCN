package org.vincenthql.keycloak;

import org.vincenthql.keycloak.config.KeycloakServerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(
        scanBasePackages = {
                "org.vincenthql.keycloak.**"
        },
        exclude = {
                LiquibaseAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class,
                JerseyAutoConfiguration.class,
                GroovyTemplateAutoConfiguration.class,
                JmxAutoConfiguration.class,
                DataSourceAutoConfiguration.class,
                MongoAutoConfiguration.class,
                MongoDataAutoConfiguration.class,
                CassandraAutoConfiguration.class,
                DataSourceTransactionManagerAutoConfiguration.class,
                RedisRepositoriesAutoConfiguration.class
        }
)
@EnableConfigurationProperties(KeycloakServerProperties.class)
public class KeyCloakMain {

    public static void main(String[] args) {
        try {
            SpringApplication.run(KeyCloakMain.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> onApplicationReadyEventListener(ServerProperties serverProperties,
                                                                               KeycloakServerProperties keycloakServerProperties) {

        return (evt) -> {

            Integer port = serverProperties.getPort();
            String keycloakContextPath = keycloakServerProperties.getContextPath();

            System.out.printf("Embedded Keycloak started: http://localhost:%s%s to use keycloak%n", port, keycloakContextPath);
        };
    }
}
