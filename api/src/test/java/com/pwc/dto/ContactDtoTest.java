package com.pwc.dto;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ContactDtoTest {

    private static final String NAME = "Tester";
    private static final String PHONE = "1234";

    @Test
    public void shouldConstructContactDto() {
        ContactDto dto = ContactDto.of(NAME, PHONE);
        assertNull(dto.getId());
        assertEquals(dto.getName(), NAME);
        assertEquals(dto.getPhone(), PHONE);
        ContactDto dto2 = ContactDto.of(dto);
        assertEquals(dto2, dto);
    }
}
