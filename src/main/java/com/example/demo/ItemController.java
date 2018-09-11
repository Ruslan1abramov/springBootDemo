package com.example.demo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(value="onlinestore", description="Operations pertaining to items in Online Store")
class ItemController {

    private final ItemRepository repository;
    private final ItemResourceAssembler assembler;

    ItemController(ItemRepository repository , ItemResourceAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    // Aggregate root
    @ApiOperation(value = "View a list of all available products",response = Resources.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved items"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @GetMapping("/items")
    Resources<Resource<Item>> all() {

        List<Resource<Item>> items = repository.findAll().stream()
                .map(assembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(items,
                linkTo(methodOn(ItemController.class).all()).withSelfRel());
    }

    @ApiOperation(value = "Adding a new item",produces = "application/json")
    @PostMapping("/items")
    ResponseEntity<?> newItem(@RequestBody Item newItem) throws URISyntaxException {

        Resource<Item> resource = assembler.toResource(repository.save(newItem));

        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    // Single item
    @ApiOperation(value = "View item by id",response = Resource.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved item"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @GetMapping("/items/{id}")
    Resource<Item> one(@PathVariable Long id) {

        Item item = repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));

        return assembler.toResource(item);
    }

    @ApiOperation(value = "Updating specific item amount")
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

    @ApiOperation(value = "Delete item")
    @DeleteMapping("/items/{id}")
    ResponseEntity<?> deleteItem(@PathVariable Long id) {

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}