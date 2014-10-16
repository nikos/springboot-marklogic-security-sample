package de.nava.mlsample;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.query.QueryManager;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.filter.HTTPDigestAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import de.nava.mlsample.domain.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.xml.bind.JAXBException;

/**
 * @author Niko Schmuck
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class MarkLogicSampleApplication {

    @Value("${marklogic.host}")
    private String host;

    @Value("${marklogic.port}")
    private int port;

    @Value("${marklogic.username}")
    private String username;

    @Value("${marklogic.password}")
    private String password;

    @Bean
    public DatabaseClient getDatabaseClient() {
        try {
            // TODO: is this really (still) required?
            // configure once before creating a client
            DatabaseClientFactory.getHandleRegistry().register(
                    JAXBHandle.newFactory(Product.class)
            );
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return DatabaseClientFactory.newClient(host, port, username, password,
                DatabaseClientFactory.Authentication.DIGEST);
    }

    @Bean
    public QueryManager getQueryManager() {
        return getDatabaseClient().newQueryManager();
    }

    @Bean
    public JSONDocumentManager getJSONDocumentManager() {
        return getDatabaseClient().newJSONDocumentManager();
    }

    @Bean
    public String getMarkLogicBaseURL() {
        return String.format("http://%s:%d", host, port);
    }

    @Bean
    public Client getJerseyClient() {
        Client client = Client.create();  // thread-safe
        client.addFilter(new LoggingFilter());
        client.addFilter(new HTTPDigestAuthFilter(username, password));
        return client;
    }


    /**
     * The entrance point to the sample application, starts Spring Boot.
     */
    public static void main(String[] args) throws Exception {
        SpringApplication.run(MarkLogicSampleApplication.class, args);
    }

}
