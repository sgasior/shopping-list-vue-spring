package pl.edu.kopalniakodu.web.model;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto extends RepresentationModel<ProductDto> {

    @NotBlank
    @Size(min = 3, max = 50)
    private String name;

    @Null
    private Integer productNumber;

    private Boolean isDone;
}
