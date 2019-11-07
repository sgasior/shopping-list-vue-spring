package pl.edu.kopalniakodu.domain;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;
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

    @NonNull
    private String name;

    @OneToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "owner_id")
    private Set<Task> tasks = new LinkedHashSet<>();

}
