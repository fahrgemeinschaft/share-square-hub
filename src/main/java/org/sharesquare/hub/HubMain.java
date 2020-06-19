package org.sharesquare.hub;


import org.sharesquare.hub.configuration.SystemConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"org.sharesquare"})
@EnableConfigurationProperties(SystemConfiguration.class)
public class HubMain {

    public static void main(String[] args) {
        SpringApplication.run(HubMain.class, args);
    }

}