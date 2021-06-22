package com.magic.employpayroll;

import com.magic.employpayroll.entity.Employ;
import com.magic.employpayroll.service.EmployPayrollServiceDB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PayrollServiceTest {
    @Test
    public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() throws SQLException {
        EmployPayrollServiceDB employPayrollServiceDB = new EmployPayrollServiceDB();
        List<Employ> employList = employPayrollServiceDB.readEmployeePayrollData();
        Assertions.assertEquals(6, employList.size());
    }

    @Test
    public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncWithDB() throws SQLException {
        EmployPayrollServiceDB employPayrollServiceDB = new EmployPayrollServiceDB();
        List<Employ> employList = employPayrollServiceDB.readEmployeePayrollData();
        employPayrollServiceDB.updateEmployeeSalary("Sam", 900000.00);
        boolean result = employPayrollServiceDB.checkEmployeePayrollInSyncWithDB("Sam");
        Assertions.assertTrue(result);
    }

    @Test
    public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() throws SQLException {
        EmployPayrollServiceDB employPayrollServiceDB = new EmployPayrollServiceDB();
        LocalDate startDate = LocalDate.of(2019, 01, 01);
        LocalDate endDate = LocalDate.now();
        List<Employ> employList = employPayrollServiceDB.getEmployListInGivenDateRange(startDate, endDate);
        Assertions.assertEquals(3, employList.size());
    }

    @Test
    public void calculatedAverageSalaryByGender_Retrieved_ShouldReturnProperValue() throws SQLException {
        EmployPayrollServiceDB employPayrollServiceDB = new EmployPayrollServiceDB();
        Map<String, Double> averageSalaryByGender = employPayrollServiceDB.readAverageSalaryByGender();
        Assertions.assertTrue(averageSalaryByGender.get("M").equals(455000.00) &&
                averageSalaryByGender.get("F").equals(98876.98));
    }

    @Test
    public void givenEmployData_WhenAddedToDB_ShouldAlsoAddedInEmployList() throws SQLException {
        EmployPayrollServiceDB employPayrollServiceDB = new EmployPayrollServiceDB();
        employPayrollServiceDB.readEmployeePayrollData();
        LocalDate startDate = LocalDate.of(2019, 01, 02);
        employPayrollServiceDB.addEmployToDB("Lee", 89000.00, startDate, "M");
        boolean result = employPayrollServiceDB.checkEmployeePayrollInSyncWithDB("Lee");
        Assertions.assertTrue(result);
    }

    @Test
    public void givenEmployData_WhenAddedToDB_ShouldAddNewRecordToPayrollDetailsAutomatically() throws SQLException {
        EmployPayrollServiceDB employPayrollServiceDB = new EmployPayrollServiceDB();
        LocalDate startDate = LocalDate.of(2017, 04, 03);
        employPayrollServiceDB.addEmploy("Lucifer", 980000.00, startDate, "M");
        boolean result = employPayrollServiceDB.checkEmployeePayrollInSyncWithDB("Lucifer");
        Assertions.assertTrue(result);
    }

    @Test
    public void given6Employ_WhenAddedToDB_ShouldMatchEmployeeEntries() throws SQLException {
        EmployPayrollServiceDB employPayrollServiceDB = new EmployPayrollServiceDB();
        employPayrollServiceDB.readEmployeePayrollData();
        Employ[] employs = {
                new Employ(0, "Samuel", 90000.00,LocalDate.now(), "M"),
                new Employ(0, "Belie", 10000.00, LocalDate.now(),"F"),
                new Employ(0, "Rock", 7500.00, LocalDate.now(),"M"),
                new Employ(0, "Bella", 150000.00, LocalDate.now(),"F"),
                new Employ(0, "Rosalie", 25000.00, LocalDate.now(),"F"),
        };
        employPayrollServiceDB.readEmployeePayrollData();
        Instant start = Instant.now();
        int size = employPayrollServiceDB.addMultipleEmployToDB(Arrays.asList(employs));
        Instant end = Instant.now();
        System.out.println("Duration Without Threads " + Duration.between(start,end));
        Assertions.assertEquals(6,size);
    }

    @Test
    public void given6Employ_WhenAddedToDBUsingThreads_ShouldMatchEmployeeEntries() throws SQLException {
        EmployPayrollServiceDB employPayrollServiceDB = new EmployPayrollServiceDB();
        Employ[] employs = {
                new Employ(0, "Samuel", 90000.00,LocalDate.now(), "M"),
                new Employ(0, "Belie", 10000.00, LocalDate.now(),"F"),
                new Employ(0, "Rock", 7500.00, LocalDate.now(),"M"),
                new Employ(0, "Bella", 150000.00, LocalDate.now(),"F"),
                new Employ(0, "Rosalie", 25000.00, LocalDate.now(),"F"),
        };
        employPayrollServiceDB.readEmployeePayrollData();
        Instant start = Instant.now();
        int size = employPayrollServiceDB.addMultipleEmployToDBUsingThreads(Arrays.asList(employs));
        Instant end = Instant.now();
        System.out.println("Duration Without Threads " + Duration.between(start,end));
        Assertions.assertEquals(6,size);
    }

}

