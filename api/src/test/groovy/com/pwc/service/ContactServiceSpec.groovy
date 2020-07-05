package com.pwc.service

import com.pwc.TestData
import com.pwc.TestHelper
import com.pwc.dto.ContactDto
import com.pwc.mapper.AddressBookMapper
import com.pwc.repository.ContactRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContactServiceSpec extends Specification {

    static mapper = AddressBookMapper.INSTANCE
    @Autowired ContactService service
    @Autowired ContactRepository repository

    // clean data before each test
    def setup() {
        TestHelper.clean()
    }

    def 'should create a new contact' () {
        // Given a contact
        given:
        def dto = ContactDto.of(TestData.BOB)
        def entity = mapper.map(dto)
        // When it's saved
        when:
        service.save(entity)
        then:
        // The contact is saved successfully
        noExceptionThrown()
        def entity2 = repository.findByName(TestData.BOB.name)
        entity2.isPresent()
        def dto2 = mapper.map(entity2.get())
        dto2.id != null
        TestHelper.contactEquals(dto2, dto)
    }

    def 'should update the existing contact' () {
        // Given a contact is created
        given:
        def dto = ContactDto.of(TestData.BOB)
        def entity = mapper.map(dto)
        service.save(entity)
        // When the contact is saved with a new phone number
        when:
        entity.phone = '0456789123'
        service.save(entity)
        // Then the contact's phone is changed
        then:
        noExceptionThrown()
        def entity2 = repository.findByName(TestData.BOB.name)
        entity2.isPresent()
        def dto2 = mapper.map(entity2.get())
        dto2.name == dto.name
        dto2.phone != dto.phone
    }
}
