package com.pwc;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public interface Constants {

    String TIMEZONE_UTC = "UTC";
    ZoneId DEFAULT_ZONE = ZoneId.of(TIMEZONE_UTC);
    TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone(DEFAULT_ZONE);
    String DATE_FORMAT = "yyyy-MM-dd";
    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    byte[] EMPTY_BYTES = {};
    String[] EMPTY_STRINGS = {};

    Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
}
