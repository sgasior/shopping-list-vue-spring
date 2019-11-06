package pl.edu.kopalniakodu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import pl.edu.kopalniakodu.domain.Owner;
import pl.edu.kopalniakodu.repository.OwnerRepository;
import pl.edu.kopalniakodu.web.mapper.OwnerMapper;
import pl.edu.kopalniakodu.web.mapper.TaskMapper;
import pl.edu.kopalniakodu.web.model.OwnerDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final TaskMapper taskMapper;
    private final OwnerMapper ownerMapper;

/*    public Optional<Set<TaskDto>> findAllTasks(String ownerId) {

        Optional<OwnerDto> ownerOptional = findDtoById(ownerId);
        if (ownerOptional.isPresent()) {
            Set<Task> foundTasks = ownerOptional.get().getTasks();
            Set<TaskDto> taskDtos = new LinkedHashSet<>();
            for (Task task : foundTasks) {
                taskDtos.add(taskMapper.taskToTaskDto(task));
            }
            return Optional.of(taskDtos);
        } else {
            return Optional.empty();
        }
    }*/

    public List<OwnerDto> findAllOwners() {

        List<OwnerDto> returnValue = new ArrayList<>();

        for (Owner ownerEntity : ownerRepository.findAll()) {
            OwnerDto ownerDto = ownerMapper.ownerToOwnerDto(ownerEntity);
            returnValue.add(ownerDto);
        }
        return returnValue;
    }

    public Optional<OwnerDto> findDtoById(String ownerId) {
        UUID ownerUUID = UUID.fromString(ownerId);
        Optional<Owner> ownerEntity = ownerRepository.findById(ownerUUID);
        if (!ownerEntity.isEmpty()) {
            return Optional.of(ownerMapper.ownerToOwnerDto(ownerEntity.get()));
        }
        return Optional.empty();
    }

    public Optional<Owner> findById(String ownerId) {
        UUID ownerUUID = UUID.fromString(ownerId);
        Optional<Owner> ownerEntity = ownerRepository.findById(ownerUUID);
        if (!ownerEntity.isEmpty()) {
            return ownerEntity;
        }
        return Optional.empty();
    }


    public void delete(Owner owner) {
        ownerRepository.delete(owner);
    }

    public void add(OwnerDto ownerDto) {
        ownerRepository.save(ownerMapper.ownerDtoToOwner(ownerDto));
    }


    public void update(Owner owner, OwnerDto updatedOwner) {
        updatedOwner.setId(owner.getId());
        BeanUtils.copyProperties(updatedOwner, owner);
        ownerRepository.save(owner);
    }


}
