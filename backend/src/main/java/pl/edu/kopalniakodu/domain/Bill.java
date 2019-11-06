package pl.edu.kopalniakodu.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Bill extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @NonNull
    BilanceType bilanceType;

    @NonNull
    @Positive
    BigDecimal amount;

}
