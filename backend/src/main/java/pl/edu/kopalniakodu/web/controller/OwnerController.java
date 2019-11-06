package pl.edu.kopalniakodu.web.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.kopalniakodu.domain.Owner;
import pl.edu.kopalniakodu.service.OwnerService;
import pl.edu.kopalniakodu.web.model.TaskDto;

import java.util.List;
import java.util.Set;

@RequestMapping("/api/v1/owner")
@RestController
@RequiredArgsConstructor
@Log4j2
public class OwnerController {


    private final OwnerService ownerService;

    // example: http://localhost:8088/api/v1/task/6d19d9bd-deef-4e3d-958d-4d63c8a0f077
    @GetMapping("/{ownerId}")
    public ResponseEntity<Set<TaskDto>> getTasks(@PathVariable("ownerId") String ownerId) {

        return new ResponseEntity<>(ownerService.findAllTasks(ownerId), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<Owner>> getOwners() {
        return new ResponseEntity<>(ownerService.findAllOwners(), HttpStatus.OK);
    }


}
