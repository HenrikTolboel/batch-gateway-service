package net.example.batchgateway.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE +10)
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Value("${DISABLED_REQUEST_LOGGING:false}")
    private boolean disabledRequestLogging;
    @Value("${security.debug:true}")
    private boolean securityDebug;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doFilterInternal(final HttpServletRequest httpServletRequest,
                                    final HttpServletResponse httpServletResponse,
                                    final FilterChain filterChain) throws ServletException, IOException {

        final ContentCachingRequestWrapper request = new ContentCachingRequestWrapper(httpServletRequest);
        final ContentCachingResponseWrapper response = new ContentCachingResponseWrapper(httpServletResponse);

        final Instant start = Instant.now();

        try {
            filterChain.doFilter(request, response);
        } finally {
            if (!disabledRequestLogging) {
                final Instant finished = Instant.now();
                final long timeInMillis = Duration.between(start, finished).toMillis();


                String requestBody = "";
                String responseBody = "";
                if (securityDebug) {
                    requestBody = request.getContentAsString();
                    responseBody = new String(response.getContentAsByteArray());
                }

                logger.info(
                        new ResultWrapper(
                                request.getMethod(),
                                request.getRequestURI(),
                                request.getProtocol(),
                                response.getStatus(),
                                timeInMillis,
                                response.getContentSize(),
                                requestBody,
                                responseBody
                        ).toString()
                );
            }

            response.copyBodyToResponse();
        }
    }

    private record ResultWrapper(
            String method,
            String requestURI,
            String protocol,
            int status,
            long timeInMillis,
            long bytesWritten,
            String requestBody,
            String responseBody
    ) {
        ResultWrapper {
            if (! SpringBootObjectMapper.isValidJSON(requestBody)) {
                requestBody = "SKIPPED";
            }
            if (! SpringBootObjectMapper.isValidJSON(responseBody)) {
                responseBody = "SKIPPED";
            }
        }

        @Override
        public String toString() {
            return SpringBootObjectMapper.writeValueAsString(this);
        }
    }

}
