package com.pwc.repository;

import com.pwc.exception.UnhandledCaseException;
import org.jooq.UpdateSetMoreStep;
import org.jooq.generated.tables.pojos.Contact;
import org.jooq.generated.tables.records.ContactRecord;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static org.jooq.generated.Tables.CONTACT;

@Repository
public class ContactRepository extends AbstractRepository<Contact, ContactRecord, Long> {

    @Override
    protected String getPojoIdSetterName() {
        return "setId";
    }

    @Override
    protected String getPojoIdGetterName() {
        return "getId";
    }

    public Contact create(Contact contact) {
        ContactRecord record = dsl.newRecord(CONTACT, contact);
        dsl.executeInsert(record);
        Contact contact2 = dsl.select().from(CONTACT)
                .where(CONTACT.NAME.eq(contact.getName()))
                .fetchOneInto(Contact.class);
        contact.setId(contact2.getId());
        return contact2;
    }

    public Contact update(Contact contact) {
        UpdateSetMoreStep<ContactRecord> uu = dsl.update(CONTACT)
                .set(CONTACT.PHONE, contact.getPhone());
        if (contact.getId() != null) {
            uu.where(CONTACT.ID.eq(contact.getId())).execute();
        } else if (contact.getName() != null) {
            uu.where(CONTACT.NAME.eq(contact.getName())).execute();
        } else {
            throw new UnhandledCaseException();
        }

        return dsl.select().from(CONTACT)
                .where(CONTACT.NAME.eq(contact.getName()))
                .fetchOneInto(Contact.class);
    }

    public Optional<Contact> findByName(String name) {
        return dsl.select()
                .from(CONTACT)
                .where(CONTACT.NAME.equal(name))
                .fetchOptionalInto(Contact.class);
    }
}
