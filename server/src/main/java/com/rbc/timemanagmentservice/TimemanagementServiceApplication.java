package com.rbc.timemanagmentservice;

import com.rbc.timemanagmentservice.util.StartupUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Arrays;

@SpringBootApplication
public class TimemanagementServiceApplication {
    private static final Logger LOG = LoggerFactory.getLogger(TimemanagementServiceApplication.class);

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(TimemanagementServiceApplication.class, args);
    }


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/customers").allowedOrigins("http://localhost:8888");
            }
        };
    }


    @Bean
    @Transactional(propagation = Propagation.REQUIRED)
    public CommandLineRunner demo(StartupUtility startupUtility) {
        return (args) -> {
            if(environment.getActiveProfiles().length != 0 && Arrays.asList(environment.getActiveProfiles()).contains("runtime")){
                startupUtility.init();
            }
        };
    }



    //    @Bean
//    public ObjectMapper myObjectMapper(Jackson2ObjectMapperBuilder builder) {
////
////        return builder
////                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
////                .build()
//////                .registerModule(doubleModule)
////                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
//        return builder.build();//.registerModule(new JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//    }
//    @Bean
//    public Jackson2ObjectMapperBuilder jacksonBuilder() {
//        Jackson2ObjectMapperBuilder b = new Jackson2ObjectMapperBuilder();
//        b.indentOutput(true).dateFormat(new SimpleDateFormat("yyyy-MM-dd"));b.a
//        return b;
//    }


}

