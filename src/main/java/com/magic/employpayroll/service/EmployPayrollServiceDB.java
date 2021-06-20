package com.magic.employpayroll.service;

import com.magic.employpayroll.entity.Employ;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class EmployPayrollServiceDB {

    private List<Employ> employeeList;
    private EmployPayrollService employPayrollService;


    public EmployPayrollServiceDB() {
        employPayrollService = EmployPayrollService.getInstance();
    }

    public EmployPayrollServiceDB(List<Employ> employeeList) {
        this();
        this.employeeList = employeeList;

    }

    public List<Employ> readEmployeePayrollData() {
        this.employeeList = employPayrollService.readData();
        return employeeList;
    }

    public boolean checkEmployeePayrollInSyncWithDB(String name) {
        List<Employ> employeePayrollDataList = employPayrollService.getEmployeePayrollData(name);
        return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
    }

    public void updateEmployeeSalary(String name, double salary) {
        int result = employPayrollService.updateEmployeeData(name, salary);
        if (result == 0) return;
        Employ employ = this.getEmployeePayrollData(name);
        if (employ != null) employ.salary = salary;
    }

    private Employ getEmployeePayrollData(String name) {
        return this.employeeList.stream()
                .filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name))
                .findFirst()
                .orElse(null);
    }

    public List<Employ> getEmployListInGivenDateRange(LocalDate startDate, LocalDate endDate) {
        return employPayrollService.getEmployeePayrollForDateRange(startDate, endDate);
    }

    public Map<String, Double> readAverageSalaryByGender() {
        return employPayrollService.getAverageSalaryByGender();
    }
}
