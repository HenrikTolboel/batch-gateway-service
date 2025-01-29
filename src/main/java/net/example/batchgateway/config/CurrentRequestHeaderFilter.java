package net.example.batchgateway.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CurrentRequestHeaderFilter extends OncePerRequestFilter {

    private final CurrentRequestHeaders currentRequestHeaders;

    public CurrentRequestHeaderFilter(final CurrentRequestHeaders currentRequestHeaders) {
        super();
        this.currentRequestHeaders = currentRequestHeaders;
    }
    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {

        try {
            MDC.remove("accountId");
            final Map<String, String> httpHeaders = getHttpHeaders(request);

            currentRequestHeaders.set(httpHeaders);

            filterChain.doFilter(request, response);
        } finally {
            currentRequestHeaders.remove();
        }
    }

    private Map<String, String> getHttpHeaders(final HttpServletRequest httpServletRequest) {
        final Map<String, String> httpHeaders = new HashMap<>();

        final Enumeration<String> headerNames = httpServletRequest.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            final String header = headerNames.nextElement();

            httpHeaders.put(header, httpServletRequest.getHeader(header));
        }

        return httpHeaders;
    }

    @Override
    public void destroy() {
        super.destroy();
        currentRequestHeaders.remove();
        MDC.remove("accountId");
    }

}
