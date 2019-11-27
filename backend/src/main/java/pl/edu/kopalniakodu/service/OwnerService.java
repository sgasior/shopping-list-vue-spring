package pl.edu.kopalniakodu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pl.edu.kopalniakodu.domain.Owner;
import pl.edu.kopalniakodu.exceptions.OwnerNotFoundException;
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

    public OwnerDto findDtoById(String ownerId) {
        Optional<Owner> ownerEntity = getOwnerEntity(ownerId);
        if (ownerEntity.isEmpty()) {
            throw new OwnerNotFoundException(ownerId);
        } else {
            return ownerMapper.ownerToOwnerDto(ownerEntity.get());
        }
    }

    public Owner findById(String ownerId) {
        Optional<Owner> ownerEntity = getOwnerEntity(ownerId);
        if (ownerEntity.isEmpty()) {
            throw new OwnerNotFoundException(ownerId);
        } else {
            return ownerEntity.get();
        }
    }

    public void delete(Owner owner) {
        ownerRepository.delete(owner);
    }

    public OwnerDto add(OwnerDto ownerDto) {
        Owner savedOwner = ownerRepository.save(ownerMapper.ownerDtoToOwner(ownerDto));
        ownerDto.setId(savedOwner.getId());
        return ownerDto;
    }

    public OwnerDto update(Owner owner, OwnerDto updatedOwner) {
        updatedOwner.setId(owner.getId());
        owner = ownerMapper.ownerDtoToOwner(updatedOwner);
        ownerRepository.save(owner);
        return updatedOwner;
    }

    private Optional<Owner> getOwnerEntity(String ownerId) {
        UUID ownerUUID;
        try {
            ownerUUID = UUID.fromString(ownerId);
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
        return ownerRepository.findById(ownerUUID);
    }

}
