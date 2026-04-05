package ufzdev.todo_list.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ufzdev.todo_list.models.UserModel;
import ufzdev.todo_list.services.StatisticsService;
import ufzdev.todo_list.util.AlertsUtil;
import ufzdev.todo_list.util.NavigationUtil;
import ufzdev.todo_list.util.StatisticsPeriod;
import ufzdev.todo_list.util.TaskExecutorUtil;
import ufzdev.todo_list.util.UserSessionUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class StatsController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label lblUserName;

    @FXML
    private ComboBox<String> cmbPeriod;

    @FXML
    private PieChart completionChart;

    @FXML
    private BarChart<String, Number> categoryChart;

    @FXML
    private CategoryAxis xCategoryAxis;

    @FXML
    private NumberAxis yCategoryAxis;

    @FXML
    private Label lblChartSummary;

    private final StatisticsService statisticsService = new StatisticsService();

    @FXML
    public void initialize() {
        loadUserName();
        setupPeriodFilter();
        refreshCharts();
    }

    @FXML
    public void handlePeriodChanged() {
        refreshCharts();
    }

    @FXML
    public void handleGoToTasks() {
        Stage stage = getCurrentStage();
        if (stage != null) {
            NavigationUtil.goToTasks(stage);
        }
    }

    @FXML
    public void handleGoToReports() {
        Stage stage = getCurrentStage();
        if (stage != null) {
            NavigationUtil.goToReports(stage);
        }
    }

    @FXML
    public void handleOpenSettings() {
        NavigationUtil.goToSettings();
    }

    @FXML
    public void handleLogout() {
        TaskExecutorUtil.execute(
                () -> {
                    UserSessionUtil.getInstance().cleanSession();
                    return true;
                },
                ignored -> {
                    AlertsUtil.showSuccess("Sesion cerrada", "Has cerrado sesion correctamente.");
                    Stage stage = getCurrentStage();
                    if (stage != null) {
                        NavigationUtil.goToLogin(stage);
                    }
                },
                error -> {
                    AlertsUtil.showError("Error al cerrar sesion", "No se pudo cerrar la sesion.");
                    System.out.println("Error cerrando sesion: " + error.getMessage());
                }
        );
    }

    private void setupPeriodFilter() {
        cmbPeriod.getItems().setAll(
                StatisticsPeriod.WEEK.getLabel(),
                StatisticsPeriod.MONTH.getLabel(),
                StatisticsPeriod.YEAR.getLabel()
        );
        cmbPeriod.setValue(StatisticsPeriod.MONTH.getLabel());
    }

    private void refreshCharts() {
        StatisticsPeriod selectedPeriod = StatisticsPeriod.fromLabel(cmbPeriod.getValue());

        Map<String, Integer> completionData = statisticsService.getCompletionStats(selectedPeriod);
        Map<String, Integer> categoryData = statisticsService.getCategoryStats(selectedPeriod);

        renderCompletionChart(completionData);
        renderCategoryChart(categoryData);
        updateSummaryLabel(completionData, categoryData, selectedPeriod);
    }

    private void renderCompletionChart(Map<String, Integer> completionData) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        for (Map.Entry<String, Integer> entry : completionData.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        completionChart.setData(pieChartData);
        completionChart.setTitle("Estado de tareas");
        completionChart.setLegendVisible(true);
        completionChart.setLabelsVisible(true);
    }

    private void renderCategoryChart(Map<String, Integer> categoryData) {
        categoryChart.getData().clear();
        categoryChart.setTitle("Resumen por categoria");

        xCategoryAxis.setLabel("Categoria");
        yCategoryAxis.setLabel("Cantidad");
        yCategoryAxis.setForceZeroInRange(true);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Periodo actual");

        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(categoryData.entrySet());
        sorted.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        for (Map.Entry<String, Integer> entry : sorted) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        categoryChart.getData().add(series);
    }

    private void updateSummaryLabel(Map<String, Integer> completionData,
                                    Map<String, Integer> categoryData,
                                    StatisticsPeriod period) {
        int notStarted = completionData.getOrDefault("No completada", 0);
        int inProgress = completionData.getOrDefault("En progreso", 0);
        int completed = completionData.getOrDefault("Completada", 0);
        int total = notStarted + inProgress + completed;

        String periodLabel = period == null ? StatisticsPeriod.MONTH.getLabel() : period.getLabel();
        lblChartSummary.setText("Periodo: " + periodLabel
                + " | Total: " + total
                + " | No completada: " + notStarted
                + " | En progreso: " + inProgress
                + " | Completada: " + completed
                + " | Categorias: " + categoryData.size());
    }

    private void loadUserName() {
        UserModel user = UserSessionUtil.getInstance().getUser();
        if (user == null) {
            lblUserName.setText("Usuario");
            return;
        }

        String name = user.getName();
        if (name == null || name.isBlank()) {
            name = user.getUsername();
        }
        if (name == null || name.isBlank()) {
            name = "Usuario";
        }

        lblUserName.setText(name);
    }

    private Stage getCurrentStage() {
        if (rootPane == null || rootPane.getScene() == null || rootPane.getScene().getWindow() == null) {
            return null;
        }
        return (Stage) rootPane.getScene().getWindow();
    }
}
