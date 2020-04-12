package org.leucam.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class LeucamAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeucamAdminApplication.class, args);
    }

}
