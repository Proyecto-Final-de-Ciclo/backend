// package com.example.demo.security;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpMethod;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.config.Customizer;
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// @Configuration
// @EnableMethodSecurity
// public class SecurityConfig {
//   @Autowired
//   UserDetailsService userDetailsService;

//   @Autowired
//   private AuthEntryPointJwt authEntryPointJwt;

//   @Bean
//   public AuthTokenFilter authenticationJwtTokenFilter() {
//     return new AuthTokenFilter();
//   }

//   @Bean
//   public DaoAuthenticationProvider authenticationProvider() {
//     DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//     authProvider.setUserDetailsService(userDetailsService);
//     authProvider.setPasswordEncoder(passwordEncoder());
//     return authProvider;
//   }

//   @Bean
//   public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//     return authConfig.getAuthenticationManager();
//   }

//   @Bean
//   public PasswordEncoder passwordEncoder() {
//     return new BCryptPasswordEncoder();
//   }

//   @Bean
//   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//     http.csrf(csrf -> csrf.disable())
//         .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPointJwt))
//         .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//         .authorizeHttpRequests(auth -> auth
//           .requestMatchers("/healthcheck").permitAll()
//             .requestMatchers("/api/auth/**").permitAll()

//             .requestMatchers(HttpMethod.GET, "/anuncios", "/anuncio/**", "/files/**")
//             .permitAll()

//             .requestMatchers(HttpMethod.POST, "/anuncio").authenticated()
//             .requestMatchers(HttpMethod.PUT, "/anuncio/**").authenticated()
//             .requestMatchers(HttpMethod.DELETE, "/anuncio/**").authenticated()

//             .requestMatchers(HttpMethod.GET, "/favoritos", "/favorito/**").authenticated()
//             .requestMatchers(HttpMethod.POST, "/favorito/**").authenticated()
//             .requestMatchers(HttpMethod.DELETE, "/favorito/**").authenticated()
//             .requestMatchers(HttpMethod.GET, "/anuncios/mios").authenticated()

//             .requestMatchers(HttpMethod.GET, "/usuarios").permitAll()
//             .requestMatchers(HttpMethod.PUT, "/usuario/perfil").authenticated()
//             .requestMatchers(HttpMethod.PUT, "/usuario/{id}").hasRole("ADMIN")
//             .requestMatchers(HttpMethod.DELETE, "/usuario/**").hasRole("ADMIN")

//             .requestMatchers(HttpMethod.GET, "/categorias", "/categoria/**").permitAll()
//             .requestMatchers(HttpMethod.POST, "/categoria").hasRole("ADMIN")
//             .requestMatchers(HttpMethod.PUT, "/categoria/**").hasRole("ADMIN")
//             .requestMatchers(HttpMethod.DELETE, "/categoria/**").hasRole("ADMIN")

//             .requestMatchers(HttpMethod.GET, "/usuario/*").permitAll()
//             .requestMatchers(HttpMethod.GET, "/usuario/*/anuncios").permitAll()
//             .requestMatchers(HttpMethod.GET, "/usuario/*/reseñas").permitAll()
//             .requestMatchers(HttpMethod.POST, "/usuario/*/reseña").authenticated()
//             .requestMatchers(HttpMethod.DELETE, "/reseña/**").authenticated()
//             .requestMatchers(HttpMethod.GET, "/cambio").permitAll()
//             .requestMatchers(HttpMethod.GET, "/noticias").permitAll()

//             .requestMatchers(HttpMethod.GET, "/llamadas").permitAll()
//             .requestMatchers(HttpMethod.POST, "/llamada").authenticated()
//             .requestMatchers(HttpMethod.DELETE, "/llamada/**").authenticated()
//             .anyRequest().authenticated());
//     http.authenticationProvider(authenticationProvider());
//     http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
//     http.cors(Customizer.withDefaults());
//     return http.build();
//   }
// }

package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired
  UserDetailsService userDetailsService;

  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }


  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())) // Permitir frames para H2
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/h2-console/**").permitAll() // Permitir acceso a H2
            .anyRequest().permitAll());
    return http.build();
  }
}