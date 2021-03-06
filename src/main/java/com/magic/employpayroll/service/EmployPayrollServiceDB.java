package com.magic.employpayroll.service;

import com.magic.employpayroll.entity.Employ;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
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

    public void updateEmployeeSalary(int id, double salary) {
        int result = employPayrollService.updateEmployeeData(id, salary);
        if (result == 0) return;
        Employ employ = this.getEmployeePayrollData(id);
        if (employ != null) employ.salary = salary;
    }

    private Employ getEmployeePayrollData(String name) {
        return this.employeeList.stream()
                .filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name))
                .findFirst()
                .orElse(null);
    }
    private Employ getEmployeePayrollData(int  id) {
        return this.employeeList.stream()
                .filter(employeePayrollDataItem -> employeePayrollDataItem.id == id)
                .findFirst()
                .orElse(null);
    }

    public List<Employ> getEmployListInGivenDateRange(LocalDate startDate, LocalDate endDate) {
        return employPayrollService.getEmployeePayrollForDateRange(startDate, endDate);
    }

    public Map<String, Double> readAverageSalaryByGender() {
        return employPayrollService.getAverageSalaryByGender();
    }

    public void addEmployToDB(String name, double salary, LocalDate startDate, String gender) {
        int id = employPayrollService.addNewEmployToEmploy_PayrollDB(name, salary, startDate, gender);
        this.employeeList.add(new Employ(id, name, salary, startDate, gender));
    }

    public void addEmploy(String name, double salary, LocalDate startDate, String gender) {
        Employ employ = employPayrollService.addNewEmploy(name, salary, startDate, gender);
        this.employeeList.add(employ);
    }

    public int addMultipleEmployToDB(List<Employ> employs) {
        employs.forEach(employ -> {
            this.addEmploy(employ.name, employ.salary, employ.startDate,
                    employ.gender);
            System.out.println(employ.name + " Added");
        });
        return this.employeeList.size();
    }

    public int addMultipleEmployToDBUsingThreads(List<Employ> employs) {
        Map<Integer, Boolean> employAdditionStatus = new HashMap<>();
        employs.forEach(employ -> {
            Runnable task = () -> {
                employAdditionStatus.put(employ.hashCode(), false);
                this.addEmploy(employ.name, employ.salary, employ.startDate, employ.gender);
                employAdditionStatus.put(employ.hashCode(), true);
            };
            Thread thread = new Thread(task);
            thread.start();
        });
        while (employAdditionStatus.containsValue(false)) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return employeeList.size();
    }
}