package management.service;

import management.model.Task;
import management.model.User;
import management.model.UserRole;
import management.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PageableServiceTest {

    private static final List<Task> ALL_TASKS = new ArrayList<>();
    private static Object PAGE_1_CONTENTS;
    private static Object PAGE_2_CONTENTS;
    private static Object PAGE_3_CONTENTS;
    private static Object PAGE_4_CONTENTS;
    private static Object EMPTY_PAGE;
    @Mock
    private TaskRepository taskRepository;
    @InjectMocks
    private TaskService taskService;
    private Task task;
    private User user;
    private UserRole userRole;
    private Task.Priority priority;
    private Task.Status status;
    private final List<String> expectedNames = Collections.singletonList("uefgiw");

    @BeforeEach
    void setup() {
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
        taskRepository.save(task);
        taskService.save(task);
        when(taskRepository.findAll()).thenReturn(ALL_TASKS);
    }

    @ParameterizedTest
    @MethodSource("testIO")
    void getTasks_whenGetTasks_thenReturnsDesiredDataAlongWithPagingInformation(int page, int size, List<String> expectedNames, long expectedTotalElements, long expectedTotalPages) {
        Page<Task> tasks = taskService.getTasks(page, size);
        List<String> namesOne = new ArrayList<>();
        List<String> names = tasks.getContent()
                .stream()
                .map(Task::getTitle)
                .collect(Collectors.toList());

        assertEquals(0, names.size());
        assertEquals(namesOne, names);
        assertEquals(0, tasks.getTotalElements());
        assertEquals(0, tasks.getTotalPages());}

    private static Collection<Object[]> testIO() {
        return Arrays.asList(
                new Object[][] {
                        { 0, 1, PAGE_1_CONTENTS, 20L, 4L },
                        { 0, 1, PAGE_2_CONTENTS, 20L, 4L },
                        { 0, 1, PAGE_3_CONTENTS, 20L, 4L },
                        { 0, 1, PAGE_3_CONTENTS, 20L, 4L },
                        { 0, 1, EMPTY_PAGE, 20L, 4L }}
        );
    }
}
