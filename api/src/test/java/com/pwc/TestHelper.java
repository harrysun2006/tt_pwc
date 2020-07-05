package com.pwc;

import com.pwc.dto.AddressBookDto;
import com.pwc.dto.ContactDto;
import org.assertj.core.api.Assertions;
import org.jooq.DSLContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Configuration
public class TestHelper implements ApplicationListener<ContextRefreshedEvent> {

    private static ApplicationContext context;

    public void onApplicationEvent(ContextRefreshedEvent event) {
        context = event.getApplicationContext();
    }

    public static void clean() {
        DSLContext dsl = context.getBean(DSLContext.class);
        dsl.execute("delete from address_book_detail");
        dsl.execute("delete from address_book");
        dsl.execute("delete from contact");
    }

    public static void bookEquals(AddressBookDto book1, AddressBookDto book2) {
        assertEquals(book1.getName(), book2.getName());
        contactsEquals(book1.getContacts(), book2.getContacts());
    }

    public static void contactsEquals(List<ContactDto> cc1, List<ContactDto> cc2) {
        assertEquals(cc1.size(), cc2.size());
        cc1.sort(Comparator.comparing(ContactDto::getName));
        cc2.sort(Comparator.comparing(ContactDto::getName));
        for (int i = 0; i < cc1.size(); i++) {
            contactEquals(cc1.get(i), cc2.get(i));
        }
    }

    public static void contactEquals(ContactDto c1, ContactDto c2) {
        Assertions.assertThat(c1).isEqualToIgnoringGivenFields(c2, "id");
    }
}
