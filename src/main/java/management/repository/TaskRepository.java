package management.repository;

import management.model.Task;
import management.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    Page<Task> findByUser (User user, Pageable pageable);

}
