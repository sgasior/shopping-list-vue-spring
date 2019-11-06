package pl.edu.kopalniakodu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.kopalniakodu.domain.Bill;

public interface BillRepository extends JpaRepository<Bill, Long> {
}
