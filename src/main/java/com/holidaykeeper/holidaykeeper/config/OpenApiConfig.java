package com.holidaykeeper.holidaykeeper.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Holiday Keeper API")
                        .version("1.0.0")
                        .description("전 세계 공휴일 데이터를 관리하는 REST API입니다.\n\n" +
                                "Nager.Date API를 활용하여 공휴일 데이터를 제공합니다.")
                );

    }
}
