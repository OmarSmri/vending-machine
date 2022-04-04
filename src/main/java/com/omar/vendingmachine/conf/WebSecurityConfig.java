package com.omar.vendingmachine.conf;

import com.omar.vendingmachine.model.user.ERole;
import com.omar.vendingmachine.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    CustomUserDetailService userDetailsService;


    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().and()
                .antMatcher("/**").authorizeRequests().antMatchers("/user/signup").permitAll()
                .antMatchers(HttpMethod.POST, "/user/login").authenticated()
                .antMatchers(HttpMethod.GET, "/user/getAllSessions").authenticated()
                .antMatchers(HttpMethod.PUT, "/user/deposit/**").hasRole(ERole.BUYER.name())
                .antMatchers(HttpMethod.GET, "/product/**").permitAll()
                .antMatchers(HttpMethod.POST, "/product").hasRole(ERole.SELLER.name())
                .antMatchers(HttpMethod.PUT, "/product/**").hasRole(ERole.SELLER.name())
                .antMatchers(HttpMethod.DELETE, "/product/**").hasRole(ERole.SELLER.name())
                .antMatchers(HttpMethod.POST, "/product/buy/**").hasRole(ERole.BUYER.name())
                .and().csrf().disable().logout().logoutUrl("/user/logout").and().sessionManagement().maximumSessions(10);
    }
}
