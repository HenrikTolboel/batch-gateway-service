package net.example.batchgateway.adapter.input.web;

import jakarta.servlet.ServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class RestExceptionHandlers {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(
            final IllegalArgumentException ex,
            final ServletRequest request
    ) {
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle(ex.getLocalizedMessage());
        problemDetail.setType(URI.create("error:illegal-argument"));

        return problemDetail;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalStateException(
            final IllegalStateException ex,
            final ServletRequest request
    ) {
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle(ex.getLocalizedMessage());
        problemDetail.setType(URI.create("error:illegal-state"));

        return problemDetail;
    }

    @ExceptionHandler(ProblemDetailException.class)
    public ProblemDetail handleProblemDetailException(
            final ProblemDetailException ex,
            final ServletRequest request
    ) {
        return ex.getProblemDetail();
    }

}
