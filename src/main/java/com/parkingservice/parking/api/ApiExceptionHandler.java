package com.parkingservice.parking.api;

import com.parkingservice.parking.infrastructure.UpstreamServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class ApiExceptionHandler {

    /** Converts provider failures into a stable error that hides infrastructure details. */
    @ExceptionHandler(UpstreamServiceException.class)
    ProblemDetail handleUpstreamFailure() {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
        problemDetail.setTitle("Parking data temporarily unavailable");
        problemDetail.setDetail("The parking provider could not be reached. Please try again later.");
        return problemDetail;
    }
}
