package com.example.fyprojbe.filters;

import com.example.fyprojbe.service.UserService;
import com.example.fyprojbe.utils.JwtTokenUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

// Class for checking if request is authenticated and sets authentication in the context
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;

    // @param userService Service: for retrieving user details
    // @param jwtTokenUtil Utility: for JWT
    public JwtAuthenticationFilter(UserService userService, JwtTokenUtil jwtTokenUtil) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    // called for every request to check if request is authenticated
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String path = request.getRequestURI();      // check the path of request
        if (shouldPermitWithoutAuth(path)) {        // if public
            chain.doFilter(request, response);      // allow request to continue
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");      // extract auth header
        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) { // if header present
            jwt = authorizationHeader.substring(7);                           // remove "Bearer " prefix
            username = jwtTokenUtil.extractUsername(jwt);                               // extract username from token
        }

        // If username extracted and no authentication exists in context, then load user details
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userService.loadUserByUsername(username);

            // after loading, validate the JWT and create and set Spring Security auth token in the context
            if (jwtTokenUtil.validateToken(jwt, userDetails)) {
                // Create authentication token, set in security context
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);      // Continue with filter chain with this info and request
    }

    // boolean to check if path is public
    private boolean shouldPermitWithoutAuth(String path) {
        return path.startsWith("/user/create") ||
                path.startsWith("/user/login") ||
                path.startsWith("/cards") ||
                path.startsWith("/card") ||
                path.startsWith("/definition");
    }
}