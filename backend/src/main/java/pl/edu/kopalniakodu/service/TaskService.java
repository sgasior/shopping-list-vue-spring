package pl.edu.kopalniakodu.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pl.edu.kopalniakodu.domain.Owner;
import pl.edu.kopalniakodu.domain.Task;
import pl.edu.kopalniakodu.exceptions.TaskNotFoundException;
import pl.edu.kopalniakodu.repository.OwnerRepository;
import pl.edu.kopalniakodu.repository.TaskRepository;
import pl.edu.kopalniakodu.web.mapper.TaskMapper;
import pl.edu.kopalniakodu.web.model.TaskDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class TaskService {

    private final OwnerService ownerService;
    private final OwnerRepository ownerRepository;
    private final TaskRepository taskRepository;
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

        Task taskEntity = getTaskByOwnerIdAndTaskNumber(ownerId, taskNumber);
        TaskDto taskDto = taskMapper.taskToTaskDto(taskEntity);
        return taskDto;
    }

    public TaskDto add(TaskDto taskDto, String ownerId) {
        Owner owner = ownerService.findById(ownerId);
        Task taskToBeSaved = taskMapper.taskDtoToTask(taskDto);
        taskToBeSaved.setCreatedDate(LocalDateTime.now());
        if (taskDto.getIsDone() == null) {
            taskToBeSaved.setIsDone(false);
        }
        taskToBeSaved.setOwner(owner);
        Task savedTask = taskRepository.save(taskToBeSaved);
        return taskMapper.taskToTaskDto(savedTask);
    }

    public TaskDto update(String ownerId, int taskNumber, TaskDto updatedTask) {
        Task taskEntity = getTaskByOwnerIdAndTaskNumber(ownerId, taskNumber);
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(updatedTask, taskEntity);
        Task savedTask = taskRepository.save(taskEntity);
        return taskMapper.taskToTaskDto(savedTask);
    }

    public void delete(String ownerId, int taskNumber) {
        Task taskEntity = getTaskByOwnerIdAndTaskNumber(ownerId, taskNumber);
        taskRepository.delete(taskEntity);
    }

    private Task getTaskByOwnerIdAndTaskNumber(String ownerId, int taskNumber) {
        Owner owner = ownerService.findById(ownerId);
        List<Task> taskList = ownerRepository.findAllTasks(owner.getId());
        if (taskList.size() < taskNumber || taskNumber <= 0) {
            throw new TaskNotFoundException(taskNumber);
        }
        return taskList.get(taskNumber - 1);
    }
}
