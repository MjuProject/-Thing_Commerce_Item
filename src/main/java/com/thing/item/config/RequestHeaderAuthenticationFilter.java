package com.thing.item.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RequestHeaderAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String clientIdx = request.getHeader("clientIdx");
        String role = request.getHeader("role");

        if(validationHeader(clientIdx, role)){
            Authentication auth = getAuthentication(clientIdx, role);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }

    private boolean validationHeader(String clientIdx, String role){
        return StringUtils.hasText(clientIdx) && StringUtils.hasText(role);
    }

    private Authentication getAuthentication(String clientIdx, String role){
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(new String[] {role})
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        User principal = new User(clientIdx, "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }
}
