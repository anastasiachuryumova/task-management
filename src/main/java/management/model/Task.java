package management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@Entity
@AllArgsConstructor
@Table(name = "tasks")
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "priority", nullable = false)
    private Priority priority;

    @ManyToOne
    private User user;

    public enum Status {
        WAITING, IN_PROGRESS, DONE
    }

    public enum Priority {
        HIGH, MEDIUM, LOW
    }

}
