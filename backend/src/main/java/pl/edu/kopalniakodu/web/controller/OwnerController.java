package pl.edu.kopalniakodu.web.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.kopalniakodu.domain.Owner;
import pl.edu.kopalniakodu.exceptions.ApiError;
import pl.edu.kopalniakodu.exceptions.OwnerNotFoundException;
import pl.edu.kopalniakodu.service.OwnerService;
import pl.edu.kopalniakodu.web.model.OwnerDto;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequestMapping("/api/v1/owner")
@RestController
@RequiredArgsConstructor
@Log4j2
public class OwnerController {

    private final OwnerService ownerService;

    @GetMapping("/{ownerId}")
    public ResponseEntity<?> getOwner(@PathVariable("ownerId") String ownerId) {
        OwnerDto ownerDto = getOwnerDtoOptional(ownerId)
                .orElseThrow(() -> new OwnerNotFoundException(ownerId));

        addLinksToOwnerDto(ownerDto);
        return new ResponseEntity<>(ownerDto, HttpStatus.OK);
    }

    @GetMapping(value = "")
    public ResponseEntity<CollectionModel<OwnerDto>> getOwners() {
        List<OwnerDto> owners = ownerService.findAllOwners();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Owners-Total", Integer.toString(owners.size()));

        addLinksToEachOwnerDto(owners);
        Link mainSelfLink = linkTo(OwnerController.class).withSelfRel();
        return new ResponseEntity<>(
                new CollectionModel<>(owners, mainSelfLink),
                headers,
                HttpStatus.OK
        );
    }


    @PostMapping("")
    public ResponseEntity<?> addOwner(@RequestBody @Valid OwnerDto ownerDto) {
        ownerDto = ownerService.add(ownerDto);

        addLinksToOwnerDto(ownerDto);
        return new ResponseEntity<>(ownerDto, HttpStatus.CREATED);
    }

    @PutMapping("/{ownerId}")
    public ResponseEntity<?> updateOwner(
            @PathVariable("ownerId") String ownerId,
            @RequestBody @Valid OwnerDto updatedOwner
    ) {
        Owner owner = getOwnerEntityOptional(ownerId)
                .orElseThrow(() -> new OwnerNotFoundException(ownerId));
        updatedOwner = ownerService.update(owner, updatedOwner);
        addLinksToOwnerDto(updatedOwner);
        return new ResponseEntity<>(updatedOwner, HttpStatus.OK);
    }

    @DeleteMapping("/{ownerId}")
    public ResponseEntity<?> deleteOwner(@PathVariable("ownerId") String ownerId) {
        Owner owner = getOwnerEntityOptional(ownerId)
                .orElseThrow(() -> new OwnerNotFoundException(ownerId));
        ownerService.delete(owner);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    private Optional<OwnerDto> getOwnerDtoOptional(String ownerId) {
        return ownerService
                .findDtoById(ownerId);
    }

    private Optional<Owner> getOwnerEntityOptional(String ownerId) {
        return ownerService
                .findById(ownerId);
    }

    @ExceptionHandler(OwnerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<ApiError> ownerNotFoundExceptionHandler(OwnerNotFoundException ex) {
        return Collections.singletonList(new ApiError("owner.notfound", ex.getMessage()));
    }

    private OwnerDto addLinksToOwnerDto(OwnerDto ownerDto) {
        ownerDto.add(linkTo(OwnerController.class).slash(ownerDto.getId()).withSelfRel());
        ownerDto.add(linkTo(OwnerController.class).withRel("owners"));
        return ownerDto;
    }

    private void addLinksToEachOwnerDto(List<OwnerDto> owners) {
        owners.stream().map(owner -> addLinksToOwnerDto(owner))
                .collect(Collectors.toList());
    }

}
