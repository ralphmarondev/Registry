package com.ralphmarondev.registry.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity, jwtFilter: JwtFilter): SecurityFilterChain {
        http.csrf { it.disable() }
            .cors { }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/account/login/").permitAll()
                // Role endpoints
                auth.requestMatchers(HttpMethod.GET, "/role/**").authenticated()
                auth.requestMatchers(HttpMethod.POST, "/role/**").hasRole(RoleConstants.ADMINISTRATOR)
                auth.requestMatchers(HttpMethod.PUT, "/role/**").hasRole(RoleConstants.ADMINISTRATOR)
                auth.requestMatchers(HttpMethod.DELETE, "/role/**").hasRole(RoleConstants.ADMINISTRATOR)

                // Account endpoints
                auth.requestMatchers("/account/register/").hasAnyRole(RoleConstants.ADMINISTRATOR, RoleConstants.STAFF)
                auth.requestMatchers("/account/batch/").hasAnyRole(RoleConstants.ADMINISTRATOR, RoleConstants.STAFF)
                auth.requestMatchers("/account/**").authenticated()

                auth.anyRequest().authenticated()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}