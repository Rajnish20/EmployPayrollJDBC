package com.magic.employpayroll.service;

import com.magic.employpayroll.entity.Employ;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class EmployPayrollServiceDB {

    private List<Employ> employeeList;
    private final EmployPayrollService employPayrollService;


    public EmployPayrollServiceDB() throws SQLException {
        employPayrollService = EmployPayrollService.getInstance();
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

    public List<Employ> addEmployToDB(String name, double salary, LocalDate startDate, String gender)
    {
        employeeList = this.readEmployeePayrollData();
        int id = employPayrollService.addNewEmployToEmploy_PayrollDB(name,salary,startDate,gender);
        if(id != -1)
            this.employeeList.add(new Employ(id,name,salary,startDate,gender));
        return this.employeeList;
    }
}