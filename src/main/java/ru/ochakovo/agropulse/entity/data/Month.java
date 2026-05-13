package ru.ochakovo.agropulse.entity.data;

import lombok.Getter;

public enum Month {
        JANUARY(1, "Январь"),
        FEBRUARY(2, "Февраль"),
        MARCH(3, "Март"),
        APRIL(4, "Апрель"),
        MAY(5, "Май"),
        JUNE(6, "Июнь"),
        JULY(7, "Июль"),
        AUGUST(8, "Август"),
        SEPTEMBER(9, "Сентябрь"),
        OCTOBER(10, "Октябрь"),
        NOVEMBER(11, "Ноябрь"),
        DECEMBER(12, "Декабрь");


    @Getter
    private final int number;

    @Getter
    private final String name;

    Month(int number, String name) {
        this.number = number;
        this.name = name;
    }

}
