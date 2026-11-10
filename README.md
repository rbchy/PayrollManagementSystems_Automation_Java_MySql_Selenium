<div align="center">

# 💼 Payroll Management System — Java Swing · MySQL · Test Automation

### A desktop payroll app (employees, salary engine, payslip and cheque) with a 260-test suite: JUnit 5, Cucumber BDD, AssertJ-Swing UI robots and real-MySQL integration tests, all running in GitHub Actions

[![Typing SVG](https://readme-typing-svg.demolab.com/?font=Fira+Code&size=18&pause=1500&color=0EA5E9&center=true&vCenter=true&width=900&repeat=true&lines=Java+21+%C2%B7+Swing+%C2%B7+MySQL+8+%C2%B7+JDBC;JUnit+5+%C2%B7+Cucumber+%C2%B7+AssertJ-Swing;260+%2F+260+green+in+CI+against+a+real+MySQL)](https://git.io/typing-svg)

<!-- ============ STATUS BAR ============ -->
[![CI](https://github.com/rbchy/PayrollManagementSystems_Automation_Java_MySql_Selenium/actions/workflows/ci.yml/badge.svg)](https://github.com/rbchy/PayrollManagementSystems_Automation_Java_MySql_Selenium/actions/workflows/ci.yml)
![Tests](https://img.shields.io/badge/Tests-260%20%2F%20260%20passed-22c55e?style=for-the-badge&logo=checkmarx&logoColor=white)
![BDD](https://img.shields.io/badge/Cucumber-85%20scenarios-23D96C?style=for-the-badge&logo=cucumber&logoColor=white)
![DB](https://img.shields.io/badge/DB%20tests-real%20MySQL%208-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/UI-Java%20Swing-E76F00?style=for-the-badge)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit-5.10-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![AssertJ-Swing](https://img.shields.io/badge/AssertJ--Swing-3.17-8B5CF6?style=for-the-badge)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

![Last Commit](https://img.shields.io/github/last-commit/rbchy/PayrollManagementSystems_Automation_Java_MySql_Selenium?style=flat-square&color=0ea5e9&label=Last%20commit)
![Repo Size](https://img.shields.io/github/repo-size/rbchy/PayrollManagementSystems_Automation_Java_MySql_Selenium?style=flat-square&color=155e75&label=Repo%20size)

</div>

---

## 📌 At a Glance

<table>
<tr>
<td width="50%" valign="top">

**🙋 What this is**

A **Java Swing** desktop application for small-business payroll, backed by **MySQL**. It covers:

- employee records: add, view, update, search, delete
- a **payroll engine**: normal / overtime / double-time / extended / holiday / special-work pay, bonus, 20 % tax, 10 % medical
- **payslip and cheque** generation, with the amount in words (`ONE THOUSAND TWO HUNDRED AND 00/100`)

The business logic lives in plain classes (no Swing, no JDBC), so it is unit-tested directly. The same rules are also written as **Cucumber** scenarios. The Swing tabs are clicked by **AssertJ-Swing** robots on a virtual display. Every SQL statement the app uses runs against a **real MySQL 8** database in CI.

</td>
<td width="50%" valign="top">

**📍 Quick facts**

| | |
|---|---|
| 🖥️ **App** | Java 21 Swing, 9 tabs |
| 🗄️ **Database** | MySQL 8, `payroll_db.employees` (35 columns) |
| 🧪 **Tests** | 260: unit 154 · BDD 85 · UI 8 · schema 9 · DB 4 |
| ✅ **CI** | 260 / 260 · `mvn verify` ≈ 35 s |
| 🧱 **Design** | Swing tabs → pure helper classes → JDBC |
| 📊 **Reports** | Surefire · Cucumber HTML (masterthought) |
| 📦 **Output** | runnable fat JAR built on every push |

</td>
</tr>
</table>

---

## ✅ Latest Results (GitHub Actions)

| Layer | Classes / features | Tests | ✅ Passed | Runs against |
|---|---|---:|---:|---|
| 🧩 Unit (logic) | 9 classes | 130 | 130 | plain JVM |
| 🛡️ Security | `SecurityTest` | 24 | 24 | plain JVM |
| 🥒 Cucumber BDD | 8 `.feature` files | 85 | 85 | plain JVM + MySQL (`@db`) |
| 🖱️ Swing UI | `WelcomeTabTest`, `RefreshTabTest` | 8 | 8 | Xvfb + fluxbox, `java.awt.Robot` |
| 📐 SQL ↔ schema | `SqlSchemaConsistencyTest` | 9 | 9 | `db/setup_payroll_db.sql` |
| 🗄️ DB integration | `EmployeeSqlIntegrationTest` | 4 | 4 | MySQL 8 service container |
| **Total** | | **260** | **260** | |

```mermaid
pie showData
    title 260 tests by layer
    "Unit logic (130)" : 130
    "Cucumber BDD (85)" : 85
    "Security (24)" : 24
    "SQL vs schema (9)" : 9
    "Swing UI robot (8)" : 8
    "Real MySQL (4)" : 4
```

```mermaid
pie showData
    title CI result
    "Passed" : 260
    "Failed" : 0
    "Skipped" : 0
```

```mermaid
xychart-beta
    title "JUnit tests per class"
    x-axis ["FormValid", "Security", "PayslipFmt", "PayrollCalc", "SearchQuery", "AmtWords", "DeleteHlp", "SqlSchema", "Auth", "Welcome", "SearchFilt", "DBInteg", "Refresh", "ViewTab", "UIHelper"]
    y-axis "Tests" 0 --> 45
    bar [40, 24, 20, 19, 15, 9, 9, 9, 8, 5, 5, 4, 3, 3, 2]
```

```mermaid
xychart-beta
    title "Cucumber scenarios per feature (85 incl. outline examples)"
    x-axis ["form_validation", "payroll_calc", "amount_words", "search", "payslip", "login", "employees", "db"]
    y-axis "Scenarios" 0 --> 35
    bar [29, 16, 11, 9, 8, 6, 5, 1]
```

> The `@db` scenario and `EmployeeSqlIntegrationTest` **skip** (not fail) on a machine without MySQL. In CI a step checks that they actually ran.

---

## 🏗️ Architecture

```mermaid
flowchart LR
    subgraph UI["🖥️ Swing tabs"]
        W[WelcomeTab] --> L[LoginTab]
        A[AddEmployeeTab]
        V[ViewEmployeeTab]
        U[UpdateEmployeeTab]
        P[PayslipTab]
        D[DeleteEmployeeTab]
        S[SearchEmployeeTab]
        R[RefreshTab]
    end
    subgraph Logic["🧠 Pure logic (unit + BDD tested)"]
        PC[PayrollCalculator]
        AW[AmountToWordsConverter]
        PF[PayslipFormatter]
        EV[EmployeeFormValidator]
        AS[AuthService]
        SQ[SearchQueryHelper]
        SF[SearchFilterHelper]
        DH[DeleteEmployeeHelper]
    end
    DB[(MySQL 8<br/>payroll_db.employees)]
    L --> AS
    A --> PC & EV
    U --> PC & EV
    P --> PF & AW
    S --> SQ
    V --> SF
    D --> DH
    UI -->|DBConnection<br/>PAYROLL_DB_* env| DB
```

### CI pipeline

```mermaid
flowchart TD
    P([push / PR / manual]) --> T[Job 1: tests]
    T --> M[(MySQL 8 service)]
    M --> S[setup_payroll_db.sql<br/>schema + admin user + 2 rows]
    S --> X[Xvfb :99 + fluxbox]
    X --> V[mvn verify<br/>JUnit 5 + Cucumber + AssertJ-Swing]
    V --> C{DB tests ran?<br/>skipped = 0}
    C --> R[(Artifacts: surefire,<br/>Cucumber HTML)]
    C -->|green| J[Job 2: mvn package]
    J --> JAR[(payroll-system-1.0.0.jar)]
```

### Payslip flow

```mermaid
sequenceDiagram
    autonumber
    actor HR as HR user
    participant PT as PayslipTab
    participant PF as PayslipFormatter
    participant DB as MySQL
    participant AW as AmountToWordsConverter
    HR->>PT: Employee ID + pay date (MM/DD/YYYY)
    PT->>PF: toSqlDate("11/06/2026")
    PF-->>PT: "2026-11-06" (STRICT: 11/31 rejected)
    PT->>DB: PAYSLIP_SQL (employee_id, pay_date)
    DB-->>PT: gross 950.00 · net 665.00
    PT->>AW: convert(665.00)
    AW-->>PT: "Six Hundred Sixty Five Dollars"
    PT->>PF: toChequeLine(665.00)
    PF-->>PT: "SIX HUNDRED SIXTY FIVE AND 00/100"
    PT-->>HR: payslip + cheque
```

### Employee record lifecycle

```mermaid
stateDiagram-v2
    [*] --> Validated: Add form (names, phone, account, dates)
    Validated --> Rejected: invalid input / negative hours
    Rejected --> Validated: fix and retry
    Validated --> Active: INSERT (pay calculated)
    Active --> Active: Update (pay recalculated, all amounts saved)
    Active --> Paid: Payslip + cheque for a pay date
    Paid --> Active: next pay period
    Active --> Hidden: active = 0
    Hidden --> Active: active = 1
    Active --> [*]: Delete
    Hidden --> [*]: Delete
```

---

## 💰 Payroll Rules

| Component | Formula | Multiplier |
|---|---|---:|
| Normal pay | `min(totalHours, 40) × rate` | 1.0 |
| Overtime | `overtimeHours × rate × 1.5` | 1.5 |
| Double time | `doubletimeHours × rate × 2` | 2.0 |
| Extended / Holiday / Special work | `hours × rate` | 1.0 |
| Gross | sum of the above + bonus | |
| Tax | `gross × 20 %` | |
| Medical | `gross × 10 %` | |
| **Net** | `gross − tax − medical` | |

```mermaid
pie showData
    title "Where a $950 gross goes (40 h × $20 + 5 h OT)"
    "Net pay" : 665
    "Tax 20%" : 190
    "Medical 10%" : 95
```

Negative pay rate, hours or bonus are rejected. Money is rounded half-up to cents.

---

## 🗄️ Database

```mermaid
erDiagram
    EMPLOYEES {
        int id PK
        int employee_id UK
        varchar first_name
        varchar last_name
        varchar ssn
        varchar designation
        varchar department
        varchar contact_no
        varchar email_id
        date hire_date
        varchar bank_name
        varchar account_no
        double pay_rate_hourly
        double Total_hours
        double overtime_hours
        double overtime_amount
        double holidaypaid_hours
        double holidaypaid_amount
        double bonus_amount
        double gross_salary
        double tax_deduction
        double medical_insurance
        double net_salary
        date pay_date
        tinyint active
    }
```

<sub>Simplified: the table also has doubletime, extended and special-work hours/amounts, `salary`, `ytd_gross` and `total_deductions`. See [`db/setup_payroll_db.sql`](db/setup_payroll_db.sql).</sub>

---

## 🧪 Test Coverage

```mermaid
mindmap
  root((260 tests))
    Unit 130
      PayrollCalculator
      AmountToWords
      PayslipFormatter
      FormValidator
      Search / Delete / Auth
    Security 24
      SQL-injection inputs
      parameterised SQL
      bad regex in search box
    Cucumber 85
      login
      payroll math
      cheque words
      form validation
      search
      db connection
    Swing UI 8
      Welcome tiles switch tabs
      Refresh button
    SQL vs schema 9
      every column exists
      34 / 33 placeholders
    Real MySQL 4
      insert
      update
      search + view
      delete
```

---

## ▶️ Running Locally

**Prerequisites:** JDK 21, Maven 3.9+, MySQL 8.

```bash
# 1. Database: creates payroll_db, user admin/admin123 (local demo only) and 2 sample rows
mysql -u root -p < db/setup_payroll_db.sql

# 2. Tests + Cucumber HTML report (target/cucumber-html-reports)
mvn clean verify

# 3. Run the app
mvn -DskipTests package
java -jar target/payroll-system-1.0.0.jar     # demo login: admin / admin123
```

| Setting | Env var | `-D` property | Default |
|---|---|---|---|
| JDBC URL | `PAYROLL_DB_URL` | `payroll.db.url` | `jdbc:mysql://localhost:3306/payroll_db` |
| DB user | `PAYROLL_DB_USER` | `payroll.db.user` | `admin` |
| DB password | `PAYROLL_DB_PASSWORD` | `payroll.db.password` | `admin123` |

> The Swing UI tests (`@Tag("ui")`) need a display. Without one they fail with `HeadlessException`; exclude them with `mvn test -DexcludedGroups=ui`. Without MySQL the DB tests skip.

---

## 📁 Project Structure

```text
PayrollManagementSystems_Automation_Java_MySql_Selenium/
├── .github/workflows/ci.yml        # MySQL service + Xvfb tests, fat-JAR job
├── db/setup_payroll_db.sql         # schema, app user, sample rows
├── pom.xml                         # Java 21 · JUnit 5 · Cucumber 7 · AssertJ-Swing · shade
└── src/
    ├── main/java/com/payroll/
    │   ├── PayrollTabbedGUI.java   # main window (entry point)
    │   ├── *Tab.java               # Welcome, Login, Add, View, Update, Payslip, Delete, Search, Refresh
    │   ├── PayrollCalculator.java  # pay engine
    │   ├── AmountToWordsConverter.java · PayslipFormatter.java
    │   ├── EmployeeFormValidator.java · AuthService.java
    │   ├── SearchQueryHelper.java · SearchFilterHelper.java · DeleteEmployeeHelper.java
    │   └── DBConnection.java       # JDBC, settings from env
    └── test/
        ├── java/com/payroll/       # 15 JUnit classes (unit, security, UI, schema, DB)
        ├── java/com/payroll/cucumber/  # step definitions + runner
        └── resources/features/     # 8 Gherkin feature files
```

---

## 🧹 Maintenance Log

| Area | Before | Now |
|---|---|---|
| **Update Employee** | SQL used `bankname`, `accountno`, `holiday_paid_hours`, `special_work_hours`; none exist, so **every update failed** with *Unknown column* | correct columns; also saves `salary` and every pay-component amount. Proven against real MySQL |
| Cheque line | `ONE THOUSAND TWO HUNDRED` (no cents) and `ONE AND FIFTY/100` | `… AND 00/100`, `ONE AND 50/100` |
| Amount in words | `One Dollars`, `One Cents` | `One Dollar`, `One Cent` |
| DB setup script | comment promised `admin@'%'`, only `admin@localhost` was created → TCP / Docker clients denied | both users created |
| `mvn verify` | Cucumber report plugin mis-configured → **build failed after the tests** | `inputDirectory` + `jsonFiles`; HTML report generated |
| DB credentials | hard-coded in `DBConnection` | `PAYROLL_DB_*` env / `-D` (same defaults) |
| SQL | inline strings in the Swing tabs | constants checked by `SqlSchemaConsistencyTest` + real-MySQL test |
| Repo | `dependency-reduced-pom.xml` committed | removed; `.gitignore` |
| README | linked a missing test plan and LICENSE, wrong clone URL | rewritten from the code and the CI run |
| CI | none | GitHub Actions: MySQL 8 + Xvfb + fluxbox, JAR artifact |

---

## 📈 Roadmap

- [x] CI with a real MySQL database and the Swing UI tests
- [x] Cheque wording in standard `AND nn/100` form
- [ ] `payroll_history` table so YTD gross is a real `SUM` per year
- [ ] Hash the login password instead of a fixed demo pair
- [ ] Export payslips to PDF (iText 7 is already a dependency)
- [ ] Excel payroll report (Apache POI is already a dependency)

---

## 🔗 Related Projects

| Repo | What it is |
|---|---|
| [InventoryControlManagement_JavaMySql](https://github.com/rbchy/InventoryControlManagement_JavaMySql) | Java Swing + MySQL inventory app with CI |
| [BhojanAloy-Food-Coffee-Shop-POS-System](https://github.com/rbchy/BhojanAloy-Food-Coffee-Shop-POS-System) | Java POS system with MySQL |
| [PPES-Pharmaceutical-Packaging-Management](https://github.com/rbchy/PPES-Pharmaceutical-Packaging-Management) | Pharma packaging management system |
| [AppiumMobileAutomation](https://github.com/rbchy/AppiumMobileAutomation) | Appium + TestNG on an Android emulator in CI |
| [rbchy](https://github.com/rbchy/rbchy) | Profile and full project list |

---

## ⚠️ Disclaimer

Educational / portfolio project. Tax and insurance rates are fixed demo values, not real payroll law. Use a payroll specialist for real payroll. `admin / admin123` are local demo credentials only.

---

## 👤 Authors

<div align="center">

**Ranajit Baran Chowdhury (RB Chowdhury)** & **Rezaul Karim**
QA Automation Engineer / SDET · West Norriton, Pennsylvania, USA

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/rbchy/)
[![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/rbchy)
[![Website](https://img.shields.io/badge/Website-rbchy.github.io%2Frbchy-0d9488?style=for-the-badge&logo=githubpages&logoColor=white)](https://rbchy.github.io/rbchy/)
[![Portfolio](https://img.shields.io/badge/Portfolio-Website-blueviolet?style=for-the-badge&logo=wix&logoColor=white)](https://rbc6543.wixsite.com/rbc-portfolio)
[![Gmail](https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white)](mailto:chyranajit@gmail.com)

⭐ *If this project helps you, a star on the repo is appreciated.*

</div>
