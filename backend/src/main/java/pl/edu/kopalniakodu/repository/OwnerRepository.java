package pl.edu.kopalniakodu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.kopalniakodu.domain.Owner;

import java.util.UUID;

public interface OwnerRepository extends JpaRepository<Owner, UUID> {
}
