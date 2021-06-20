package com.magic.employpayroll;

import com.magic.employpayroll.entity.Employ;
import com.magic.employpayroll.service.EmployPayrollServiceDB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PayrollServiceTest {
    @Test
    public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
        EmployPayrollServiceDB employPayrollServiceDB = new EmployPayrollServiceDB();
        List<Employ> employList = employPayrollServiceDB.readEmployeePayrollData();
        Assertions.assertEquals(3, employList.size());
    }

    @Test
    public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncWithDB() {
        EmployPayrollServiceDB employPayrollServiceDB = new EmployPayrollServiceDB();
        List<Employ> employList = employPayrollServiceDB.readEmployeePayrollData();
        employPayrollServiceDB.updateEmployeeSalary("Sam", 900000.00);
        boolean result = employPayrollServiceDB.checkEmployeePayrollInSyncWithDB("Sam");
        Assertions.assertTrue(result);
    }

    @Test
    public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() {
        EmployPayrollServiceDB employPayrollServiceDB = new EmployPayrollServiceDB();
        LocalDate startDate = LocalDate.of(2019, 01, 01);
        LocalDate endDate = LocalDate.now();
        List<Employ> employList = employPayrollServiceDB.getEmployListInGivenDateRange(startDate, endDate);
        Assertions.assertEquals(3, employList.size());
    }

    @Test
    public void givenPayrollDataWhenAverageSalaryRetrievedByGenderShouldReturnProperValue() {
        EmployPayrollServiceDB employPayrollServiceDB = new EmployPayrollServiceDB();
        Map<String, Double> averageSalaryByGender = employPayrollServiceDB.readAverageSalaryByGender();
        Assertions.assertTrue(averageSalaryByGender.get("M").equals(455000.00) &&
                averageSalaryByGender.get("F").equals(98876.98));
    }
}

