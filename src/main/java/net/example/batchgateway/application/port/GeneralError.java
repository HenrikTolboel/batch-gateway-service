package net.example.batchgateway.application.port;


// https://softwaremill.com/functional-error-handling-with-java-17/


public sealed interface GeneralError permits ReportError, UserError, DomainError, ServiceError {
}
