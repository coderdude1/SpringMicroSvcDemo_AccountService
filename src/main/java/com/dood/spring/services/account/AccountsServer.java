package com.dood.spring.services.account;

import com.dood.spring.services.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

import java.util.logging.Logger;

/**
 * Run as a micro-service, registering with the Discovery Server (Eureka).
 * <p>
 * Note that the configuration for this application is imported from
 * {@link AccountServiceApplication}. This is a deliberate separation of concerns
 * and allows the application to run:
 * <ul>
 * <li>Standalone - by executing {@link AccountServiceApplication#main(String[])}</li>
 * <li>As a microservice - by executing {@link AccountsServer#main(String[])}</li>
 * </ul>
 *
 * @author Paul Chapman
 */
@EnableAutoConfiguration //SpringBoot App
// this enables service registration and discovery. In this case, this process registers itself with the
// discovery-server service using its application name.  Look at the YML file for the config.
@EnableDiscoveryClient
@Import(AccountServiceApplication.class)// Import the account webapp
public class AccountsServer {

    @Autowired
    protected AccountRepository accountRepository;

    protected Logger logger = Logger.getLogger(AccountsServer.class.getName());

    /**
     * Run the application using Spring Boot and an embedded servlet engine.
     *
     * @param args
     *            Program arguments - ignored.
     */
    public static void main(String[] args) {
        // Tell server to look for accounts-server.properties or
        // accounts-server.yml.  Comment out if using spring config service????
        System.setProperty("spring.config.name", "accounts-server");//Scan the classpath for accounts-server.yml

        SpringApplication.run(AccountsServer.class, args);
    }
}