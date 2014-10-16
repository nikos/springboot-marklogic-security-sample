package de.nava.mlsample;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${default.username}")
    String defaultUsername;

    @Value("${default.password}")
    String defaultPassword;

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
                .antMatchers("/**").hasRole("USER");

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

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
            .ignoring()
                .antMatchers("/public/**")
        .and()
            .ignoring()
                .antMatchers("/")
        ;
    }

}
