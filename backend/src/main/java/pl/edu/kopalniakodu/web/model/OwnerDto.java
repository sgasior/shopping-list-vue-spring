package pl.edu.kopalniakodu.web.model;

import lombok.*;
import pl.edu.kopalniakodu.domain.Task;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerDto {

    @Null
    private UUID id;

    @NotBlank
    @Size(min = 3, max = 20)
    private String name;

    private Set<Task> tasks;
}
