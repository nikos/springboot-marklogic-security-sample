package de.nava.mlsample.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Sifts through all incoming requests and installs a Spring Security principal
 * if a header corresponding to a valid user is found.
 *
 * @author Philip W. Sorst (philip@sorst.net)
 * @author Josh Long (josh@joshlong.com)
 * @author Niko Schmuck (niko@nava.de)
 */
public class XAuthTokenFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(XAuthTokenFilter.class);

    public final static String X_AUTH_TOKEN_HEADER = "x-auth-token";

    private final UserDetailsService detailsService;

    public XAuthTokenFilter(UserDetailsService userDetailsService) {
        this.detailsService = userDetailsService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String authToken = httpServletRequest.getHeader(X_AUTH_TOKEN_HEADER);

            if (StringUtils.hasText(authToken)) {
                String username = TokenUtils.getUserNameFromToken(authToken);
                logger.debug("Retrieved request from user {}", username);
                UserDetails details = this.detailsService.loadUserByUsername(username);
                logger.debug("User details for {}: {}", username, details);

                if (TokenUtils.validateToken(authToken, details)) {
                    logger.debug("Valid token retrieved: user {}, roles: {}", username, details.getAuthorities());
                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(details, details.getPassword(), details.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(token);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}