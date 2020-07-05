package com.pwc.service;

import com.pwc.dto.AddressBookDto;
import com.pwc.dto.ContactDto;
import com.pwc.exception.ResourceNotFoundException;
import com.pwc.mapper.AddressBookMapper;
import com.pwc.repository.AddressBookRepository;
import org.jooq.generated.tables.pojos.AddressBook;
import org.jooq.generated.tables.pojos.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AddressBookService {

    @Autowired
    private AddressBookRepository addressBookRepository;

    @Autowired
    private ContactService contactService;

    private AddressBookMapper mapper = AddressBookMapper.INSTANCE;

    /**
     * Store the given address book, with all contacts in it being saved too.
     * contact will be identified by its name
     * i.e. new contact will be created and exiting contact will be updated
     * @param dto
     */
    @Transactional
    public void create(AddressBookDto dto) {
        AddressBook book = mapper.map(dto);
        addressBookRepository.create(book);
        dto.getContacts().forEach(contactDto -> {
            Contact contact = mapper.map(contactDto);
            contactService.save(contact);
            addressBookRepository.addContactToBook(book, contact);
        });
    }

    /**
     * Retrieve the address book by the given name
     * @param name address book's name is unique
     * @return the address book with the given name
     *  or an Optional.empty() if the address book with the given name does not exist
     */
    public Optional<AddressBookDto> list(String name) {
        Optional<AddressBook> book = addressBookRepository.findByName(name);
        if (book.isEmpty()) return Optional.empty();
        AddressBookDto dto = mapper.map(book.get());
        List<Contact> contacts = addressBookRepository.listContactsByBook(dto.getId());
        dto.setContacts(mapper.map(contacts));
        return Optional.of(dto);

    }

    /**
     * TODO: use SQL query to find the exclusive list of contacts from one address book to another
     *       when an address book has a loads of contacts in it ( >= 100,000 )
     * Find contacts that are only in one address book
     * @param name1 the name of address book1
     * @param name2 the name of address book2
     * @return a list of contact that only appears in one of the given address books
     *  or throw an exception if any of the address book does not exist
     */
    public List<ContactDto> diff(String name1, String name2) {
        // if given address books are exactly the same one, return an empty contact list
        List<ContactDto> list = new ArrayList<>();
        if (name1 != null && name1.equals(name2) && addressBookRepository.findByName(name1).isPresent()) {
            return list;
        }
        Optional<AddressBookDto> book1 = list(name1);
        Optional<AddressBookDto> book2 = list(name2);
        // throw a ResourceNotFoundException if one of the address books does not exist
        if (book1.isEmpty() || book2.isEmpty()) {
            throw new ResourceNotFoundException("Address book " + name1 + " or " + name2 + " is not found!");
        } else {
            Map<String, ContactDto> contacts1 = book1.get().getContacts().stream().collect(Collectors.toMap(ContactDto::getName, c -> c));
            Map<String, ContactDto> contacts2 = book2.get().getContacts().stream().collect(Collectors.toMap(ContactDto::getName, c -> c));
            list.addAll(book1.get().getContacts().stream().filter(c -> !contacts2.containsKey(c.getName())).collect(Collectors.toList()));
            list.addAll(book2.get().getContacts().stream().filter(c -> !contacts1.containsKey(c.getName())).collect(Collectors.toList()));
            return list;
        }
    }
}
