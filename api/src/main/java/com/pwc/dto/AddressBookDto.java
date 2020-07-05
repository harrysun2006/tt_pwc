package com.pwc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressBookDto  implements Serializable {

    private Long id;
    private String name;
    private List<ContactDto> contacts;

    public static AddressBookDto of(String name) {
        return new AddressBookDto(null, name, null);
    }

    public static AddressBookDto of(List<ContactDto> contacts) {
        return new AddressBookDto(null, null, contacts);
    }

    public static AddressBookDto of(String name, List<ContactDto> contacts) {
        return new AddressBookDto(null, name, contacts);
    }

    public static AddressBookDto of(AddressBookDto book) {
        List<ContactDto> contacts = new ArrayList<>();
        book.getContacts().forEach(c -> contacts.add(ContactDto.of(c)));
        return new AddressBookDto(book.getId(), book.getName(), contacts);
    }
}
