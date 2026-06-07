package ru.ochakovo.agropulse.service.menu;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MenuService {

    public List<MenuItem> getMenu(String role){
        List<MenuItem> items = List.of(
                new MenuItem("ADMINISTRATOR", "main", "/companies/main", "Главная"),
                new MenuItem("ADMINISTRATOR", "users", "/users", "Сотрудники"),
                new MenuItem("ADMINISTRATOR", "reports", "/reports", "Отчёты"),
                new MenuItem("ADMINISTRATOR", "audit", "/audit", "Журнал аудита"),

                new MenuItem("OPERATOR", "create", "/reports/create", "Создать отчёт"),
                new MenuItem("OPERATOR", "reports", "/reports", "Отчёты"),
                new MenuItem("OPERATOR", "esg", "/esg", "ESG-показатели"),

                new MenuItem("ANALYTIC", "reports", "/reports", "Отчёты")
                                      );

        return items.stream()
                .filter(item -> item.getRole().contains(role))
                .toList();
    }
}
