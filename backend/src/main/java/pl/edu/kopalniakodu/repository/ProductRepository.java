package pl.edu.kopalniakodu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.kopalniakodu.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
