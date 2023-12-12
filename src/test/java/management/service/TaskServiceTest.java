package management.service;

import jakarta.persistence.EntityNotFoundException;
import management.model.Task;
import management.model.User;
import management.model.UserRole;
import management.repository.TaskRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatRuntimeException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TaskServiceTest {

    private static final List<Task> ALL_TASKS = new ArrayList<>();
    private static Object PAGE_1_CONTENTS;
    private static Object PAGE_2_CONTENTS;
    private static Object PAGE_3_CONTENTS;
    private static Object PAGE_4_CONTENTS;
    private static Object EMPTY_PAGE;
    @Mock
    private TaskRepository taskRepository;
    private Task task;
    private Task taskOne;
    private Task taskTwo;
    private Task taskThree;
    private User user;
    private UserRole userRole;
    private Task.Priority priority;
    private Task.Status status;
    AutoCloseable autoCloseable;
    private final Integer id = 7;
    private final Integer idTwo = 15;
    private final Integer idThree = 125;
    private TaskService taskService;
    private Page<Task[]> page;

    @BeforeEach()
    void setUp() {

        autoCloseable = MockitoAnnotations.openMocks(this);
        //use a new, clean taskRepository mock for each test
        taskRepository = mock(TaskRepository.class);
        taskService = mock(TaskService.class);

        user = User.builder()
                .id(8)
                .email("78563")
                .firstName("ssygs")
                .lastName("483hfus")
                .password("36273")
                .isRemoved(false)
                .login("74534")
                .lastVisit(LocalDateTime.now())
                .roles(Collections.singleton(userRole)).build();

        task = Task.builder()
                .id(1)
                .user(user)
                .description("iruheiruhg")
                .priority(priority)
                .status(status)
                .title("krugekgu")
                .build();

        taskOne = Task.builder()
                .id(1)
                .user(user)
                .description("iruheiruhg")
                .priority(priority)
                .status(status)
                .title("krugekgu")
                .build();

        taskTwo = Task.builder()
                .id(1)
                .user(user)
                .description("iruheiruhg")
                .priority(priority)
                .status(status)
                .title("krugekgu")
                .build();

        taskThree = Task.builder()
                .id(15)
                .user(user)
                .description("iruheiruhg")
                .priority(priority)
                .status(status)
                .title("krugekgu")
                .build();


        taskRepository.save(task);
        taskRepository.save(taskOne);
        taskRepository.save(taskThree);
        taskRepository.findAll();

        Task[] strArray = new Task[]{task, taskOne, taskThree};
        List<Task[]> data = new ArrayList<>();
        data.add(strArray);

        Page<Task[]> page = new PageImpl<Task[]>(data);
    }

    @Test
    @DisplayName("If save method was successful the task should be in a repository")
    void saveTask_shouldSaveTask() {
        //ARRANGE
        when(taskRepository.findById(any(Integer.class)))
                .thenReturn(Optional.of(task));

        //ACT
        when(taskRepository.save(any(Task.class)))
                .thenReturn(task);

        //VERIFY
        verify(taskRepository, times(3)).save(any(Task.class));
        assertThat(taskTwo).usingRecursiveComparison().isEqualTo(taskRepository.save(taskOne));
    }

    @Test
    @DisplayName("If the task is not found then throw an exception")
    void findById_ifNotSuccess_thenThrow() {
        //ARRANGE
        when(taskRepository.findById(id)).thenThrow(EntityNotFoundException.class);

        //ACT
        ThrowableAssert.ThrowingCallable getTask = () -> taskService.findByID(id).orElseThrow();

        //VERIFY
        Assertions.assertThatThrownBy(this::findById_ifNotSuccess_thenThrow).isInstanceOf(EntityNotFoundException.class);
        Assertions.assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(getTask);
    }

    @Test
    @DisplayName("If the task is found it should return task")
    void findById_shouldReturnTask () {
        //ARRANGE
        when(taskRepository.findById(id))
                .thenReturn(Optional.ofNullable(task));
        when(taskService.findByID(id))
                .thenReturn(Optional.ofNullable(taskTwo));

        //ACT
        Optional<Task> result = taskService.findByID(id);

        //VERIFY
        assertThat(result).isEqualTo(Optional.ofNullable(task));
    }

    @Test
    @DisplayName("If delete successful return no content")
    void deleteById_shouldReturnNoContent () {
        //ARRANGE
        when(taskRepository.findById(idTwo)).thenReturn(Optional.of(taskThree));

        //ACT
        taskRepository.deleteById(idTwo);

        //VERIFY
        verify(taskRepository, times(1)).deleteById(idTwo);
    }

    @Test
    @DisplayName("If delete is successful then throw Runtime Exception")
    void deleteById_ifNotSuccess_thenThrow() {
        //ARRANGE
        when(taskRepository.findById(idThree)).thenThrow(EntityNotFoundException.class);

        //ACT
        ThrowableAssert.ThrowingCallable deleteTask = () ->
                taskService.deleteById(idThree).orElseThrow();

        //VERIFY
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(deleteTask);
        assertThatRuntimeException().isThrownBy(deleteTask);
        Assertions.assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(deleteTask);
        verify(taskRepository, never()).delete(task);
    }

    @Test
    @DisplayName("If the task is not found then throw an exception")
    void findByUser_ifNotSuccess_thenThrow() {
        //ARRANGE
        when(taskRepository.findByUser(user, Pageable.unpaged())).thenThrow(EntityNotFoundException.class);

        //ACT
        ThrowableAssert.ThrowingCallable getTask = () ->
                taskService.findByUser(user, Pageable.unpaged()).getTotalElements();

        //VERIFY
        Assertions.assertThatThrownBy(this::findByUser_ifNotSuccess_thenThrow).isInstanceOf(EntityNotFoundException.class);
        Assertions.assertThatExceptionOfType(NullPointerException.class).isThrownBy(getTask);
    }

    @Test
    @DisplayName("If the task is found it should return task")
    void findByUser_shouldReturnTask () {
        //ARRANGE
        when(taskRepository.findByUser(user, Pageable.unpaged())).thenThrow(EntityNotFoundException.class);

        //ACT
        List<Task> result = (List<Task>) taskService.findByUser(user, Pageable.unpaged());
        System.out.println(result);

        //VERIFY
        assertThat(result).isEqualTo(page);
    }

    @Test
    @DisplayName("If the task is not found then throw an exception")
    void getTasks_ifNotSuccess_thenThrow() {
        //ARRANGE
        when(taskRepository.findById(user.getId())).thenThrow(EntityNotFoundException.class);

        //ACT
        ThrowableAssert.ThrowingCallable getTask = () ->
                taskService.getTasks(1,1);

        //VERIFY
        Assertions.assertThatThrownBy(this::getTasks_ifNotSuccess_thenThrow).isInstanceOf(EntityNotFoundException.class);
    }

}

