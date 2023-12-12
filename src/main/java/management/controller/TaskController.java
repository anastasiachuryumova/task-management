package management.controller;

import management.config.TaskProperties;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import management.model.Task;
import management.model.User;
import org.springdoc.api.ErrorMessage;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import management.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskProperties taskProperties;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask (@RequestBody @Validated Task task, @AuthenticationPrincipal User user) {
        task.setUser(user);
        return taskService.save(task);
    }

    @PatchMapping("/edit/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Task editTask (@PathVariable("id") Integer id, @AuthenticationPrincipal User user) {
        taskService.findByID(id).orElseThrow(() -> new EntityNotFoundException(
                "no task with id " + id + " was found")).setUser(user);
        return taskService.save(taskService.findByID(id).orElseThrow(() -> new EntityNotFoundException(
                "no task with id " + id + " was found")));
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete (@PathVariable("id") Integer id, @AuthenticationPrincipal User user) {
        taskService.findByID(id).orElseThrow(() -> new EntityNotFoundException(
                "no task with id " + id + " was found")).setUser(user);
        taskService.deleteById(id);
    }

    @GetMapping("/find/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Task findTask (@PathVariable("id") Integer id, @AuthenticationPrincipal User user) {
        taskService.findByID(id).orElseThrow(() -> new EntityNotFoundException(
                "no task with id " + id + " was found")).setUser(user);
        return taskService.findByID(id).orElseThrow(() -> new EntityNotFoundException(
                "no task with id " + id + " was found"));
    }

    @GetMapping("/findByUser/{user}")
    @ResponseStatus(HttpStatus.OK)
    public List<Task> findByUser (@PathVariable User user) {
        Pageable pageable = PageRequest.of(0, taskProperties.getPageSize());
        return taskService.findByUser(user, pageable).getContent();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public ErrorMessage handleException(ChangeSetPersister.NotFoundException exception) {
        return new ErrorMessage(exception.getMessage());
    }

    @GetMapping("/getTasks")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<Task>> getTasks(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam Integer userId) {

        Page<Task> taskPage = taskService.getTasks(page, size);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Page-Number", String.valueOf(taskPage.getNumber()));
        headers.add("X-Page-Size", String.valueOf(taskPage.getSize()));

        return ResponseEntity.ok()
                .headers(headers)
                .body(taskPage);
    }
}
