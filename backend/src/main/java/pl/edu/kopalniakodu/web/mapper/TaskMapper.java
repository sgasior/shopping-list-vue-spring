package pl.edu.kopalniakodu.web.mapper;

import org.mapstruct.Mapper;
import pl.edu.kopalniakodu.domain.Task;
import pl.edu.kopalniakodu.web.model.TaskDto;

@Mapper
public interface TaskMapper {

    TaskDto TaskToTaskDto(Task task);

    Task TaskDtoToTask(TaskDto dto);
}
