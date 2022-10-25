package prgms.lecture.order_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import prgms.lecture.order_management.security.Jwt;
import prgms.lecture.order_management.security.JwtTokenConfigure;

@Configuration
public class ServiceConfigure {

    @Bean
    public Jwt jwt(JwtTokenConfigure jwtTokenConfigure) {
        return new Jwt(jwtTokenConfigure.getIssuer(), jwtTokenConfigure.getClientSecret(), jwtTokenConfigure.getExpirySeconds());
    }

}