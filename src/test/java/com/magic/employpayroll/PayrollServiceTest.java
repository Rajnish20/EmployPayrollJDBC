package com.magic.employpayroll;

import com.magic.employpayroll.entity.Employ;
import com.magic.employpayroll.service.EmployPayrollService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PayrollServiceTest {
    @Test
    public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
        EmployPayrollService employPayrollService = new EmployPayrollService();
        List<Employ> employList = employPayrollService.readData();
        Assertions.assertEquals(3, employList.size());
    }
}
