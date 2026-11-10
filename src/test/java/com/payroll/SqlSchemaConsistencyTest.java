package com.payroll;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every column named in the app's SQL must exist in db/setup_payroll_db.sql.
 * Runs without MySQL. Catches the bug where Update Employee used
 * bankname / accountno / holiday_paid_hours / special_work_hours.
 */
class SqlSchemaConsistencyTest {

    private static final Set<String> SCHEMA_COLUMNS = new TreeSet<>();

    @BeforeAll
    static void readSchema() throws Exception {
        String sql = Files.readString(Paths.get("db/setup_payroll_db.sql"));
        String table = sql.substring(sql.indexOf("CREATE TABLE IF NOT EXISTS employees"));
        table = table.substring(table.indexOf('(') + 1, table.indexOf(");"));
        Matcher m = Pattern.compile("(?m)^\\s+(\\w+)\\s+(INT|VARCHAR|DOUBLE|DATE|TINYINT)").matcher(table);
        while (m.find()) SCHEMA_COLUMNS.add(m.group(1).toLowerCase(Locale.ROOT));
        assertTrue(SCHEMA_COLUMNS.size() > 30, "schema parse failed: " + SCHEMA_COLUMNS);
    }

    static Stream<Arguments> statements() {
        return Stream.of(
                Arguments.of("AddEmployeeTab.INSERT_SQL", AddEmployeeTab.INSERT_SQL),
                Arguments.of("UpdateEmployeeTab.UPDATE_SQL", UpdateEmployeeTab.UPDATE_SQL),
                Arguments.of("UpdateEmployeeTab.LOAD_SQL", UpdateEmployeeTab.LOAD_SQL),
                Arguments.of("PayrollTabbedGUI.VIEW_SQL", PayrollTabbedGUI.VIEW_SQL),
                Arguments.of("SearchQueryHelper.buildIdSearchSql", SearchQueryHelper.buildIdSearchSql()),
                Arguments.of("SearchQueryHelper.buildNameSearchSql", SearchQueryHelper.buildNameSearchSql()),
                Arguments.of("DeleteEmployeeHelper.DELETE_SQL", DeleteEmployeeHelper.DELETE_SQL),
                Arguments.of("PayslipTab.PAYSLIP_SQL", PayslipTab.PAYSLIP_SQL));
    }

    /** Column identifiers in a statement, ignoring keywords, functions and the table name. */
    static List<String> columnsOf(String sql) {
        Set<String> skip = Set.of("select", "from", "where", "and", "or", "insert", "into", "values",
                "update", "set", "delete", "like", "upper", "order", "by", "employees");
        List<String> cols = new ArrayList<>();
        Matcher m = Pattern.compile("\\b([A-Za-z_][A-Za-z0-9_]*)\\b").matcher(sql.replaceAll("'[^']*'", ""));
        while (m.find()) {
            String w = m.group(1).toLowerCase(Locale.ROOT);
            if (!skip.contains(w)) cols.add(w);
        }
        return cols;
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("statements")
    @DisplayName("SQL-এর প্রতিটি column schema-তে আছে")
    void everyColumnExistsInSchema(String name, String sql) {
        List<String> missing = new ArrayList<>();
        for (String c : columnsOf(sql)) if (!SCHEMA_COLUMNS.contains(c)) missing.add(c);
        assertTrue(missing.isEmpty(), name + " uses columns not in the schema: " + missing);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Placeholder count: INSERT 34, UPDATE 33")
    void placeholderCounts() {
        assertTrue(AddEmployeeTab.INSERT_SQL.chars().filter(c -> c == '?').count() == 34);
        assertTrue(UpdateEmployeeTab.UPDATE_SQL.chars().filter(c -> c == '?').count() == 33);
    }
}
