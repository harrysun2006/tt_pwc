package com.pwc.service

import com.pwc.TestData
import com.pwc.TestHelper
import com.pwc.dto.AddressBookDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Unroll

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AddressBookServiceSpec extends Specification {

    @Autowired AddressBookService service

    // clean data before each test
    def setup() {
        TestHelper.clean()
    }

    def 'should create the address book'() {
        given:
        // Given two address books have been created
        service.create(AddressBookDto.of(TestData.BOOK1))
        service.create(AddressBookDto.of(TestData.BOOK2))

        when:
        def dto = service.list(TestData.BOOK1_NAME)
        def dto2 = service.list('None')

        then:
        noExceptionThrown()
        // address book 'Book1' is available and equals (except id) to book1
        dto.isPresent()
        TestHelper.bookEquals(TestData.BOOK1, dto.get())
        // address book 'None' doesn't exist
        dto2.isEmpty()
    }

    @Unroll
    def 'should return unique contacts (difference) from two address books - #scenario' () {
        given:
        // Given address books have been created
        service.create(AddressBookDto.of(TestData.BOOK1))
        service.create(AddressBookDto.of(TestData.BOOK2))
        service.create(AddressBookDto.of(TestData.BOOK3))

        when:
        // Get the unique(different) contacts from two address books
        def list = service.diff(name1, name2)

        then:
        noExceptionThrown()
        list != null

        // If at least one address book exists, the unique contact names should equal to expected names
        def names = list.collectEntries { it -> [it.name, it] }.keySet().toList()
        names == expect

        where:
        scenario                | name1                 | name2                 | expect
        'book1 is empty'        | TestData.BOOK3_NAME   | TestData.BOOK2_NAME   | [ 'Jane', 'John', 'Mary' ]
        'book2 is empty'        | TestData.BOOK1_NAME   | TestData.BOOK3_NAME   | [ 'Bob', 'Jane', 'Mary' ]
        'both books not empty'  | TestData.BOOK1_NAME   | TestData.BOOK2_NAME   | [ 'Bob', 'John']
        'both books empty'      | TestData.BOOK3_NAME   | TestData.BOOK3_NAME   | [ ]
        'same books not empty'  | TestData.BOOK1_NAME   | TestData.BOOK1_NAME   | [ ]
    }

    @Unroll
    def 'should throw an exception - #scenario' () {
        given:
        // Given address books have been created
        service.create(AddressBookDto.of(TestData.BOOK1))
        service.create(AddressBookDto.of(TestData.BOOK2))
        service.create(AddressBookDto.of(TestData.BOOK3))

        when:
        // Get the unique(different) contacts from two address books
        def list = service.diff(name1, name2)

        then:
        thrown(Exception)

        where:
        scenario                | name1                 | name2
        'book1 not exist'       | null                  | TestData.BOOK2_NAME
        'book2 not exist'       | TestData.BOOK1_NAME   | ''
        'both books not exist'  | 'NONE'                | 'NONE'
    }
}
