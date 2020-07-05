package com.pwc.helper;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ErrorCodes implements ErrorCode {

    UNKNOWN_ERROR_9001(9001, "Unknown Error"),
    GENERIC_DECLINE_9002(9002, "Generic Decline"),
    GENERIC_FAILURE_9003(9003, "Generic Failure"),
    INVALID_CREDENTIAL_9004(9004, "Invalid Credential"),
    NOT_AUTHORISED_9005(9005, "Not Authorised"),
    SESSION_ERROR_9006(9006, "Session Error"),
    SESSION_REQUIRED_9007(9007, "Session Required"),
    HEADER_USERID_REQUIRED_9008(9008, "Header 'UserId' Required"),
    INVALID_JSON_REQUEST_9009(9009, "Invalid JSON Request"),
    HEADER_USERAGENT_REQUIRED_9010(9010, "Header 'User-Agent' Required"),
    BAD_HTTP_METHOD_9011(9011, "Bad HTTP Method"),
    HEADER_REQUESTID_REQUIRED_9012(9012, "Header 'RequestId' Required"),
    HEADER_CLIENT_IP_REQUIRED_9013(9013, "Header 'Client-IP' Required"),
    BACKEND_SERVICE_UNAVAILABLE_9014(9014, "Backend Service Unavailable"),

    DUPLICATE_REQUEST_9018(9018, "Duplicated Request"),
    HEADER_ACCEPT_REQUIRED_9019(9019, "Header 'Accept' Required"),
    TARGET_ENDPOINT_UNAVAILABLE_9020(9020, "Target Endpoint Unavailable"),
    HEADER_CONTENT_TYPE_REQUIRED_9021(9021, "Header 'Content-Type' Required"),
    REQUEST_NOT_CONFORM_TO_SPECIFICATION_9022(9022, "Request Not Conform To Specification"),
    HEADER_USERAGENT_INVALID_9025(9025, "Header 'User-Agent' Invalid"),
    INVALID_SESSION_9026(9026, "Invalid Session"),

    REQUEST_TIMEOUT_9041(9041, "Request Timeout"),
    INVALID_REQUEST_PARAMETER_9044(9044, "Invalid Request Parameter"),
    REQUEST_PARAMETER_MISSING_9045(9045, "Request Parameter Missing"),
    RESOURCE_NOT_FOUND_9046(9046, "Resource Not Found"),
    HEADER_ACCESS_TOKEN_REQUIRED_9047(9047, "Header 'Access-Token' Required"),
    HEADER_ACCESS_TOKEN_INVALID_9048(9048, "Header 'Access-Token' Invalid"),
    RESOURCE_CONFLICT_9049(9049, "Resource Conflict");

    private final int code;
    private final String message;

    ErrorCodes(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ErrorCodes fromCode(int code) {
        return Arrays.stream(values()).filter(c -> c.getCode() == code).findFirst().orElse(UNKNOWN_ERROR_9001);
    }

    @Override
    public String toString() {
        return String.valueOf(this.getCode());
    }

}
