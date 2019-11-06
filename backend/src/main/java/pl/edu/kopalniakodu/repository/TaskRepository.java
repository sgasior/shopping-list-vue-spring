package pl.edu.kopalniakodu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.kopalniakodu.domain.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
