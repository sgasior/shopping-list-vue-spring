package pl.edu.kopalniakodu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.kopalniakodu.domain.Owner;
import pl.edu.kopalniakodu.domain.Task;
import pl.edu.kopalniakodu.repository.OwnerRepository;
import pl.edu.kopalniakodu.web.mapper.TaskMapper;
import pl.edu.kopalniakodu.web.model.TaskDto;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final TaskMapper taskMapper;

    public Set<TaskDto> findAllTasks(String ownerId) {

        UUID ownerUUID = UUID.fromString(ownerId);
        Optional<Owner> ownerOptional = ownerRepository.findById(ownerUUID);
        if (ownerOptional.isPresent()) {
            Set<Task> foundTasks = ownerOptional.get().getTasks();
            Set<TaskDto> taskDtos = new LinkedHashSet<>();
            for (Task task : foundTasks) {
                taskDtos.add(taskMapper.TaskToTaskDto(task));
            }
            return taskDtos;
        } else {
            return null;
        }
    }

    public List<Owner> findAllOwners() {
        return ownerRepository.findAll();
    }
}
