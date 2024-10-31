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

//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("*"));
//        configuration.setAllowedMethods(Arrays.asList("*"));
//        configuration.setAllowedHeaders(Arrays.asList("*"));
//        configuration.setAllowCredentials(false);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)throws Exception{
        httpSecurity.cors(Customizer.withDefaults());
        httpSecurity.csrf(customizer -> customizer.disable());
        httpSecurity.authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.GET, "/api/admin-manager/users/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/api/admin-manager/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/koi-fish/customize-koi-fish").hasRole("BREEDER")
                        .requestMatchers(HttpMethod.DELETE,"/api/koi-fish").hasRole("BREEDER")
                        .requestMatchers(HttpMethod.DELETE,"/api/koi-fish").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/koi-types/add-koitype").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/koi-types/delete/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/koi-origin/add-origin").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/koi-origin/delete/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/system-config/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/system-config/**").hasRole("ADMIN")
                        .requestMatchers("/api/wallet/vnpay_return").permitAll()
                        .requestMatchers("/api/auction/breeder/**").hasRole("BREEDER")
                        .requestMatchers("/api/auction/staff/**").hasRole("ADMIN")
                        .requestMatchers("/api/auction/staff/**").hasRole("STAFF")
                        .requestMatchers("/api/auction/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/user").hasRole("USER")
                        .requestMatchers("/api/auction/{}").permitAll()
                        .requestMatchers("/api/auction/filter").permitAll()
                        .requestMatchers("/api/auction/past").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/api/security/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/verify/**").permitAll()
                        .requestMatchers("/api/forgot-password/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/koi-fish/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/koi-types/**","/api/koi-origin/**").permitAll()
                        .requestMatchers("/api/dashboard/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                        .sessionManagement(session ->
                                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                                )
                        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.httpBasic(Customizer.withDefaults());
        return httpSecurity.build();
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
