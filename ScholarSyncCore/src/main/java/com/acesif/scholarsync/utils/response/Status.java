package com.acesif.scholarsync.utils.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {

    RETRIEVED( "Retrieved"),
    DUPLICATE( "Duplicate resource"),
    CREATED("Resource Created"),
    UPDATED("Resource Updated"),
    DELETED("Resource Deleted"),
    PURGED("Resource Purged"),
    VALIDATION_FAILED("Validation Failed"),
    UNAUTHORIZED("Unauthorized Access"),
    FORBIDDEN("Forbidden"),
    NO_DATA("No data"),
    NOT_FOUND("Resource Not Found"),
    INTERNAL_SERVER_ERROR("Internal Server Error");

    private final String message;
}
