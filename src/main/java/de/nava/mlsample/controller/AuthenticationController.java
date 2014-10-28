package de.nava.mlsample.controller;

import de.nava.mlsample.domain.LoginCredential;
import de.nava.mlsample.domain.UserTransfer;
import de.nava.mlsample.security.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

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
            value = "/auth/authenticate",
            method = { RequestMethod.POST }
    )
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

}
