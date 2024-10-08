package com.yo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	
	 
	@Autowired
	private  JwtAuthFilter jwtAuthFiter;

	
	@Bean
	UserDetailsService getUserDetailsService() {
		return new CustomUserDetailsService();
	}
	
	@Bean
	BCryptPasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	@Bean
	DaoAuthenticationProvider dProvider() {
		DaoAuthenticationProvider dProvider=new DaoAuthenticationProvider();
		dProvider.setUserDetailsService(getUserDetailsService());
		dProvider.setPasswordEncoder(encoder());
		return dProvider;
	}
	
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	   HttpSessionRequestCache reqCache=new HttpSessionRequestCache();
	   reqCache.setMatchingRequestParameterName(null);
	   http.csrf(csrf-> csrf.disable())
	   .authorizeHttpRequests(authHttpReq-> authHttpReq
			   .requestMatchers("/v3/api-docs/**","/swagger-ui/**","/student/api/v1/authenticate","/student/api/v1/signup").permitAll()
			   
			   .anyRequest().authenticated())
	   .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	   .requestCache(cache -> cache
               .requestCache(reqCache)
           );

       http.authenticationProvider(dProvider())
       .addFilterBefore(jwtAuthFiter, UsernamePasswordAuthenticationFilter.class );;
       
       return http.build();	
	}
	@Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
       return config.getAuthenticationManager();
   }

}
