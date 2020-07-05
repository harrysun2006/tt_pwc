package com.pwc.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.pwc.TestData
import com.pwc.TestHelper
import com.pwc.dto.AddressBookDto
import com.pwc.dto.ContactDto
import com.pwc.service.AddressBookService
import com.pwc.util.JsonUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AddressBookControllerSpec extends Specification {

    @Value('${api.rest-base-url}')
    String restBaseUrl

    @Autowired
    WebApplicationContext context

    @Autowired
    MockMvc mvc

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    AddressBookService service

    static TypeReference<List<ContactDto>> TR_CONTACTS = new TypeReference<List<ContactDto>>() {}

    // clean data before each test
    def setup() {
        // mvc = webAppContextSetup(this.context).alwaysDo(MockMvcResultHandlers.print()).build()
        TestHelper.clean()
    }

    def 'should create the given address book'() {
        // Given the address book creation payload
        given:
        def book = AddressBookDto.of(TestData.BOOK1)
        def json = JsonUtils.writeValue(book)
        // When post to the endpoint
        when:
        def resonse = mvc.perform(post("${restBaseUrl}/address-books").contentType(MediaType.APPLICATION_JSON).content(json))
        // Then the address book should be created
        then:
        noExceptionThrown()
        resonse.andExpect(status().isCreated())
        def book1 = service.list(TestData.BOOK1_NAME)
        TestHelper.bookEquals(book1.get(), book)
    }

    def 'should respond to health check request'() {
        when:
        def response = mvc.perform(get("${restBaseUrl}/health-check").contentType(MediaType.APPLICATION_JSON))
        then:
        noExceptionThrown()
        def ok = response.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()
        ok != null
    }

    def 'should list the address book with given name'() {
        // Given the address book already exists
        given:
        service.create(TestData.BOOK1)
        // When request the endpoint to retrieve it
        when:
        def response = mvc.perform(get("${restBaseUrl}/address-books").contentType(MediaType.APPLICATION_JSON).param("name", TestData.BOOK1_NAME))
        then:
        // Then the address book should be returned in the payload
        noExceptionThrown()
        def json = response.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()
        def book = JsonUtils.readValue(json, AddressBookDto.class)
        TestHelper.bookEquals(TestData.BOOK1, book)
    }

    def 'should return 404 when the address book with given name does not exist'() {
        // When request a non-exist address book
        when:
        def response = mvc.perform(get(restBaseUrl + "/address-books").contentType(MediaType.APPLICATION_JSON).param("name", "NONE"))

        // Then 404 should return
        then:
        noExceptionThrown()
        def json = response.andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString()
        json != null
    }

    @Unroll
    def 'should return unique contacts (difference) from two address books - #scenario'() {
        // Given address books have been created
        given:
        service.create(TestData.BOOK1)
        service.create(TestData.BOOK2)
        service.create(TestData.BOOK3)

        // When request to get the unique(different) contacts from two address books
        when:
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>()
        params.add("name1", name1)
        params.add("name2", name2)
        def response = mvc.perform(get(restBaseUrl + "/address-books-diff").contentType(MediaType.APPLICATION_JSON).params(params))

        // Then a list containing expected contacts should return
        then:
        noExceptionThrown()
        def json = response.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()
        def list = JsonUtils.readValue(json, TR_CONTACTS)
        list != null
        def names = list.collectEntries { it -> [it.name, it] }.keySet().toList()
        names == expect

        where:
        scenario               | name1               | name2               | expect
        'book1 is empty'       | TestData.BOOK3_NAME | TestData.BOOK2_NAME | [ 'Jane', 'John', 'Mary' ]
        'book2 is empty'       | TestData.BOOK1_NAME | TestData.BOOK3_NAME | [ 'Bob', 'Jane', 'Mary' ]
        'both books not empty' | TestData.BOOK1_NAME | TestData.BOOK2_NAME | [ 'Bob', 'John' ]
        'both books empty'     | TestData.BOOK3_NAME | TestData.BOOK3_NAME | [ ]
        'same books not empty' | TestData.BOOK1_NAME | TestData.BOOK1_NAME | [ ]
    }

    @Unroll
    def 'should throw an exception - #scenario'() {
        given:
        // Given address books have been created
        service.create(AddressBookDto.of(TestData.BOOK1))
        service.create(AddressBookDto.of(TestData.BOOK2))
        service.create(AddressBookDto.of(TestData.BOOK3))

        // When request to get the unique(different) contacts from two address books
        when:
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>()
        params.add("name1", name1)
        params.add("name2", name2)
        def response = mvc.perform(get(restBaseUrl + "/address-books-diff").contentType(MediaType.APPLICATION_JSON).params(params))

        // Then 404 should return if one address book does not exist
        then:
        noExceptionThrown()
        def json = response.andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString()

        where:
        scenario                | name1                 | name2
        'book1 not exist'       | ''                    | TestData.BOOK2_NAME
        'book2 not exist'       | TestData.BOOK1_NAME   | ''
        'both books not exist'  | 'NONE'                | 'NONE'
    }

}