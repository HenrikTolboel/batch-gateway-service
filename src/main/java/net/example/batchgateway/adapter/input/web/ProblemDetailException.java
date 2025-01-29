package net.example.batchgateway.adapter.input.web;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

import java.net.URI;

public class ProblemDetailException extends RuntimeException {
    private final ProblemDetail problemDetail;
    public ProblemDetailException(final ProblemDetail problemDetail) {
        super();
        this.problemDetail = problemDetail;
    }

    public ProblemDetailException(final HttpStatusCode status, final String detail, final URI type) {
        super();
        problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(type);
    }

    public ProblemDetail getProblemDetail() {
        return problemDetail;
    }
}
