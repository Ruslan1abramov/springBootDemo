package com.example.demo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@Api(value="onlineStore", description="Operations pertaining to items in Online Store")
class ItemController {

    private final ItemRepository repository;
    private final ItemResourceAssembler assembler;
    
    @Autowired
    ItemController(ItemRepository repository , ItemResourceAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    // Aggregate root
    @ApiOperation(value = "View a list of all available products",response = Resources.class,produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved items"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )

    @CrossOrigin
    @RequestMapping(value = "/items",method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, "application/hal+json"})
    Resources<Resource<Item>> all() {

        List<Resource<Item>> items = repository.findAll().stream()
                .map(assembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(items,
                linkTo(methodOn(ItemController.class).all()).withSelfRel());
    }

    @CrossOrigin
    @ApiOperation(value = "Adding a new item",produces = "application/json")
    @RequestMapping(value = "/items",method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, "application/hal+json"})
    ResponseEntity<?> newItem(@RequestBody Item newItem) throws URISyntaxException {

        Resource<Item> resource = assembler.toResource(repository.save(newItem));

        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    // Single item
    @CrossOrigin
    @ApiOperation(value = "View item by id",response = Resource.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved item"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @RequestMapping(value = "/items/{id}",method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, "application/hal+json"})
    Resource<Item> one(@PathVariable Long id) {

        Item item = repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));

        return assembler.toResource(item);
    }

    @CrossOrigin
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

    @CrossOrigin
    @ApiOperation(value = "Delete item")
    @DeleteMapping("/items/{id}")
    ResponseEntity<?> deleteItem(@PathVariable Long id) {

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}