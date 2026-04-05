package ufzdev.todo_list.services;

import ufzdev.todo_list.models.TaskModel;
import ufzdev.todo_list.util.StatisticsChartUtil;
import ufzdev.todo_list.util.StatisticsPeriod;
import ufzdev.todo_list.util.UserSessionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatisticsService {
    private final UserSessionUtil session = UserSessionUtil.getInstance();

    public List<TaskModel> getTasksForPeriod(StatisticsPeriod period) {
        List<TaskModel> currentTasks = new ArrayList<>(session.getTasks());
        return StatisticsChartUtil.filterByPeriod(currentTasks, period);
    }

    public Map<String, Integer> getCompletionStats(StatisticsPeriod period) {
        return StatisticsChartUtil.buildCompletionCounts(getTasksForPeriod(period));
    }

    public Map<String, Integer> getCategoryStats(StatisticsPeriod period) {
        return StatisticsChartUtil.buildCategoryCounts(getTasksForPeriod(period));
    }
}

