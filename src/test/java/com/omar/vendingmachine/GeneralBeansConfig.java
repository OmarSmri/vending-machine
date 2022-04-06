package com.omar.vendingmachine;

import com.omar.vendingmachine.service.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class GeneralBeansConfig {

    @Bean
    public CustomUserDetailService getCustomUserDetailService() {
        return new CustomUserDetailService();
    }
}
