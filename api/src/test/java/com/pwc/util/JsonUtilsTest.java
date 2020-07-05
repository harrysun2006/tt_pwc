package com.pwc.util;

import com.pwc.dto.ContactDto;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JsonUtilsTest {

    private static final String JSON = "{\"id\":null,\"name\":\"Tester\",\"phone\":\"0412345678\"}";
    private static final ContactDto CONTACT = ContactDto.of("Tester", "0412345678");

    @Test
    public void shouldReadValue() {
        ContactDto dto = JsonUtils.readValue(JSON, ContactDto.class);
        assertEquals(dto, CONTACT);
    }

    @Test
    public void shouldWriteValue() {
        String json = JsonUtils.writeValue(CONTACT);
        assertEquals(json, JSON);
    }
}
