package ru.ochakovo.agropulse.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MenuItem {
    String role;
    String name;
    String url;
    String title;
}
