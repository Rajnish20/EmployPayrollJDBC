package com.magic.employpayroll.service;

import com.magic.employpayroll.entity.Employ;
import com.magic.employpayroll.utility.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployPayrollService {
    DBConnection dbConnection = new DBConnection();
    private final Connection connection = dbConnection.getConnection();
    private PreparedStatement employeePayrollDataStatement;
    private static EmployPayrollService employPayrollService;

    private EmployPayrollService() throws SQLException {
    }

    public static EmployPayrollService getInstance() throws SQLException {
        if (employPayrollService == null)
            employPayrollService = new EmployPayrollService();
        return employPayrollService;
    }

    public List<Employ> readData() {
        String sql = "Select * from employ_payroll;";
        List<Employ> employList = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            employList = this.getEmployeePayrollData(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employList;
    }

    public List<Employ> getEmployeePayrollData(String name) {
        List<Employ> employeePayrollList = null;
        if (this.employeePayrollDataStatement == null)
            this.prepareStatementForEmployeeData();
        try {
            employeePayrollDataStatement.setString(1, name);
            ResultSet resultSet = employeePayrollDataStatement.executeQuery();
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeePayrollList;
    }


    private void prepareStatementForEmployeeData() {
        try {
            String sql = "SELECT * FROM employ_payroll WHERE name = ?";
            employeePayrollDataStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int updateEmployeeData(int id, double salary) {
        return this.updateEmployeeDataUsingStatement(id, salary);
    }


    private int updateEmployeeDataUsingStatement(int id, double salary) {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (Statement statement = connection.createStatement()) {
            String sql = String.format("update employ_payroll set salary = %.2f where id = '%s';", salary, id);
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        try (Statement statement = connection.createStatement()) {
            double deductions = salary * 0.2;
            double taxablePay = salary - deductions;
            double tax = taxablePay * 0.1;
            double netPay = salary - tax;
            String sql = String.format("update payroll_details set basic_pay = '%s'," +
                    "                   deductions = '%s'," +
                    "                   taxable_pay = '%s'," +
                    "                   tax = '%s'," +
                    "                   net_pay = '%s' where id = '%s';",salary,deductions,taxablePay,tax,netPay,id);

            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        try {
            connection.commit();
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Employ> getEmployeePayrollForDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = String.format("SELECT * FROM employ_payroll WHERE start BETWEEN '%s' AND '%s';",
                Date.valueOf(startDate), Date.valueOf(endDate));
        return this.getEmployeePayrollDataUsingDB(sql);
    }

    private List<Employ> getEmployeePayrollDataUsingDB(String sql) {
        List<Employ> employList = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            employList = this.getEmployeePayrollData(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employList;
    }

    public Map<String, Double> getAverageSalaryByGender() {
        String sql = "SELECT gender, AVG(salary) as average_salary FROM employ_payroll GROUP BY gender;";
        Map<String, Double> genderToAverageSalaryMap = new HashMap<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String gender = resultSet.getString("gender");
                double salary = resultSet.getDouble("average_salary");
                genderToAverageSalaryMap.put(gender, salary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return genderToAverageSalaryMap;
    }


    private List<Employ> getEmployeePayrollData(ResultSet resultSet) {
        List<Employ> employeePayrollList = new ArrayList<>();
        try {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double salary = resultSet.getDouble("salary");
                LocalDate startDate = resultSet.getDate("start").toLocalDate();
                String gender = resultSet.getString("gender");
                employeePayrollList.add(new Employ(id, name, salary, startDate, gender));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    public int addNewEmployToEmploy_PayrollDB(String name, double salary, LocalDate startDate, String gender) {
        int id = -1;
        String sql = String.format("INSERT INTO employ_payroll(name, salary, start,gender) "
                + "VALUES( '%s', '%s', '%s', '%s' )", name, salary, Date.valueOf(startDate), gender);
        try {
            Statement statement = connection.createStatement();
            int rowsAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
            if (rowsAffected == 1) {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next())
                    id = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public synchronized Employ addNewEmploy(String name, double salary, LocalDate startDate, String gender) {
        int employeeId = -1;
        Employ employ = null;
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (Statement statement = connection.createStatement()) {
            String sql = String.format("INSERT INTO employ_payroll(name,salary,start,gender) "
                    + "VALUES( '%s', '%s', '%s', '%s' )", name, salary, Date.valueOf(startDate), gender);
            int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
            if (rowAffected == 1) {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next())
                    employeeId = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        try (Statement statement = connection.createStatement()) {
            double deductions = salary * 0.2;
            double taxablePay = salary - deductions;
            double tax = taxablePay * 0.1;
            double netPay = salary - tax;
            String sql = String
                    .format("INSERT INTO payroll_details(id, basic_pay, deductions, taxable_Pay, tax, net_Pay)"
                            + "VALUES(%s, %s, %s, %s, %s, %s)", employeeId, salary, deductions, taxablePay, tax, netPay);
            int rowAffected = statement.executeUpdate(sql);
            if (rowAffected == 1) {
                employ = new Employ(employeeId, name, salary, startDate, gender);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        try {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employ;
    }
}
