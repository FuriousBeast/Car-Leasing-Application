package com.trimble.trimblecars;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.trimble.trimblecars"}) //For Junit Test
public class TrimbleCarsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrimbleCarsApplication.class, args);
    }

}
