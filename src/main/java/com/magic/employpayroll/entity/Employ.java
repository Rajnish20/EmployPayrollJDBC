package com.magic.employpayroll.entity;

import java.time.LocalDate;
import java.util.Objects;

public class Employ {
    public int id;
    public String name;
    public double salary;
    public LocalDate startDate;

    public Employ(int id, String name, double salary, LocalDate startDate) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "Employ{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", salary=" + salary +
                ", startDate=" + startDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employ employ = (Employ) o;
        return id == employ.id &&
                Double.compare(employ.salary, salary) == 0 && Objects.equals(name, employ.name) &&
                Objects.equals(startDate, employ.startDate);
    }

}
