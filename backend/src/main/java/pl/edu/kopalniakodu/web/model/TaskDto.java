package pl.edu.kopalniakodu.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto extends RepresentationModel<TaskDto> {

    @Size(min = 3, max = 50)
    private String taskTitle;

    @Null
    @JsonFormat(pattern = "yyyy-MM-dd HH.mm")
    private LocalDateTime createdDate;

    private Boolean isDone;

    @Null
    private Integer taskNumber;
}
