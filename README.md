# Medical Center Management System (Java + Spring Boot + MySQL)

A full-stack **Web-based Medical Center Management System** built using **Spring Boot and MySQL**.  
This project was developed as part of an academic assignment to demonstrate understanding of:
- Java Spring Boot framework
- MVC (Model-View-Controller) architecture
- JDBC database connectivity
- JSP-based frontend rendering
- Role-based access control
- Servlet to Spring Boot migration

---

## Features

- **Doctor Module** — Search patients, write prescriptions, add medicines and diagnosis details
- **Medicine Distributor Module** — View undelivered medicines, mark medicines as delivered
- **Pharmacist Module** — Manage stock ledger, transfer medicines from central stock to sub-stock
- **Employee Module** — View own prescription history
- **Student Module** — View personal profile and prescription history
- **Role-based Login System** — Each role sees only its own dashboard
- **Data persistence** using MySQL

---

## Technologies Used

- Java 11
- Spring Boot 2.7.18
- Spring MVC
- JSP (JavaServer Pages)
- JDBC (JdbcTemplate)
- MySQL 8.0
- Maven
- VS Code
- MySQL Workbench

---

## Project Structure

```
springboot-medical/
│
├── src/main/java/
│   ├── com/medicalcenter/
│   │       MedicalCenterApplication.java
│   │
│   └── com/medicalcenter/controller/
│           LoginController.java
│           DoctorController.java
│           DistributorController.java
│           PharmacistController.java
│           AdminController.java
│           ImageController.java
│
├── src/main/java/medicalcenter/
│       database.java
│       StockLedgerEntry.java
│       ClientDate.java
│
├── src/main/resources/
│       application.properties
│       central_db.sql
│
├── src/main/webapp/
│       login.jsp
│       doctor_first_if.jsp
│       medicine_distributor.jsp
│       student_profile.jsp
│       ... (all JSP pages)
│       CSS/
│       javascript/
│
├── mvnw.cmd
├── pom.xml
└── README.md
```

---

## Database Setup

Open **MySQL Workbench** or **MySQL Command Line** and run:

```bash
mysql -u root -p < src/main/resources/central_db.sql
```

Or open MySQL Workbench → **Server → Data Import** → Import `central_db.sql`

> **Note:** If you get `NO_AUTO_CREATE_USER` error, open `central_db.sql` in Notepad,  
> press **Ctrl+H**, find `NO_AUTO_CREATE_USER,` and replace with nothing (empty). Save and re-run.

---

## Configure Database Connection

Update the credentials in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/central_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

---

## Run the Application

Open a terminal inside the project folder and run:

```bash
.\mvnw.cmd spring-boot:run
```

> The first time only, Maven will be downloaded automatically (requires internet).  
> After that it works offline.

Wait for this message in the console:

```
Started MedicalCenterApplication on port 8080
```

Then open your browser and go to:

```
http://localhost:8080
```

---

## Login Credentials

| Role | Username | Password |
|------|----------|----------|
| Doctor | `doctor` | `d` |
| Doctor 2 | `doctor2` | `d` |
| Medicine Distributor | `md` | `md` |
| Pharmacist | `pharmacist` | `p` |
| Employee | `a-cse` | `p` |
| Student | `2007331039` | `mokarrom` |

---

## Application Flow

```
Doctor writes prescription
        ↓
Medicines saved as "not available"
        ↓
Pharmacist transfers stock (Central Stock → Sub Stock)
        ↓
Medicines become "undelivered"
        ↓
Distributor delivers medicines to patient
        ↓
Student views prescription history
```

---

## Module Screenshots

### Login Page
- All roles use the same login page
- Username starting with a digit = Student login
- Username starting with a letter = Employee login

### Doctor Dashboard
- Search patient by registration number
- Fill diagnosis details, on-examination findings, investigations
- Add medicines with dose, duration, quantity
- Submit prescription

### Medicine Distributor Dashboard
- Search patient by registration number
- View list of undelivered medicines
- Click Delivered to confirm distribution

### Pharmacist Dashboard
- View Central Stock levels
- View Sub-Stock levels
- Transfer medicines from Central to Sub-Stock
- Add new stock entries from companies

---

## Example Usage

```
Login as Doctor (doctor / d)
→ Search patient: 2007331039
→ Select Medicine Type: Tablet
→ Select Medicine: Napa (1000mg)
→ Set Dose: 1+1+1, Duration: 7, Qty: 21
→ Submit Prescription ✅

Login as Distributor (md / md)
→ Search patient: 2007331039
→ Undelivered medicines appear
→ Click Delivered ✅

Login as Student (2007331039 / mokarrom)
→ Click Previous Prescription
→ View full prescription history ✅
```

---

## Original vs Converted

| Original Project | Converted Project |
|---|---|
| Java Servlets | Spring Boot Controllers |
| Tomcat (external) | Embedded Tomcat (Spring Boot) |
| `web.xml` mappings | `@RequestMapping` annotations |
| Raw JDBC | Spring `JdbcTemplate` |
| NetBeans IDE | VS Code |
| Manual deployment | `mvn spring-boot:run` |

---

## Learning Outcomes

- Migrated a legacy Java Servlet project to Spring Boot
- Implemented role-based authentication using HTTP sessions
- Used Spring MVC controllers to handle all request mappings
- Connected MySQL database using Spring JdbcTemplate
- Maintained JSP frontend pages with minimal changes
- Understood the full flow of a hospital management system

---

## Author

**Vemuri Sravan Ram Kumar**  
B.Tech Student
