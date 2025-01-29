package net.example.batchgateway.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final boolean disabledSecurity = true;

    private final ApplicationContext applicationContext;

    public JwtAuthorizationFilter(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        logger.info("JWT Authorization Filter: {}", request.getRequestURI());

        if (disabledSecurity) {
            filterChain.doFilter(request, response);
            return;
        }

        if (request.getRequestURI().startsWith("/actuator/") ||
                request.getRequestURI().startsWith("/info/version") ||
                request.getRequestURI().startsWith("/readyz") ||
                request.getRequestURI().startsWith("/livez")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        var jwtAuthorizationList = getJwtAuthorization(request, response);

        filterChain.doFilter(request, response);
    }

    private List<JwtAuthorization> getJwtAuthorization(HttpServletRequest request, HttpServletResponse response) {
        List<JwtAuthorization> result = new ArrayList<>();

        var optionalMethod = getHandlerMethod(request);
        if (optionalMethod.isPresent()) {
            result.addAll(List.of(optionalMethod.get().getMethod().getDeclaredAnnotationsByType(JwtAuthorization.class)));
        }

        if (optionalMethod.isEmpty() || result.isEmpty()) {
            // We found no handler method, meaning there's no Controller Endpoint. Throw exception to allow rejection
            throw new NoSuchElementException(String.format("endpoint '%s' not found", request.getRequestURI()));
        }

        return result;
    }

    private Optional<HandlerMethod> getHandlerMethod(HttpServletRequest request) {
        RequestMappingHandlerMapping mapping = applicationContext.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);

        try {
            var handler = mapping.getHandler(request);
            if (handler == null) {
                return Optional.empty();
            }
            return Optional.of((HandlerMethod)handler.getHandler());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Optional.empty();
        }
    }
}
