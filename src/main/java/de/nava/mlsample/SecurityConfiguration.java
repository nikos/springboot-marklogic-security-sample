package de.nava.mlsample;

import de.nava.mlsample.security.XAuthTokenConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${default.username}")
    String defaultUsername; // TODO: remove

    @Value("${default.password}")
    String defaultPassword; // TODO: remove

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Sync HTTP Header names to AngularJs name (default Spring: X-CSRF-TOKEN)
        HttpSessionCsrfTokenRepository tokenRepository = new HttpSessionCsrfTokenRepository();
        tokenRepository.setHeaderName("X-XSRF-TOKEN");
        // ~~
        http
            .csrf()
                .csrfTokenRepository(tokenRepository)
                // .disable()  // for testing purposes
        .and()
            .authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
        .and()
            .authorizeRequests()
                .antMatchers("/**").hasRole("USER");

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // injects filter to read out x-auth-token header and validates it
        SecurityConfigurer<DefaultSecurityFilterChain, HttpSecurity> securityConfigurerAdapter = new XAuthTokenConfigurer(userDetailsServiceBean());
        http.apply(securityConfigurerAdapter);

        // Since we use the client-side AngularJS login view, we do not have to cover redirection
        /*
        .and()
            .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .usernameParameter("usr")
                .passwordParameter("pwd")
                .permitAll()
        .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .permitAll();
        */
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Alternative is to set own customized userDetailsService
        auth
            .inMemoryAuthentication()
                .withUser(defaultUsername)
                    .password(defaultPassword)
                    .roles("USER")
            .and()
                .withUser("admin")
                    .password("admin")
                    .roles("ADMIN", "USER");
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return super.userDetailsServiceBean();
    }

    /**
     * Which routes should be ignored on dealing with authentication at all.
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
            .ignoring()
                .antMatchers("/public/**")   // all assets
        .and()
            .ignoring()
                .antMatchers("/auth/login")  // login REST end-point
        .and()
            .ignoring()
                .antMatchers("/")   // the index page serves as root for the AngularJS app
        ;
    }

}
