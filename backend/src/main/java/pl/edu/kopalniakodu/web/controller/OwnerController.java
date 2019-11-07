package pl.edu.kopalniakodu.web.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.kopalniakodu.domain.Owner;
import pl.edu.kopalniakodu.exceptions.OwnerNotFoundException;
import pl.edu.kopalniakodu.service.OwnerService;
import pl.edu.kopalniakodu.web.model.OwnerDto;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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
        return new ResponseEntity<>(ownerDto, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<OwnerDto>> getOwners() {
        return new ResponseEntity<>(ownerService.findAllOwners(), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<?> addOwner(@RequestBody @Valid OwnerDto ownerDto) {
        ownerService.add(ownerDto);
        return new ResponseEntity<>(ownerDto, HttpStatus.CREATED);
    }

    @PutMapping("/{ownerId}")
    public ResponseEntity<?> updateOwner(
            @PathVariable("ownerId") String ownerId,
            @RequestBody @Valid OwnerDto updatedOwner
    ) {
        Owner owner = getOwnerEntityOptional(ownerId)
                .orElseThrow(() -> new OwnerNotFoundException(ownerId));
        ownerService.update(owner, updatedOwner);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
}
