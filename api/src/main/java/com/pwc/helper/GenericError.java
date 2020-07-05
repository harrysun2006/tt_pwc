package com.pwc.helper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenericError {

    private ErrorSeverity severity;
    private int code;
    private String message;
    private String description;
    private String location;

}
