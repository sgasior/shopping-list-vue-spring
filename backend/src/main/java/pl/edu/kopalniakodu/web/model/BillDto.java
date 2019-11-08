package pl.edu.kopalniakodu.web.model;

import lombok.*;
import pl.edu.kopalniakodu.domain.BilanceType;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillDto {

    BilanceType bilanceType;

    BigDecimal amount;

}
