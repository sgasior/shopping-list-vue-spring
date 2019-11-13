package pl.edu.kopalniakodu.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pl.edu.kopalniakodu.domain.Owner;
import pl.edu.kopalniakodu.domain.Task;
import pl.edu.kopalniakodu.exceptions.OwnerNotFoundException;
import pl.edu.kopalniakodu.exceptions.TaskNotFoundException;
import pl.edu.kopalniakodu.repository.OwnerRepository;
import pl.edu.kopalniakodu.web.mapper.TaskMapper;
import pl.edu.kopalniakodu.web.model.TaskDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class TaskService {

    private final OwnerService ownerService;
    private final OwnerRepository ownerRepository;
    private final TaskMapper taskMapper;

    public Optional<List<TaskDto>> findAllTasks(String ownerId) {

        Optional<Owner> ownerOptional = ownerService.findById(ownerId);

        if (!ownerService.findById(ownerId).isPresent()) {
            return Optional.empty();
        }

        List<TaskDto> returnValue = new ArrayList<>();

        for (Task taskEntity : ownerRepository.findAllTasks(UUID.fromString(ownerId))) {
            TaskDto taskDto = taskMapper.taskToTaskDto(taskEntity);
            returnValue.add(taskDto);
        }

        return Optional.of(returnValue);
    }

    public TaskDto findTask(String ownerId, int taskNumber) {

        Owner owner = ownerService.findById(ownerId).orElseThrow(
                () -> new OwnerNotFoundException(ownerId)
        );

        List<Task> taskList = ownerRepository.findAllTasks(owner.getId());
        if (taskList.size() < taskNumber || taskNumber <= 0) {
            throw new TaskNotFoundException(taskNumber);
        }
        TaskDto taskDto = taskMapper.taskToTaskDto(taskList.get(taskNumber - 1));
        return taskDto;
    }
}
