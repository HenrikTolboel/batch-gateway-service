package net.example.batchgateway.application.port;

public sealed interface ServiceError extends GeneralError {
    record DatabaseError(Throwable cause) implements ServiceError { }
    record DatabaseErrorMessage(String message) implements ServiceError { }
    record ReportApiError(Throwable cause) implements ServiceError { }
}
