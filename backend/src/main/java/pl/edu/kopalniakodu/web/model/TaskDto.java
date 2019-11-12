package pl.edu.kopalniakodu.web.model;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.sql.Timestamp;


@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto extends RepresentationModel<TaskDto> {

    @Null
    private Long id;

    @NonNull
    @Size(min = 3, max = 50)
    private String taskTitle;

    @NonNull
    private Timestamp createdDate;

    @NonNull
    private Boolean isDone;

}
