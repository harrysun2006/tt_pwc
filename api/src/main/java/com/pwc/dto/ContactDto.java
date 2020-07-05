package com.pwc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto implements Serializable {

    private Long   id;
    private String name;
    private String phone;

    public static ContactDto of(String name, String phone) {
        return new ContactDto(null, name, phone);
    }

    public static ContactDto of(ContactDto contact) {
        return new ContactDto(contact.getId(), contact.getName(), contact.getPhone());
    }
}
