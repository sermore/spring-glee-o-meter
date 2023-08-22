package net.reliqs.gleeometer.security;

import net.reliqs.gleeometer.errors.CustomAccessDeniedHandler;
import net.reliqs.gleeometer.errors.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    public ResourceServerConfiguration(CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId("api");
    }

    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .securityMatcher("/api/**")
                .authorizeHttpRequests(registry -> registry
                        .requestMatchers("/api/signin**").permitAll()
                        .requestMatchers("/api/signin/**").permitAll()
                        .requestMatchers("/api/glee**").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers("/api/users**").hasAuthority("ADMIN")
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(registry -> registry
                    .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                );

        return http.build();
    }

}