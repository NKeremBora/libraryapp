package com.nihatkerembora.libraryapp.auth.security;

import com.nihatkerembora.libraryapp.auth.model.Token;
import com.nihatkerembora.libraryapp.auth.service.InvalidTokenService;
import com.nihatkerembora.libraryapp.auth.service.TokenService;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomBearerTokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final InvalidTokenService invalidTokenService;

/**
 * Intercepts incoming HTTP requests to process JWT-based Bearer authentication.
 * <p>
 * If the Authorization header contains a valid Bearer token, this method:
 * <ul>
 *     <li>Validates the JWT</li>
 *     <li>Checks if the token is invalidated</li>
 *     <li>Builds authentication details and sets them in the SecurityContext</li>
 * </ul>
 * </p>
 *
 * @param httpServletRequest  the incoming HTTP request
 * @param httpServletResponse the response object
 * @param filterChain         the remaining filter chain
 * @throws ServletException if filter chain processing fails
 * @throws IOException      if an I/O error occurs
 */
    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest httpServletRequest,
                                    @NonNull final HttpServletResponse httpServletResponse,
                                    @NonNull final FilterChain filterChain) throws ServletException, IOException {

        log.debug("API Request was secured with Security!");

        final String authorizationHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

        if (Token.isBearerToken(authorizationHeader)) {

            final String jwt = Token.getJwt(authorizationHeader);

            tokenService.verifyAndValidate(jwt);

            final String tokenId = tokenService.getId(jwt);

            invalidTokenService.checkForInvalidityOfToken(tokenId);

            final UsernamePasswordAuthenticationToken authentication = tokenService
                    .getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug(">> AFTER CONTEXT SET: {}", SecurityContextHolder.getContext().getAuthentication());

        }

        filterChain.doFilter(httpServletRequest,httpServletResponse);

    }

}