package com.pwc.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pwc.TestData;
import com.pwc.TestHelper;
import com.pwc.dto.AddressBookDto;
import com.pwc.dto.ContactDto;
import com.pwc.util.JsonUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class AddressBookControllerTest {

    @Autowired
    protected WebApplicationContext context;

    @Value("${api.rest-base-url}")
    protected String restBaseUrl;

    // Main entry point for server-side Spring MVC test support
    protected MockMvc mvc;

    private static final TypeReference<List<ContactDto>> TR_CONTACTS = new TypeReference<List<ContactDto>>() {};

    @Before
    public final void before() {
        this.mvc = webAppContextSetup(this.context)
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
    }

    @Test
    public void should1CreatAddressBooks() throws Exception {
        TestHelper.clean();
        AddressBookDto book = AddressBookDto.of(TestData.BOOK1);
        String json = JsonUtils.writeValue(book);
        mvc.perform(post(restBaseUrl + "/address-books").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());
        book = AddressBookDto.of(TestData.BOOK2);
        json = JsonUtils.writeValue(book);
        mvc.perform(post(restBaseUrl + "/address-books").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());
    }

    @Test
    public void should2ListContacts() throws Exception {
        String json = mvc.perform(get(restBaseUrl + "/address-books").contentType(MediaType.APPLICATION_JSON).param("name", TestData.BOOK1_NAME))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        AddressBookDto book = JsonUtils.readValue(json, AddressBookDto.class);
        TestHelper.bookEquals(TestData.BOOK1, book);
        json = mvc.perform(get(restBaseUrl + "/address-books").contentType(MediaType.APPLICATION_JSON).param("name", "None"))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(json);
    }

    @Test
    public void should3DiffContacts() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("name1", TestData.BOOK1_NAME);
        params.add("name2", TestData.BOOK2_NAME);
        String json = mvc.perform(get(restBaseUrl + "/address-books-diff").contentType(MediaType.APPLICATION_JSON).params(params))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<ContactDto> contacts = JsonUtils.readValue(json, TR_CONTACTS);
        TestHelper.contactsEquals(TestData.CONTACTS4, contacts);
        TestHelper.clean();
    }
}
