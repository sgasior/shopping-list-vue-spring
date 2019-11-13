package pl.edu.kopalniakodu.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"products", "bill"})
@ToString(exclude = {"products", "bill"})
public class Task extends BaseEntity {

    private String taskTitle;

    private LocalDateTime createdDate;

    private Boolean isDone;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "task_id")
    List<Product> products = new ArrayList<>();

    @OneToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "bill_id")
    private Bill bill;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private Owner owner;

}
