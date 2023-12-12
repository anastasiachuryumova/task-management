package management.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import management.model.Task;
import management.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import management.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;

    public Task save (Task task) {
        log.info("saveTask() method invoke");
        return taskRepository.save(task);
    }

    public Optional<Task> deleteById (Integer id) {
        log.info("deleteTask() method invoke");
        if (taskRepository.findById(id).isPresent()) {
            taskRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("no task with id " + id + " was found");
        }
        taskRepository.deleteById(id);
        return Optional.empty();
    }

    public Optional <Task> findByID (Integer id) {
        log.info("findTask() method invoke");
        return Optional.of(taskRepository.findById(id)).orElseThrow(() -> new EntityNotFoundException(
                "no task with id " + id + " was found"));
    }

    public Page<Task> findByUser (User user, Pageable pageable) {
        log.info("findByUser() method invoke");
        return taskRepository.findByUser(user, pageable);
    }

    public Page<Task> getTasks (int page, int size) {

        Pageable pageRequest = createPageRequestUsing(page, size);

        List<Task> allTasks = taskRepository.findAll();
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), allTasks.size());

        List<Task> pageContent = allTasks.subList(start, end);
        return new PageImpl<>(pageContent, pageRequest, allTasks.size());
    }

    private Pageable createPageRequestUsing(int page, int size) {
        return PageRequest.of(page, size);
    }

}
