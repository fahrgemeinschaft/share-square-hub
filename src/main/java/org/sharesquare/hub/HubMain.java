package org.sharesquare.hub;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"org.sharesquare"})
public class HubMain {

    public static void main(String[] args) {
        SpringApplication.run(HubMain.class, args);
    }

}