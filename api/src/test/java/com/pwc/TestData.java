package com.pwc;

import com.pwc.dto.AddressBookDto;
import com.pwc.dto.ContactDto;

import java.util.ArrayList;
import java.util.List;

public interface TestData {

    String BOOK1_NAME = "Book1";
    String BOOK2_NAME = "Book2";
    String BOOK3_NAME = "Book3";
    ContactDto BOB = ContactDto.of("Bob", "0423789001");
    ContactDto MARY = ContactDto.of("Mary", "0423789002");
    ContactDto JANE = ContactDto.of("Jane", "0423789003");
    ContactDto JOHN = ContactDto.of("John", "0423789004");

    List<ContactDto> CONTACTS1 = new ArrayList<>() {{
        add(ContactDto.of(BOB));
        add(ContactDto.of(MARY));
        add(ContactDto.of(JANE));
    }};

    List<ContactDto> CONTACTS2 = new ArrayList<>() {{
        add(ContactDto.of(MARY));
        add(ContactDto.of(JOHN));
        add(ContactDto.of(JANE));
    }};

    List<ContactDto> CONTACTS4 = new ArrayList<>() {{
        add(ContactDto.of(BOB));
        add(ContactDto.of(JOHN));
    }};

    AddressBookDto BOOK1 = AddressBookDto.of(BOOK1_NAME, CONTACTS1);

    AddressBookDto BOOK2 = AddressBookDto.of(BOOK2_NAME, CONTACTS2);

    AddressBookDto BOOK3 = AddressBookDto.of(BOOK3_NAME, new ArrayList<>());

    AddressBookDto BOOK4 = AddressBookDto.of(CONTACTS4);

}
