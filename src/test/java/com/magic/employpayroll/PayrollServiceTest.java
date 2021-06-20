package com.magic.employpayroll;

import com.magic.employpayroll.entity.Employ;
import com.magic.employpayroll.service.EmployPayrollServiceDB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

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
}

