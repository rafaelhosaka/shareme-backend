package com.rafaelhosaka.shareme.config;

import com.rafaelhosaka.shareme.filter.CustomAuthenticationFilter;
import com.rafaelhosaka.shareme.filter.CustomAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import java.util.List;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    private Environment environment;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        customAuthenticationFilter.setFilterProcessesUrl("/api/auth/login");

        http.csrf().disable();
        http.cors().configurationSource(request -> {
                var cors = new CorsConfiguration();
                cors.setAllowedOrigins(List.of(environment.getProperty("project.client.url")));
                cors.setAllowedMethods(List.of("GET","POST", "PUT", "DELETE", "OPTIONS"));
                cors.setAllowedHeaders(List.of("*"));
                cors.setAllowCredentials(true);
                return cors;
        });

        http.sessionManagement().sessionCreationPolicy(STATELESS);
        authorizeRequests(http);

        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }



    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }


    private void authorizeRequests(HttpSecurity http) throws Exception {
        //all
        http.authorizeRequests().antMatchers(getPermitAllPaths()).permitAll();

        //like
        http.authorizeRequests().antMatchers(PUT, "/api/like/post").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");

        //comment
        http.authorizeRequests().antMatchers(PUT, "/api/comment/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");

        //post
        http.authorizeRequests().antMatchers(GET, "/api/post/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");
        http.authorizeRequests().antMatchers(POST, "/api/post/upload").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");
        http.authorizeRequests().antMatchers(DELETE, "/api/post/delete").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");
        http.authorizeRequests().antMatchers(PUT, "/api/post/update").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");

        //message
        http.authorizeRequests().antMatchers(POST, "/api/message/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");

        //user
        http.authorizeRequests().antMatchers(GET, "/api/user/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");
        http.authorizeRequests().antMatchers(PUT, "/api/user/upload").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");
        http.authorizeRequests().antMatchers(PUT, "/api/user/uploadCoverImage").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");
        http.authorizeRequests().antMatchers(PUT, "/api/user/updateUser").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");

        //friend
        http.authorizeRequests().antMatchers(GET, "/api/friend/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");
        http.authorizeRequests().antMatchers(POST, "/api/friend/createRequest").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");
        http.authorizeRequests().antMatchers(DELETE, "/api/friend/deleteRequest").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");
        http.authorizeRequests().antMatchers(PUT, "/api/friend/unfriend").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");

        //notification
        http.authorizeRequests().antMatchers(GET, "/api/notification/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");
        http.authorizeRequests().antMatchers(PUT, "/api/notification/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");
        http.authorizeRequests().antMatchers(DELETE, "/api/notification/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");

        //product
        http.authorizeRequests().antMatchers(GET, "/api/product/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");
        http.authorizeRequests().antMatchers(POST, "/api/product/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");

        //chat
        http.authorizeRequests().antMatchers(GET, "/api/chat/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");
        http.authorizeRequests().antMatchers(PUT, "/api/chat/markAsRead").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");

        //group
        http.authorizeRequests().antMatchers(GET, "/api/group/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");
        http.authorizeRequests().antMatchers(POST, "/api/group/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");
        http.authorizeRequests().antMatchers(PUT, "/api/group/uploadCoverImage").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");

        //auth

        http.authorizeRequests().antMatchers(PUT, "/password/username").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");
        http.authorizeRequests().antMatchers(GET, "/api/auth/**").hasAnyAuthority("ROLE_ADMIN");

        http.authorizeRequests().anyRequest().authenticated();

    }

    public String[] getPermitAllPaths(){
        return new String[]{
                "/",
                "/error",
                "/api/registrationConfirm",
                "/api/resend/**",
                "/api/recovery/**",
                "/api/auth/login/**",
                "/api/auth/user/createAccount/**",
                "/api/auth/refresh/**",
                "/api/auth/password/token",
                "/api/post/download/**",
                "/api/user/save/**",
                "/ws/**"
                };
    }
}
