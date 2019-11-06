package pl.edu.kopalniakodu.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {

    @NonNull
    @Size(min = 3, max = 50)
    private String name;

}
