package com.bsrvch.arbita;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ArbitaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArbitaApplication.class, args);
    }
//    @Bean
//    public CommandLineRunner CommandLineRunnerBean() {
//        return (args) -> {
//            System.out.println("In CommandLineRunnerImpl ");
//
//            for (String arg : args) {
//                System.out.println(arg);
//            }
//        };
//    }
}
