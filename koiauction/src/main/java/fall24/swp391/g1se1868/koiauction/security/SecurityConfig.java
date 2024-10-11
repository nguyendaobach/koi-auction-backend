package fall24.swp391.g1se1868.koiauction.security;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.filters.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    UserDetailsService userDetailService;

    @Autowired
     JwtFilter jwtFilter;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAuthenticationEntryPoint authenticationEntryPoint, CustomAccessDeniedHandler accessDeniedHandler) throws Exception {
        http
                .cors(cors -> cors.disable())  // Customize as needed
                .csrf(csrf -> csrf.disable())  // Disable CSRF for stateless APIs
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(HttpMethod.POST, "/api/koi-fish/customize").hasRole("BREEDER")
                        .requestMatchers(HttpMethod.POST, "/api/auction/add-auction").hasRole("BREEDER")
                        .requestMatchers(HttpMethod.POST, "/api/koi-types/add-koitype").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/koi-types/delete/{id}").hasRole("ADMIN")
                        .requestMatchers("/api/auction/approve-auction/").hasRole("STAFF")
                        .requestMatchers("/api/auction/get-auction-requests").hasRole("STAFF")
                        .requestMatchers("/api/admin-manager/users").hasRole("ADMIN")
                        .requestMatchers("/api/user").hasRole("USER")
                        .requestMatchers("/api/koi-fish/{id}").permitAll()
                        .requestMatchers("/api/koi-fish/get-all").permitAll()
                        .requestMatchers("/api/security/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/verify/**").permitAll()
                        .requestMatchers("/api/forgot-password/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/koi-types", "/api/koi-types/{id}", "/api/koi-origin", "/api/koi-origin/{id}").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint)  // Custom handler for unauthenticated users
                        .accessDeniedHandler(accessDeniedHandler)  // Custom handler for access denied cases
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Stateless session
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }



    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider=new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        provider.setUserDetailsService(userDetailService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
