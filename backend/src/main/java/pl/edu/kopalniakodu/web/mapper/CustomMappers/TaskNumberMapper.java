package pl.edu.kopalniakodu.web.mapper.CustomMappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;
import pl.edu.kopalniakodu.domain.Task;
import pl.edu.kopalniakodu.web.model.TaskDto;

import java.util.List;

@Component
public class TaskNumberMapper {

    ////We have to use TaskDto.TaskDtoBuilder because of Lombok @Builder
    @AfterMapping
    public void calcTaskNumber(Task task, @MappingTarget TaskDto.TaskDtoBuilder taskDtoBuilder) {

        List<Task> taskList = task.getOwner().getTasks();
        int indexOfTask = taskList.indexOf(task);
        taskDtoBuilder.taskNumber(++indexOfTask);

    }
}
