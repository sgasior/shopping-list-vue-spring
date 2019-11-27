package pl.edu.kopalniakodu.domain;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"tasks", "name"})
@ToString(exclude = {"tasks", "id"})
public class Owner {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @org.hibernate.annotations.Type(type = "org.hibernate.type.UUIDCharType")
    private UUID id;


/*    @PrePersist
    protected void onCreateEntity() {
        this.uuid = UUID.randomUUID();
    }*/

    private String name;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "owner"
    )
    private List<Task> tasks = new ArrayList<>();

}
