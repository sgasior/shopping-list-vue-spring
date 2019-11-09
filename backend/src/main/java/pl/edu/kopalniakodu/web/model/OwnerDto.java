package pl.edu.kopalniakodu.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerDto {

    @Null
    @JsonIgnore
    private UUID id;

    @NotBlank
    @Size(min = 3, max = 20)
    private String name;

}
