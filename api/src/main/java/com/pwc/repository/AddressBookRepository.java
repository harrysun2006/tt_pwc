package com.pwc.repository;

import org.jooq.generated.tables.pojos.AddressBook;
import org.jooq.generated.tables.pojos.AddressBookDetail;
import org.jooq.generated.tables.pojos.Contact;
import org.jooq.generated.tables.records.AddressBookDetailRecord;
import org.jooq.generated.tables.records.AddressBookRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.jooq.generated.Tables.ADDRESS_BOOK;
import static org.jooq.generated.Tables.ADDRESS_BOOK_DETAIL;
import static org.jooq.generated.Tables.CONTACT;

@Repository
public class AddressBookRepository extends AbstractRepository<AddressBook, AddressBookRecord, Long> {

    @Override
    protected String getPojoIdSetterName() {
        return "setId";
    }

    @Override
    protected String getPojoIdGetterName() {
        return "getId";
    }

    public AddressBook create(AddressBook book) {
        AddressBookRecord record = dsl.newRecord(ADDRESS_BOOK, book);
        dsl.executeInsert(record);
        AddressBook book2 = dsl.select().from(ADDRESS_BOOK)
                .where(ADDRESS_BOOK.NAME.eq(book.getName()))
                .fetchOneInto(AddressBook.class);
        book.setId(book2.getId());
        return book2;
    }

    public void addContactToBook(AddressBook book, Contact contact) {
        AddressBookDetail detail = new AddressBookDetail(book.getId(), contact.getId());
        AddressBookDetailRecord record = dsl.newRecord(ADDRESS_BOOK_DETAIL, detail);
        dsl.executeInsert(record);
    }

    public Optional<AddressBook> findByName(String name) {
        return dsl.select()
                .from(ADDRESS_BOOK)
                .where(ADDRESS_BOOK.NAME.equal(name))
                .fetchOptionalInto(AddressBook.class);
    }

    public List<Contact> listContactsByBook(Long bookId) {
        return dsl.select(CONTACT.fields())
                .from(ADDRESS_BOOK_DETAIL)
                .leftJoin(CONTACT).onKey()
                .where(ADDRESS_BOOK_DETAIL.FK_BOOK_ID.eq(bookId))
                .orderBy(CONTACT.NAME)
                .fetchInto(Contact.class);
    }

}
