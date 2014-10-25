package de.nava.mlsample.controller;

import de.nava.mlsample.domain.LoginCredential;
import de.nava.mlsample.security.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This controller lets an user log in and log out against the authentication
 * service.
 *
 * @author Niko Schmuck
 */
@RestController
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;


    @RequestMapping(
            value = "/auth/status",
            method = { RequestMethod.GET },
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity status(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Current authenticated user {}", authentication);
        return new ResponseEntity<>(authentication, HttpStatus.OK);
    }


    @RequestMapping(value = "/auth/authenticate", method = { RequestMethod.POST })
    public UserTransfer authenticate(@RequestBody @Valid LoginCredential login) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(login.getUsername(),
                login.getPassword());
        Authentication authentication = this.authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails details = this.userDetailsService.loadUserByUsername(login.getUsername());

        Map<String, Boolean> roles = new HashMap<String, Boolean>();
        for (GrantedAuthority authority : details.getAuthorities()) {
            roles.put(authority.toString(), Boolean.TRUE);
        }

        return new UserTransfer(details.getUsername(), roles, TokenUtils.createToken(details));
    }

    public static class UserTransfer {

        private final String name;
        private final Map<String, Boolean> roles;
        private final String token;

        public UserTransfer(String userName, Map<String, Boolean> roles, String token) {

            Map<String, Boolean> mapOfRoles = new ConcurrentHashMap<String, Boolean>();
            for (String k : roles.keySet())
                mapOfRoles.put(k, roles.get(k));

            this.roles = mapOfRoles;
            this.token = token;
            this.name = userName;
        }

        public String getName() {
            return this.name;
        }

        public Map<String, Boolean> getRoles() {
            return this.roles;
        }

        public String getToken() {
            return this.token;
        }
    }

    // ~~~~~

    @RequestMapping(
        value = "/auth/login",
        method = { RequestMethod.POST },
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity login(@RequestBody @Valid LoginCredential login, HttpServletRequest request) {
        logger.info("Try to login user {} ...", login.getUsername());
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword(),
                getAuthorities());
        try {
            token.setDetails(new WebAuthenticationDetails(request));
            Authentication authentication = this.authenticationManager.authenticate(token);
            SecurityContext ctx = SecurityContextHolder.getContext();
            ctx.setAuthentication(authentication);

            // Create a new session and add the security context.
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", ctx);

            return new ResponseEntity<>(authentication, HttpStatus.OK);   // new User(login.getUsername());
        } catch (AuthenticationException e) {
            logger.warn("Unable to login user {}: {}", login.getUsername(), e.getMessage());
            return new ResponseEntity<>("{\"flash\": \"Invalid username or password\"}", HttpStatus.FORBIDDEN);
        }
    }

    public Collection<GrantedAuthority> getAuthorities() {
        //make everyone ROLE_USER
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        GrantedAuthority grantedAuthority = new GrantedAuthority() {
            //anonymous inner type
            public String getAuthority() {
                return "ROLE_USER";
            }
        };
        grantedAuthorities.add(grantedAuthority);
        return grantedAuthorities;
    }

    @RequestMapping(
            value = "/auth/logout",
            /*method = { RequestMethod.POST },*/
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity logout(HttpServletRequest request) {
        logger.info("Logout user, invalidate session {}", request.getSession().getId());
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return new ResponseEntity<>("{\"flash\": \"Logged out\"}", HttpStatus.OK);
    }


    // ~~

    // TOOD: to we need an transfer object to be exposed or do we continue to expose Authentication?
    public class User {
        private String username;
        public User() {}
        public User(final String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

}
