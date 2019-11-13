package pl.edu.kopalniakodu.web.mapper;

import org.mapstruct.*;
import pl.edu.kopalniakodu.domain.Task;
import pl.edu.kopalniakodu.web.mapper.CustomMappers.TaskNumberMapper;
import pl.edu.kopalniakodu.web.model.TaskDto;

@Mapper(uses = TaskNumberMapper.class)
public interface TaskMapper {

    TaskDto taskToTaskDto(Task task);

    Task taskDtoToTask(TaskDto dto);
}
