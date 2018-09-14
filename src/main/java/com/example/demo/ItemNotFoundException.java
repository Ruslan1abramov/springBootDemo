package com.example.demo;

public class ItemNotFoundException extends RuntimeException {

    ItemNotFoundException(Long id) {
        super("Could not find item " + id);
    }
}
