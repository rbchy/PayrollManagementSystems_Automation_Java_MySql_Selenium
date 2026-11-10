package com.payroll;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Runs the app's real SQL (Add, Update, Search, View, Payslip, Delete) against MySQL.
 * In CI a MySQL 8 service is created from db/setup_payroll_db.sql. Without a database
 * the class is skipped, not failed.
 */
@Tag("db")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeSqlIntegrationTest {

    private static final int EMP_ID = 9001;
    private static Connection conn;

    @BeforeAll
    static void connect() throws SQLException {
        try {
            conn = DBConnection.getConnection();
        } catch (SQLException e) {
            conn = null;
        }
        assumeTrue(conn != null, "MySQL not reachable at " + DBConnection.url() + " — skipping DB tests");
        try (PreparedStatement ps = conn.prepareStatement(DeleteEmployeeHelper.DELETE_SQL)) {
            ps.setInt(1, EMP_ID);
            ps.executeUpdate();
        }
    }

    @AfterAll
    static void cleanup() throws SQLException {
        if (conn == null) return;
        try (PreparedStatement ps = conn.prepareStatement(DeleteEmployeeHelper.DELETE_SQL)) {
            ps.setInt(1, EMP_ID);
            ps.executeUpdate();
        }
        conn.close();
    }

    @Test
    @Order(1)
    @DisplayName("Add Employee INSERT সফল হয় এবং হিসাব ঠিক থাকে")
    void insertEmployee() throws SQLException {
        PayrollCalculator.PayResult r = PayrollCalculator.calculate(20, 40, 5, 0, 0, 0, 0, 0);
        try (PreparedStatement ps = conn.prepareStatement(AddEmployeeTab.INSERT_SQL)) {
            Object[] v = {"ADA", "LOVELACE", "123-45-6789", "QA Engineer", "+1 555-0100", "ada@example.com",
                    "2024-02-01", 40.0, 20.0, 1, "Engineering", "ACME", "First Bank", "ACC-1234",
                    r.normalSalary, 5.0, r.overtimeSalary, 0.0, r.doubletimeSalary, 0.0, r.extendedSalary,
                    0.0, r.holidayPaidSalary, 0.0, r.specialWorkSalary, r.bonusAmount, r.grossSalary,
                    r.grossSalary, r.taxDeduction, r.medicalInsurance, r.netSalary, "2026-11-06",
                    r.totalDeductions, EMP_ID};
            for (int i = 0; i < v.length; i++) ps.setObject(i + 1, v[i]);
            assertEquals(1, ps.executeUpdate());
        }
        try (PreparedStatement ps = conn.prepareStatement(PayslipTab.PAYSLIP_SQL)) {
            ps.setInt(1, EMP_ID);
            ps.setString(2, "2026-11-06");
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next(), "payslip query must find the new record");
                assertEquals(950.0, rs.getDouble("gross_salary"), 0.001);   // 40*20 + 5*20*1.5
                assertEquals(665.0, rs.getDouble("net_salary"), 0.001);     // 950 - 30%
            }
        }
    }

    @Test
    @Order(2)
    @DisplayName("Update Employee UPDATE সফল হয়: bank, account, holiday/special hours, component amounts")
    void updateEmployee() throws SQLException {
        PayrollCalculator.PayResult r = PayrollCalculator.calculate(25, 40, 2, 1, 0, 8, 0, 100);
        try (PreparedStatement ps = conn.prepareStatement(UpdateEmployeeTab.UPDATE_SQL)) {
            Object[] v = {"ADA", "KING", "QA Lead", "Engineering", "ada.king@example.com", "+1 555-0101",
                    "2024-02-01", 25.0, 1, 40.0, 2.0, 1.0, "2026-11-13", "123-45-6789", "ACME",
                    "Second Bank", "ACC-9999", 0.0, 8.0, 0.0,
                    r.grossSalary, r.taxDeduction, r.medicalInsurance, r.totalDeductions, r.netSalary, r.bonusAmount,
                    r.normalSalary, r.overtimeSalary, r.doubletimeSalary, r.extendedSalary, r.holidayPaidSalary,
                    r.specialWorkSalary, EMP_ID};
            for (int i = 0; i < v.length; i++) ps.setObject(i + 1, v[i]);
            assertEquals(1, ps.executeUpdate(), "UPDATE must change exactly one row");
        }
        try (PreparedStatement ps = conn.prepareStatement(UpdateEmployeeTab.LOAD_SQL)) {
            ps.setInt(1, EMP_ID);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals("Second Bank", rs.getString("bank_name"));
                assertEquals("ACC-9999", rs.getString("account_no"));
                assertEquals(8.0, rs.getDouble("holidaypaid_hours"), 0.001);
                assertEquals(r.normalSalary, rs.getDouble("salary"), 0.001);
            }
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT overtime_amount, holidaypaid_amount, net_salary FROM employees WHERE employee_id = ?")) {
            ps.setInt(1, EMP_ID);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(75.0, rs.getDouble("overtime_amount"), 0.001);     // 2h * 25 * 1.5
                assertEquals(200.0, rs.getDouble("holidaypaid_amount"), 0.001); // 8h * 25
                assertEquals(r.netSalary, rs.getDouble("net_salary"), 0.001);
            }
        }
    }

    @Test
    @Order(3)
    @DisplayName("Search (ID ও নাম) এবং View query নতুন employee খুঁজে পায়")
    void searchAndView() throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SearchQueryHelper.buildIdSearchSql())) {
            ps.setInt(1, EMP_ID);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals("KING", rs.getString("last_name"));
            }
        }
        try (PreparedStatement ps = conn.prepareStatement(SearchQueryHelper.buildNameSearchSql())) {
            ps.setString(1, SearchQueryHelper.buildNamePattern("ad"));
            try (ResultSet rs = ps.executeQuery()) {
                boolean found = false;
                while (rs.next()) found |= rs.getInt("employee_id") == EMP_ID;
                assertTrue(found, "name search 'ad' must find ADA");
            }
        }
        try (PreparedStatement ps = conn.prepareStatement(PayrollTabbedGUI.VIEW_SQL);
             ResultSet rs = ps.executeQuery()) {
            int rows = 0;
            while (rs.next()) rows++;
            assertTrue(rows >= 1);
        }
    }

    @Test
    @Order(4)
    @DisplayName("Delete Employee সারি মুছে দেয়")
    void deleteEmployee() throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(DeleteEmployeeHelper.DELETE_SQL)) {
            ps.setInt(1, EMP_ID);
            assertEquals(1, ps.executeUpdate());
            assertEquals(0, ps.executeUpdate(), "second delete finds nothing");
        }
    }
}
