package pl.edu.kopalniakodu.web.model;

import lombok.*;
import pl.edu.kopalniakodu.domain.Task;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerDto {

    private UUID id;

    private String name;

    private Set<Task> tasks;
}
