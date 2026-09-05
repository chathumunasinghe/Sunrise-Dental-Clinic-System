# Sunrise Dental Clinic - Appointment & Billing System

Java Servlet/JSP web application for managing patient appointments and billing
for Sunrise Dental Clinic.

This is now a **plain (non-Maven) Eclipse Dynamic Web Project**. Dependencies
are added manually as JAR files in `WebContent/WEB-INF/lib` instead of being
resolved by Maven.

## Architecture
- **3-tier**: Controller (Servlets) → Service (business logic) → DAO (persistence) → MySQL
- **Design patterns used**: Singleton (`DBConnection`), DAO, MVC, Strategy (notifications)
- **Role-based access**: `AuthFilter` protects every page under `/views/*`; `staff.role`
  (`ADMIN` / `GUEST`) gates admin-only pages (Reports, Manage Staff)

## Folder layout
```
SunriseDental/
├── src/                      Java source (Controller, Service, DAO, Model, Filter)
├── WebContent/
│   ├── WEB-INF/
│   │   ├── web.xml
│   │   └── lib/              <-- put mysql-connector-j.jar (+ mail jars) here
│   ├── views/                JSP pages
│   └── assets/               css/js
└── database/schema.sql       run this once in MySQL to create the DB + tables
```

## Setup (Eclipse)

1. **Import the project**
   - File → Import → General → Existing Projects into Workspace → select this
     `SunriseDental` folder → Finish.
   - If Eclipse complains about the facet/runtime config not matching your
     Eclipse version, don't fight it — instead: File → New → Dynamic Web
     Project, name it `SunriseDental`, target runtime **Apache Tomcat v10.1**,
     Dynamic Web Module **6.0**, Java **17**, then copy this project's `src`
     and `WebContent` folders into the new project (overwrite when asked).

2. **JDBC driver — already included**
   - `WebContent/WEB-INF/lib/mariadb-java-client-2.7.6.jar` is already in this
     zip (MariaDB Connector/J, from Ubuntu's official package archive — it
     speaks the MySQL protocol and connects to a normal MySQL server fine).
     You don't need to download or add anything yourself.
   - Do **not** add `jakarta.servlet-api` or `jakarta.servlet.jsp-api` jars —
     those come from the Tomcat 10.1 server runtime automatically.

3. **Create the database**
   - Run `database/schema.sql` in MySQL Workbench / phpMyAdmin / CLI. This
     creates the `sunrise_dental` database, all tables, and two sample
     logins.

4. **Check DB credentials**
   - Open `src/com/SunriseDental/Dao/DBConnection.java` and update
     `DB_USER` / `DB_PASSWORD` if they don't match your local MySQL setup.

5. **Run**
   - Right-click the project → Run As → Run on Server → Tomcat v10.1.
   - Open `/SunriseDental/views/login.jsp`. Default logins:
     - Admin (full access): `admin` / `admin123`
     - Guest (front-desk access): `guest` / `guest123`

## If you still get a 500 error after this
Read the actual exception message/stack trace shown on the Tomcat error
page (or the `catalina.out` / Eclipse Console log) — `DBConnection.java`
now throws a clear message describing exactly what's wrong (MySQL not
running, database missing, wrong credentials, driver jar missing) instead
of a silent `NullPointerException`.
