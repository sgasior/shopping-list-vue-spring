package pl.edu.kopalniakodu.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Task extends BaseEntity {

    @NonNull
    @Size(min = 3, max = 50)
    private String taskTitle;

    @NonNull
    private Timestamp createdDate;

    @NonNull
    private Boolean isDone;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "task_id")
    Set<Product> products = new LinkedHashSet<>();

    @OneToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "bill_id")
    private Bill bill;


}
