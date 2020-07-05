package com.pwc.controller;

import com.pwc.dto.AddressBookDto;
import com.pwc.dto.ContactDto;
import com.pwc.exception.ResourceNotFoundException;
import com.pwc.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.rest-base-url}")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @PostMapping("/address-books")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody AddressBookDto dto) {
        addressBookService.create(dto);
    }

    @GetMapping("/address-books")
    @ResponseStatus(HttpStatus.OK)
    public AddressBookDto list(@RequestParam(value = "name") String name) {
        return addressBookService.list(name).orElseThrow(() -> new ResourceNotFoundException("Address book " + name + " is not found!"));
    }

    @GetMapping("/address-books-diff")
    @ResponseStatus(HttpStatus.OK)
    public List<ContactDto> diff(@RequestParam(value = "name1") String name1, @RequestParam(value = "name2") String name2) {
        return addressBookService.diff(name1, name2);
    }

    @GetMapping("/health-check")
    @ResponseStatus(HttpStatus.OK)
    public String healthCheck() {
        return "OK";
    }
}
