package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@SpringBootTest
@ComponentScan(basePackages = {
        "com.example.demo.config", // 包含SecurityConfig
        "com.example.demo.service" // 包含CustomUserDetailsService
})
class DemoApplicationTests {

    @Test
    void contextLoads() {
    }

}
