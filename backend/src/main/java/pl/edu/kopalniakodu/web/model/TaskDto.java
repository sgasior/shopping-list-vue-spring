package pl.edu.kopalniakodu.web.model;

import lombok.*;
import pl.edu.kopalniakodu.domain.Bill;
import pl.edu.kopalniakodu.domain.Product;

import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.LinkedHashSet;


@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {

    @NonNull
    private Long id;

    @NonNull
    @Size(min = 3, max = 50)
    private String taskTitle;

    @NonNull
    private Timestamp createdDate;

    @NonNull
    private Boolean isDone;

    LinkedHashSet<Product> products;
    private Bill bill;

}
