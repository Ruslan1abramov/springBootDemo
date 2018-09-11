package com.example.demo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
class ItemController {

    private final ItemRepository repository;
    private final ItemResourceAssembler assembler;

    ItemController(ItemRepository repository , ItemResourceAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    // Aggregate root

    @GetMapping("/items")
    Resources<Resource<Item>> all() {

        List<Resource<Item>> items = repository.findAll().stream()
                .map(assembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(items,
                linkTo(methodOn(ItemController.class).all()).withSelfRel());
    }

    @PostMapping("/items")
    ResponseEntity<?> newItem(@RequestBody Item newItem) throws URISyntaxException {

        Resource<Item> resource = assembler.toResource(repository.save(newItem));

        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    // Single item

    @GetMapping("/items/{id}")
    Resource<Item> one(@PathVariable Long id) {

        Item item = repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));

        return assembler.toResource(item);
    }

    @PutMapping("/items/{id}")
    ResponseEntity<?> replaceItem(@RequestBody AmountToChange amountToChange, @PathVariable Long id)  throws URISyntaxException{

        Item updatedItem = repository.findById(id)
                .map(item -> {
                    if(amountToChange.getAmount() + item.getAmount() >= 0)
                        item.setAmount(item.getAmount() + amountToChange.getAmount() );

                    return repository.save(item);
                })
                .orElseThrow(() -> new ItemNotFoundException(id));
        if(updatedItem.getAmount() == 0)
            return deleteItem(id);

        Resource<Item> resource = assembler.toResource(updatedItem);
        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    @DeleteMapping("/items/{id}")
    ResponseEntity<?> deleteItem(@PathVariable Long id) {

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}