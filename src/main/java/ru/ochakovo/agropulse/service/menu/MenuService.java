package ru.ochakovo.agropulse.service.menu;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MenuService {

    public List<MenuItem> getMenu(String role){
        List<MenuItem> items = List.of(
                new MenuItem("ADMINISTRATOR", "main", "/admin/main", "Главная"),
                new MenuItem("ADMINISTRATOR", "users", "/admin/users", "Сотрудники"),
                new MenuItem("ADMINISTRATOR", "audit", "/admin/audit", "Журнал аудита"),

                new MenuItem("OPERATOR", "create", "/operator/create", "Создание"),
                new MenuItem("OPERATOR", "reports", "/operator/reports", "Отчёты"),

                new MenuItem("ANALYTIC", "calculation", "/analytic/calculation", "Расчёты"),
                new MenuItem("ANALYTIC", "analytic", "/analytic/analytic", "Аналитика"),
                new MenuItem("ANALYTIC", "archive", "/analytic/archive", "Архив"),
                new MenuItem("ANALYTIC", "export", "/analytic/export", "Формирование")
                                      );

        return items.stream()
                .filter(item -> item.getRole().contains(role))
                .toList();
    }
}
