import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private String description;
    private TeamMember assignedMember;
    private TaskStatus status;
    private LocalDateTime assignedTime;
    private LocalDateTime completedTime;

    public Task(String description) {
        this.description = description;
        this.status = TaskStatus.DO_ZROBIENIA; // Ustawienie domyślnego statusu
    }

    public String getDescription() {
        return description;
    }

    public TeamMember getAssignedMember() {
        return assignedMember;
    }

    public void setAssignedMember(TeamMember assignedMember) {
        this.assignedMember = assignedMember;
        this.assignedTime = LocalDateTime.now();
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
        if (status == TaskStatus.ZAKONCZONE) {
            this.completedTime = LocalDateTime.now();
        }
    }

    public LocalDateTime getAssignedTime() {
        return assignedTime;
    }

    public LocalDateTime getCompletedTime() {
        return completedTime;
    }

    public long getTimeSpent() {
        if (assignedTime != null && completedTime != null) {
            Duration duration = Duration.between(assignedTime, completedTime);
            return duration.getSeconds();
        }
        return 0;
    }

    @Override
    public String toString() {
        String assignedTo = (assignedMember != null) ? assignedMember.getName() : "Nikt";
        return description + " (" + status.translate() + ") - Przypisane do: " + assignedTo;
    }
}