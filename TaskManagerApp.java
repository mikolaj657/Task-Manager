import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TaskManagerApp extends Application {
    private ObservableList<TeamMember> teamMembers;
    private ObservableList<Task> tasks;

    @Override
    public void start(Stage primaryStage) {
        teamMembers = FXCollections.observableArrayList();
        tasks = FXCollections.observableArrayList();

        // ListViews
        ListView<TeamMember> teamListView = new ListView<>(teamMembers);
        ListView<Task> taskListView = new ListView<>(tasks);
        taskListView.setPrefWidth(300);

        // TextFields and Buttons for adding Team Members
        TextField memberNameField = new TextField();
        memberNameField.setPromptText("Wprowadź nazwę członka zespołu");
        Button addMemberButton = new Button("Dodaj");
        addMemberButton.setOnAction(e -> {
            String name = memberNameField.getText().trim();
            if (!name.isEmpty()) {
                teamMembers.add(new TeamMember(name));
                memberNameField.clear();
            } else {
                showAlert("Błąd", "Nie podano nazwy członka zespołu!");
            }
        });

        // TextFields and Buttons for adding Tasks
        TextField taskDescriptionField = new TextField();
        taskDescriptionField.setPromptText("Wprowadź opis zadania");
        Button addTaskButton = new Button("Dodaj");
        addTaskButton.setOnAction(e -> {
            String description = taskDescriptionField.getText().trim();
            if (!description.isEmpty()) {
                tasks.add(new Task(description));
                taskDescriptionField.clear();
            } else {
                showAlert("Błąd", "Nie podano opisu zadania!");
            }
        });

        // Button for managing tasks
        Button manageTasksButton = new Button("Zarządzaj zadaniami");
        manageTasksButton.setOnAction(e -> {
            Stage manageTasksStage = new Stage();
            manageTasksStage.setTitle("Zarządzanie zadaniami");

            // ComboBox for assigning Tasks
            ComboBox<TeamMember> assignTaskMemberComboBoxStage = new ComboBox<>(teamMembers);
            assignTaskMemberComboBoxStage.setPromptText("Wybierz członka zespołu");

            // ComboBox for choosing Task to assign
            ComboBox<Task> assignTaskComboBoxStage = new ComboBox<>(tasks);
            assignTaskComboBoxStage.setPromptText("Wybierz zadanie");

            // Button for assigning Tasks
            Button assignTaskButtonStage = new Button("Przypisz");
            assignTaskButtonStage.setOnAction(event -> {
                Task selectedTask = assignTaskComboBoxStage.getValue();
                TeamMember selectedMember = assignTaskMemberComboBoxStage.getValue();
                if (selectedTask != null && selectedMember != null) {
                    selectedTask.setAssignedMember(selectedMember);
                    taskListView.refresh();
                } else {
                    showAlert("Błąd", "Nie wybrano zadania lub członka zespołu!");
                }
            });

            // ComboBox for setting Task Status
            ComboBox<TaskStatus> taskStatusComboBoxStage = new ComboBox<>(
                    FXCollections.observableArrayList(TaskStatus.values()));
            taskStatusComboBoxStage.setPromptText("Wybierz status");

            // Button for setting Task Status
            Button setTaskStatusButtonStage = new Button("Ustaw");
            setTaskStatusButtonStage.setOnAction(event -> {
                Task selectedTask = assignTaskComboBoxStage.getValue();
                TaskStatus selectedStatus = taskStatusComboBoxStage.getValue();
                if (selectedTask != null && selectedStatus != null) {
                    selectedTask.setStatus(selectedStatus);
                    taskListView.refresh();
                } else {
                    showAlert("Błąd", "Nie wybrano zadania lub statusu!");
                }
            });

            VBox manageTasksLayout = new VBox(10, assignTaskMemberComboBoxStage, assignTaskComboBoxStage,
                    assignTaskButtonStage, taskStatusComboBoxStage, setTaskStatusButtonStage);
            manageTasksLayout.setPadding(new Insets(20));

            Scene manageTasksScene = new Scene(manageTasksLayout, 400, 300);
            manageTasksStage.setScene(manageTasksScene);
            manageTasksStage.show();
        });

        // Button for generating reports
        Button generateReportButton = new Button("Generuj raporty");
        generateReportButton.setOnAction(e -> {
            List<String> memberNames = teamMembers.stream()
                    .map(TeamMember::getName)
                    .collect(Collectors.toList());

            ChoiceDialog<String> memberDialog = new ChoiceDialog<>(null, memberNames);
            memberDialog.setTitle("Wybierz członka zespołu");
            memberDialog.setHeaderText("Wybierz członka zespołu, aby wyświetlić jego zadania");
            memberDialog.setContentText("Członek zespołu:");

            Optional<String> result = memberDialog.showAndWait();

            result.ifPresent(memberName -> {
                TeamMember selectedMember = teamMembers.stream()
                        .filter(member -> member.getName().equals(memberName))
                        .findFirst()
                        .orElse(null);

                if (selectedMember != null) {
                    StringBuilder report = new StringBuilder();
                    report.append("Raport zadań dla członka zespołu: ").append(selectedMember.getName()).append("\n\n");

                    List<Task> memberTasks = tasks.stream()
                            .filter(t -> t.getAssignedMember() == selectedMember)
                            .collect(Collectors.toList());

                    for (Task task : memberTasks) {
                        report.append("Zadanie: ").append(task.getDescription()).append("\n");
                        report.append("Status: ").append(task.getStatus().translate()).append("\n");
                        report.append("Czas przypisania: ").append(formatDateTime(task.getAssignedTime())).append("\n");
                        report.append("Czas zakończenia: ").append(formatDateTime(task.getCompletedTime()))
                                .append("\n");
                        report.append("Czas wykonania: ").append(formatTime(task.getTimeSpent())).append("\n");
                        report.append("\n");
                    }

                    showAlert("Raport zadań członka zespołu", report.toString());
                }
            });
        });

        // Layout setup
        GridPane addMemberPane = new GridPane();
        addMemberPane.setHgap(10);
        addMemberPane.setVgap(5);
        addMemberPane.addRow(0, memberNameField, addMemberButton);

        GridPane addTaskPane = new GridPane();
        addTaskPane.setHgap(10);
        addTaskPane.setVgap(5);
        addTaskPane.addRow(0, taskDescriptionField, addTaskButton);

        VBox teamBox = new VBox(10, new Label("Dodaj nowego członka zespołu:"), addMemberPane,
                new Label("Lista członków zespołu:"), teamListView);
        VBox taskBox = new VBox(10, new Label("Dodaj nowe zadanie:"), addTaskPane, new Label("Lista zadań:"),
                taskListView);

        HBox mainBox = new HBox(20, teamBox, taskBox);
        mainBox.setPadding(new Insets(20));

        VBox layout = new VBox(20, mainBox, new Separator(), manageTasksButton, generateReportButton);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Menadżer zadań");
        primaryStage.show();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return dateTime.format(formatter);
        }
        return "N/A";
    }

    private String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    public static void main(String[] args) {
        launch(args);
    }
}