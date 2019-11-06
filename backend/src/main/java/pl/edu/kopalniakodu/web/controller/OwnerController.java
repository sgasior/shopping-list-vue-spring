package pl.edu.kopalniakodu.web.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.kopalniakodu.domain.Owner;
import pl.edu.kopalniakodu.service.OwnerService;
import pl.edu.kopalniakodu.web.model.OwnerDto;

import java.util.List;
import java.util.Optional;

@RequestMapping("/api/v1/owner")
@RestController
@RequiredArgsConstructor
@Log4j2
public class OwnerController {

    private final OwnerService ownerService;

    // example: http://localhost:8088/api/v1/owner/6d19d9bd-deef-4e3d-958d-4d63c8a0f077
    @GetMapping("/{ownerId}")
    public ResponseEntity<?> getOwner(@PathVariable("ownerId") String ownerId) {
        Optional<OwnerDto> ownerOptional = getOwnerDtoOptional(ownerId);
        if (ownerOptional.isEmpty()) {
            return new ResponseEntity<>(
                    "Unable to found. Owner with " + ownerId + " does not exists.",
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ownerOptional.get(), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<OwnerDto>> getOwners() {
        return new ResponseEntity<>(ownerService.findAllOwners(), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<?> addOwner(@RequestBody OwnerDto ownerDto) {
        ownerService.add(ownerDto);
        return new ResponseEntity<>(ownerDto, HttpStatus.CREATED);
    }

    @PutMapping("/{ownerId}")
    public ResponseEntity<?> updateOwner(
            @PathVariable("ownerId") String ownerId,
            @RequestBody OwnerDto updatedOwner
    ) {
        Optional<Owner> ownerOptional = getOwnerOptional(ownerId);
        if (ownerOptional.isEmpty()) {
            return new ResponseEntity<>(
                    "Unable to update. Owner with " + ownerId + " does not exists.",
                    HttpStatus.NOT_FOUND);
        }
        ownerService.update(ownerOptional.get(), updatedOwner);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{ownerId}")
    public ResponseEntity<?> deleteOwner(@PathVariable("ownerId") String ownerId) {
        Optional<Owner> ownerOptional = getOwnerOptional(ownerId);
        if (ownerOptional.isEmpty()) {
            return new ResponseEntity<>(
                    "Unable to delete. Owner with " + ownerId + " does not exists.",
                    HttpStatus.NOT_FOUND);
        }
        ownerService.delete(ownerOptional.get());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    private Optional<OwnerDto> getOwnerDtoOptional(String ownerId) {
        return ownerService
                .findDtoById(ownerId);
    }

    private Optional<Owner> getOwnerOptional(String ownerId) {
        return ownerService
                .findById(ownerId);
    }
}
