package pl.edu.kopalniakodu.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pl.edu.kopalniakodu.domain.Owner;
import pl.edu.kopalniakodu.domain.Task;
import pl.edu.kopalniakodu.exceptions.TaskNotFoundException;
import pl.edu.kopalniakodu.repository.OwnerRepository;
import pl.edu.kopalniakodu.web.mapper.TaskMapper;
import pl.edu.kopalniakodu.web.model.TaskDto;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class TaskService {

    private final OwnerService ownerService;
    private final OwnerRepository ownerRepository;
    private final TaskMapper taskMapper;

    public List<TaskDto> findAllTasks(String ownerId) {

        Owner owner = ownerService.findById(ownerId);
        List<TaskDto> taskDtoList = new ArrayList<>();
        for (Task taskEntity : ownerRepository.findAllTasks(owner.getId())) {
            TaskDto taskDto = taskMapper.taskToTaskDto(taskEntity);
            taskDtoList.add(taskDto);
        }
        return taskDtoList;
    }

    public TaskDto findTask(String ownerId, int taskNumber) {

        Owner owner = ownerService.findById(ownerId);
        List<Task> taskList = ownerRepository.findAllTasks(owner.getId());
        if (taskList.size() < taskNumber || taskNumber <= 0) {
            throw new TaskNotFoundException(taskNumber);
        }
        TaskDto taskDto = taskMapper.taskToTaskDto(taskList.get(taskNumber - 1));
        return taskDto;
    }
}
