package com.payroll;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Connection settings come from environment variables (or -D system properties),
    // falling back to the local defaults created by db/setup_payroll_db.sql.
    //   PAYROLL_DB_URL      / payroll.db.url       jdbc:mysql://localhost:3306/payroll_db
    //   PAYROLL_DB_USER     / payroll.db.user      admin
    //   PAYROLL_DB_PASSWORD / payroll.db.password  admin123  (local-only demo password)
    static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/payroll_db";
    static final String DEFAULT_USER = "admin";
    static final String DEFAULT_PASSWORD = "admin123";

    static String setting(String property, String env, String fallback) {
        String v = System.getProperty(property);
        if (v == null || v.isBlank()) v = System.getenv(env);
        return (v == null || v.isBlank()) ? fallback : v;
    }

    public static String url()  { return setting("payroll.db.url", "PAYROLL_DB_URL", DEFAULT_URL); }
    public static String user() { return setting("payroll.db.user", "PAYROLL_DB_USER", DEFAULT_USER); }
    static String password()    { return setting("payroll.db.password", "PAYROLL_DB_PASSWORD", DEFAULT_PASSWORD); }
    
    public static Connection getConnection() throws SQLException {
        // Class.forName is often redundant in modern Java versions, but harmless to include:
        // try {
        //     Class.forName("com.mysql.cj.jdbc.Driver");
        // } catch (ClassNotFoundException e) {
        //     throw new SQLException("MySQL JDBC Driver not found.", e);
        // }
        return DriverManager.getConnection(url(), user(), password());
    }
}