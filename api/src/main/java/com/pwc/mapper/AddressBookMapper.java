package com.pwc.mapper;

import com.pwc.dto.AddressBookDto;
import com.pwc.dto.ContactDto;
import org.jooq.generated.tables.pojos.AddressBook;
import org.jooq.generated.tables.pojos.Contact;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressBookMapper {

    AddressBookMapper INSTANCE = Mappers.getMapper(AddressBookMapper.class);

    AddressBookDto map(AddressBook entity);

    AddressBook map(AddressBookDto dto);

    ContactDto map(Contact entity);

    Contact map(ContactDto dto);

    List<ContactDto> map(List<Contact> entityList);
}
