package pl.edu.kopalniakodu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.kopalniakodu.domain.Owner;
import pl.edu.kopalniakodu.domain.Task;

import java.util.List;
import java.util.UUID;

public interface OwnerRepository extends JpaRepository<Owner, UUID> {

    @Query("SELECT o.tasks FROM Owner o WHERE o.id=:UUID")
    List<Task> findAllTasks(@Param("UUID") UUID ownerUUID);
}
