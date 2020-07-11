package com.example.oauth2jwtdemo.config;

import com.example.oauth2jwtdemo.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Pelumi on 11-Jul-2020
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    CustomUserDetailsService userDetailsService;

    /**
     * Implementation of the filter which extracts the bearer token in the request and validates it and also confirms
     * that there is no authenticated user in the security context before recreating the
     * 'UsernamePasswordAuthenticationToken' and passes the request to the next filter.
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {
        final String authorizationRequestHeader = httpServletRequest.getHeader("Authorization");
        String username = null;
        String jwtToken = null;
        // JWT Token is in the form "Bearer token". Remove Bearer word and get
        // only the Token
        if (authorizationRequestHeader != null && authorizationRequestHeader.startsWith("Bearer ")) {
            jwtToken = authorizationRequestHeader.substring(7);
            try {
                username = jwtTokenUtil.extractUserName(jwtToken);
            }
            catch (IllegalArgumentException e) {
                throw new ServletException("Unable to get JWT Token", e);
            }
            catch (ExpiredJwtException e) {
                throw new ServletException("Jwt Token has expired", e);
            }
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
