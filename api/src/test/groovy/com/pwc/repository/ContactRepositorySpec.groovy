package com.pwc.repository


import com.pwc.TestData
import com.pwc.TestHelper
import com.pwc.dto.ContactDto
import com.pwc.mapper.AddressBookMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContactRepositorySpec extends Specification {

    static mapper = AddressBookMapper.INSTANCE
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
        repository.create(entity)
        then:
        // The contact is saved successfully
        noExceptionThrown()
        def entity2 = repository.findByName(TestData.BOB.name)
        entity2.isPresent()
        entity2.get() == entity
    }

    def 'should update the existing contact' () {
        // Given a contact is created
        given:
        def dto = ContactDto.of(TestData.BOB)
        def entity = mapper.map(dto)
        repository.create(entity)
        // When the contact is saved with a new phone number
        when:
        // The record can be updated by name
        def entity1 = entity
        entity1.id = null
        entity1.phone = dto.phone + '123'
        entity1 = repository.update(entity1)
        // Then the contact's phone is changed
        then:
        noExceptionThrown()
        def entity2 = repository.findByName(TestData.BOB.name)
        entity2.isPresent()
        def entity3 = entity2.get()
        entity3 == entity1
        entity3.phone != dto.phone
    }

    def 'cannot update the existing contact' () {
        // Given a contact is created
        given:
        def dto = ContactDto.of(TestData.BOB)
        def entity = mapper.map(dto)
        repository.create(entity)
        // When the contact is saved with a new phone number
        when:
        // The record can't be updated if both id & name are null
        entity.id = null
        entity.name = null
        entity.phone = dto.phone + '123'
        repository.update(entity)
        // Then an exception should be thrown
        then:
        thrown(Exception)
    }
}
