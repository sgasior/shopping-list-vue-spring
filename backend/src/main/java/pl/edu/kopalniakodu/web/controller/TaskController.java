package pl.edu.kopalniakodu.web.controller;


//owner/1/task -> all tasks GET, POST
//owner/1/task/1 -> task 1 GET, DELETE, PUT

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.kopalniakodu.exceptions.ApiError;
import pl.edu.kopalniakodu.exceptions.OwnerNotFoundException;
import pl.edu.kopalniakodu.exceptions.TaskNotFoundException;
import pl.edu.kopalniakodu.service.TaskService;
import pl.edu.kopalniakodu.web.model.TaskDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequestMapping("/api/v1/owner")
@RestController
@RequiredArgsConstructor
@Log4j2
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/{ownerId}/task")
    public ResponseEntity<CollectionModel<TaskDto>> getTasks(@PathVariable("ownerId") String ownerId) {


        List<TaskDto> tasks = taskService.findAllTasks(ownerId)
                .orElseThrow(() -> new OwnerNotFoundException(ownerId));
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Tasks-Total", Integer.toString(tasks.size()));

        addLinksToEachTaskDto(tasks, ownerId);
        Link mainSelfLink = linkTo(TaskController.class).slash(ownerId).slash("task").withSelfRel();
        return new ResponseEntity<>(
                new CollectionModel<>(tasks, mainSelfLink),
                headers,
                HttpStatus.OK
        );
    }

    @GetMapping("/{ownerId}/task/{taskNumber}")
    public ResponseEntity<?> getTask(
            @PathVariable("ownerId") String ownerId,
            @PathVariable("taskNumber") int taskNumber
    ) {
        TaskDto taskDto = taskService.findTask(ownerId, taskNumber);
        return new ResponseEntity<>(taskDto, HttpStatus.HTTP_VERSION_NOT_SUPPORTED);
    }


    @ExceptionHandler(OwnerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<ApiError> ownerNotFoundExceptionHandler(OwnerNotFoundException ex) {
        return Collections.singletonList(new ApiError("owner.notfound", ex.getMessage()));
    }

    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<ApiError> taskNotFoundExceptionHandler(TaskNotFoundException ex) {
        return Collections.singletonList(new ApiError("task.notfound", ex.getMessage()));
    }

    private TaskDto addLinksToTaskDto(TaskDto taskDto, String ownerId) {
        taskDto.add(linkTo(TaskController.class).slash(ownerId).withRel("owner"));
        taskDto.add(linkTo(TaskController.class).slash(ownerId).slash("task").withRel("tasks"));
        return taskDto;
    }

    private void addLinksToEachTaskDto(List<TaskDto> tasks, String ownerId) {
        tasks.stream().map(taskDto -> addLinksToTaskDto(taskDto, ownerId))
                .collect(Collectors.toList());
    }
}
