package com.payroll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Senior-QA "security" pass over the pure-logic helper classes extracted
 * from the Swing tabs.
 *
 * Scope (positive + negative):
 *  - SQL injection: every SQL string built by the helpers must be
 *    parameterized ("?" placeholders), never string-concatenate raw user
 *    input into the SQL text itself.
 *  - Auth: AuthService must reject SQL-injection-style and other malicious
 *    credential strings, not just "wrong password".
 *  - Regex/DoS: SearchFilterHelper must not throw or hang on adversarial
 *    regex input from the search box.
 *
 * NOTE: DBConnection currently hardcodes DB credentials
 * ("admin"/"admin123") and AuthService hardcodes the login credentials
 * ("admin"/"admin123"). These are flagged as known issues in
 * TEST_PLAN_GUI_TABS.md / this test's javadoc but are NOT changed here,
 * since they are pre-existing application behaviour and changing them
 * would be a functional/credentials change outside this QA pass.
 */
class SecurityTest {

    // ------------------------------------------------------------------
    // SQL injection - SearchQueryHelper
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Security: ID-search SQL is parameterized (contains '?', no string concatenation of user input)")
    void buildIdSearchSql_isParameterized() {
        String sql = SearchQueryHelper.buildIdSearchSql();
        assertTrue(sql.contains("employee_id = ?"), "Expected a '?' placeholder for employee_id");
        assertFalse(sql.contains("' +"), "SQL must not be built via string concatenation");
    }

    @Test
    @DisplayName("Security: name-search SQL is parameterized (contains '?', no string concatenation of user input)")
    void buildNameSearchSql_isParameterized() {
        String sql = SearchQueryHelper.buildNameSearchSql();
        assertTrue(sql.contains("LIKE ?"), "Expected a '?' placeholder for the LIKE parameter");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "' OR '1'='1",
            "'; DROP TABLE employees; --",
            "1 OR 1=1",
            "%' UNION SELECT * FROM employees --"
    })
    @DisplayName("Negative: SQL-injection-style search keywords only ever become a LIKE parameter value, never raw SQL")
    void buildNamePattern_neverEmbedsRawSqlSyntax(String maliciousKeyword) {
        // isNumericId must reject these (they go down the name-search path,
        // which is always parameterized via buildNameSearchSql()'s "?").
        assertFalse(SearchQueryHelper.isNumericId(maliciousKeyword),
                "Injection payload must not be classified as a numeric ID search");

        String pattern = SearchQueryHelper.buildNamePattern(maliciousKeyword);
        // The pattern is just a bind-parameter VALUE (uppercased, wrapped in
        // % %) - the SQL string itself (buildNameSearchSql()) is unaffected
        // by the keyword content, so injection via this path is not possible.
        assertEquals("%" + maliciousKeyword.toUpperCase() + "%", pattern);
        assertTrue(SearchQueryHelper.buildNameSearchSql().contains("LIKE ?"));
    }

    // ------------------------------------------------------------------
    // SQL injection - DeleteEmployeeHelper
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Security: DELETE_SQL is parameterized by employee_id, no string concatenation")
    void deleteSql_isParameterized() {
        assertEquals("DELETE FROM employees WHERE employee_id=?", DeleteEmployeeHelper.DELETE_SQL);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1 OR 1=1",
            "1; DROP TABLE employees",
            "' OR '1'='1"
    })
    @DisplayName("Negative: SQL-injection-style Employee ID input is rejected before reaching SQL (NumberFormatException)")
    void parseEmployeeId_rejectsInjectionPayloads(String payload) {
        assertThrows(NumberFormatException.class,
                () -> DeleteEmployeeHelper.parseEmployeeId(payload),
                "Non-numeric injection payload must fail parsing, never reach the DELETE statement");
    }

    // ------------------------------------------------------------------
    // Auth - AuthService
    // ------------------------------------------------------------------

    @ParameterizedTest
    @ValueSource(strings = {
            "' OR '1'='1",
            "admin' --",
            "admin' OR '1'='1' --",
            "' OR 1=1#"
    })
    @DisplayName("Negative: SQL-injection-style username with any password is rejected")
    void authenticate_rejectsSqlInjectionUsername(String maliciousUsername) {
        assertFalse(AuthService.authenticate(maliciousUsername, "admin123"));
        assertFalse(AuthService.authenticate(maliciousUsername, "anything"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "' OR '1'='1",
            "admin' --",
            "' OR 1=1#"
    })
    @DisplayName("Negative: valid username with SQL-injection-style password is rejected")
    void authenticate_rejectsSqlInjectionPassword(String maliciousPassword) {
        assertFalse(AuthService.authenticate("admin", maliciousPassword));
    }

    @Test
    @DisplayName("Negative: empty-string credentials are rejected (not treated as a wildcard match)")
    void authenticate_rejectsEmptyCredentials() {
        assertFalse(AuthService.authenticate("", ""));
    }

    @Test
    @DisplayName("Positive: only the exact configured admin/admin123 credentials authenticate")
    void authenticate_acceptsOnlyExactValidCredentials() {
        assertTrue(AuthService.authenticate("admin", "admin123"));
        assertFalse(AuthService.authenticate("Admin", "admin123"), "Username check must be case-sensitive");
        assertFalse(AuthService.authenticate("admin", "Admin123"), "Password check must be case-sensitive");
    }

    // ------------------------------------------------------------------
    // Regex / DoS - SearchFilterHelper (live search box)
    // ------------------------------------------------------------------

    @ParameterizedTest
    @ValueSource(strings = {
            "(",
            "*",
            "[a-",
            "(((((((((((((((((((((((((((((((",
            "(a+)+$"   // classic ReDoS-shaped pattern
    })
    @DisplayName("Negative: adversarial/malformed regex from the search box never throws PatternSyntaxException")
    void safeRegexFilter_neverThrowsOnAdversarialInput(String payload) {
        assertDoesNotThrow(() -> SearchFilterHelper.safeRegexFilter(payload, 1, 2));
    }
}
