package com.pwc.repository


import com.pwc.TestHelper
import com.pwc.mapper.AddressBookMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AddressBookRepositorySpec extends Specification {

    static mapper = AddressBookMapper.INSTANCE
    @Autowired AddressBookRepository repository

    // clean data before each test
    def setup() {
        TestHelper.clean()
    }


}
