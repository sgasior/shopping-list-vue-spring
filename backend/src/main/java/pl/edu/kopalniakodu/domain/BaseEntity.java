package pl.edu.kopalniakodu.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity implements Serializable {


    //    @Id
//    @GenericGenerator(name = "uuid2", strategy = "uuid2")
//    @GeneratedValue(generator = "uuid2")
//    @Id
//    @GeneratedValue(generator = "UUID")
//    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
//    @Column(length = 36, columnDefinition = "varchar", updatable = false, nullable = false)
//    @Id
//    @GeneratedValue(generator = "UUID")
//    @GenericGenerator(
//            name = "UUID",
//            strategy = "org.hibernate.id.UUIDGenerator"
//    )
//    @Column(name = "ID", updatable = false, nullable = false)
//    @ColumnDefault("random_uuid()")
//    @Type(type = "uuid-char")
//    @Getter
//    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

}
