package com.pwc.service;

import com.pwc.repository.ContactRepository;
import org.jooq.generated.tables.pojos.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    /**
     * Store the contact
     * when there is no contact with the given name, it's created; otherwise it's updated
     * @param contact
     */
    @Transactional
    public void save(Contact contact) {
        contactRepository.findByName(contact.getName()).ifPresentOrElse(c0 -> {
            c0.setPhone(contact.getPhone());
            contactRepository.update(c0);
            contact.setId(c0.getId());
        }, () -> contactRepository.create(contact));
    }

}
