# ⚡ Electricity Billing System

A desktop application built with **Java Swing** and **MySQL (JDBC)** that manages electricity customers, their meters, monthly bill generation and payment status.

It has two roles — **Admin** and **Customer** — and each role sees a different set of menus after logging in.

The code is organised in layers (UI → Service → DAO → Database) and demonstrates OOP, Collections & Generics, Multithreading & Synchronization, and JDBC.

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Java Concepts Implemented (Rubric Mapping)](#-java-concepts-implemented-rubric-mapping)
- [Requirements](#-requirements)
- [Setup Guide](#-setup-guide)
- [How to Run](#-how-to-run)
- [Default Login](#-default-login)
- [Usage Flow](#-usage-flow)
- [Bill Calculation](#-bill-calculation)
- [Database Schema](#-database-schema)
- [Troubleshooting](#-troubleshooting)
- [Building a Distributable JAR](#-building-a-distributable-jar)
- [Known Limitations](#-known-limitations)

---

## ✨ Features

### Admin
- Add new customers (a **unique** 6-digit meter number is generated automatically)
- Store meter information (location, type, phase code, bill type)
- Calculate monthly bills — **Normal** and **Industrial** bills are priced differently
- View all customers, and all bills with their payment status
- Duplicate bills for the same meter and month are blocked

### Customer
- Sign up using the meter number assigned by the admin
- View and update personal information (with email/phone validation)
- Generate a detailed, itemised bill for any month
- View bill history
- Pay a bill (marks it *Paid* and opens the Paytm page)

### Common
- Splash screen on startup (runs on its own thread)
- Role-based menus
- Clear error pop-ups instead of silent failures
- Tables load in the background so windows never freeze

---

## 🛠 Tech Stack

| Layer | Technology | Purpose |
|---|---|---|
| Language | Java 17 | Core application logic |
| GUI | Java Swing / AWT | Windows, forms, tables, menus |
| Database | MySQL 8.0 | Persistent storage |
| Connectivity | JDBC + MySQL Connector/J | Java ↔ MySQL communication |
| IDE / Build | Apache NetBeans + Apache Ant | Development and packaging |

---

## 🏗 Architecture

```
┌─────────────────────────────────────────────┐
│ UI layer (Swing windows)                    │  Login, CalculateBill, PayBill ...
└──────────────────────┬──────────────────────┘
                       │ calls
┌──────────────────────▼──────────────────────┐
│ Service layer                               │  BillingService, MeterNumberGenerator
│ (business rules, synchronization)           │
└──────────────────────┬──────────────────────┘
                       │ calls
┌──────────────────────▼──────────────────────┐
│ DAO layer (GenericDAO<T,K>)                 │  CustomerDAO, MeterDAO, BillDAO,
│ (all SQL lives here, PreparedStatement)     │  LoginDAO, TaxDAO
└──────────────────────┬──────────────────────┘
                       │ JDBC
┌──────────────────────▼──────────────────────┐
│ DBConnection (Singleton) → MySQL `ebs`      │
└─────────────────────────────────────────────┘
```

The UI never writes SQL. It works with **model objects** (`Customer`, `BillRecord`, ...) and asks DAOs/services to load or save them.

---

## 📁 Project Structure

```
Electricity-Billing-System/
├── src/
│   ├── db.properties                         # Database URL, username, password
│   ├── icon/                                 # Images used by the UI
│   └── electricity/billing/system/
│       ├── Splash.java                       # Entry point (main) — splash + thread
│       ├── Login.java, Signup.java           # Authentication screens
│       ├── Project.java                      # Dashboard with role-based menus
│       ├── NewCustomer.java, MeterInfo.java  # Admin: add customer + meter
│       ├── CalculateBill.java                # Admin: create a monthly bill
│       ├── CustomerDetails.java              # Admin: all customers
│       ├── DepositDetails.java               # Admin: all bills / search
│       ├── ViewInformation.java              # Customer: view details
│       ├── UpdateInformation.java            # Customer: edit details
│       ├── GenerateBill.java                 # Customer: itemised bill
│       ├── BillDetails.java                  # Customer: bill history
│       ├── PayBill.java, Paytm.java          # Customer: payment
│       │
│       ├── model/          Person (abstract), Admin, Customer, Meter,
│       │                   BillRecord, BillKey, TaxRate
│       ├── billing/        Billable (interface), Bill (abstract),
│       │                   NormalBill, IndustrialBill, BillFactory
│       ├── db/             DBConnection (Singleton, synchronized)
│       ├── dao/            GenericDAO<T,K> (interface), CustomerDAO, MeterDAO,
│       │                   BillDAO, LoginDAO, TaxDAO
│       ├── service/        BillingService (synchronized block),
│       │                   MeterNumberGenerator (static synchronized)
│       ├── exception/      EBSException + 5 custom exceptions
│       └── util/           BackgroundTask (SwingWorker), TableModelBuilder<T>,
│                           Months, Validator, MessageUtil
├── nbproject/                                # NetBeans configuration
├── build.xml                                 # Ant build script
├── ebs_schema.sql                            # Database setup script
└── README.md
```

---

## 🎯 Java Concepts Implemented (Rubric Mapping)

### 1. OOP — Polymorphism, Inheritance, Exception Handling, Interfaces

| Concept | Where | What it shows |
|---|---|---|
| **Inheritance** | `model/Person` → `Admin`, `Customer` | Common `name` field and methods in the parent class |
| | `billing/Bill` → `NormalBill`, `IndustrialBill` | Shared charge logic written once in the parent |
| | `exception/EBSException` → 5 child exceptions | Exception hierarchy |
| | Every screen `extends JFrame` | Reusing Swing's window class |
| **Abstraction** | `Person`, `Bill` are `abstract` | Abstract methods `getRole()`, `calculateEnergyCharge()` |
| **Polymorphism (runtime)** | `BillFactory.createBill()` returns `Bill` | `bill.calculateTotal()` runs Normal or Industrial logic decided at runtime |
| | `LoginDAO.authenticate()` returns `Person` | `user.getWelcomeMessage()` / `getMeterNo()` behave differently for Admin and Customer |
| **Method overriding** | `getRole()`, `getWelcomeMessage()`, `getBillType()`, `actionPerformed()`, `run()` | `@Override` in child classes |
| **Interfaces** | `billing/Billable`, `dao/GenericDAO<T,K>` | Custom contracts |
| | `ActionListener`, `Runnable`, `ItemListener`, `FocusListener` | Java library interfaces |
| **Exception handling** | `exception/` package | `DatabaseException`, `InvalidInputException`, `InvalidUnitsException`, `DuplicateBillException`, `AuthenticationException` |
| | All DAOs | `try-with-resources`, `try-catch-finally`, `throw`, `throws`, multi-catch |
| | `CustomerDAO.addWithLoginAccount()` | Transaction with `commit()` / `rollback()` in `catch` and cleanup in `finally` |
| **Encapsulation** | All model classes | `private` fields with getters/setters |

### 2. Collections & Generics

| Concept | Where |
|---|---|
| `List<T>` / `ArrayList` | Every `findAll()` in the DAOs returns `List<Customer>`, `List<BillRecord>`, ... |
| `Set<String>` / `HashSet` | `MeterNumberGenerator` checks uniqueness in O(1) |
| `Map<String,Integer>` / `LinkedHashMap` | `Bill.getBreakdown()` — ordered charge breakdown |
| `Optional<T>` | `findById()` returns `Optional` instead of `null` |
| Immutable `List.of(...)` | `util/Months.ALL` |
| **Generic interface** | `GenericDAO<T, K>` implemented by `CustomerDAO<Customer,String>`, `BillDAO<BillRecord,BillKey>` ... |
| **Generic methods** | `TableModelBuilder.<T>build(...)`, `BackgroundTask.<T>run(...)` |
| Functional interfaces | `Function<T,Object[]>`, `Callable<T>`, `Consumer<T>` |

### 3. Multithreading & Synchronization

| Concept | Where | Why |
|---|---|---|
| `Thread` + `Runnable` | `Splash.java` | A separate thread waits 7 s, then opens Login |
| `SwingWorker` background threads | `util/BackgroundTask` used in `CustomerDetails`, `DepositDetails`, `BillDetails`, `CalculateBill`, `GenerateBill` | Database queries run off the UI thread, so windows don't freeze |
| `SwingUtilities.invokeLater` | `Splash.run()` | UI updates must happen on the Event Dispatch Thread |
| `synchronized` method | `DBConnection.getInstance()` | Only one Singleton object even if two threads call it together |
| `static synchronized` method | `MeterNumberGenerator.generate()` | Class-level lock — no two threads generate at once |
| `synchronized` block on a lock object | `BillingService.generateBill()` | "Check if bill exists" + "insert bill" happen atomically — prevents duplicate bills (tested: 10 concurrent attempts → exactly 1 bill created) |

### 4. Classes for Database Operations

- `db/DBConnection` — **Singleton** connection factory; reads `db.properties`
- `dao/GenericDAO<T,K>` — common CRUD contract (`add`, `findById`, `findAll`, `update`)
- `dao/CustomerDAO`, `dao/MeterDAO`, `dao/BillDAO`, `dao/LoginDAO`, `dao/TaxDAO` — one class per table
- `service/BillingService` — business logic that combines several DAOs
- Model classes map table rows to Java objects

### 5 & 6. JDBC — Connectivity and Implementation

- `DriverManager.getConnection()` with URL/user/password from `db.properties`
- `PreparedStatement` with `?` placeholders everywhere → **SQL injection protected**
- `executeQuery()` for SELECT, `executeUpdate()` for INSERT/UPDATE, reading with `ResultSet`
- `try-with-resources` closes every `Connection`, `PreparedStatement` and `ResultSet`
- **Transactions**: `setAutoCommit(false)`, `commit()`, `rollback()`
- `SQLException` wrapped in the custom `DatabaseException`

---

## 💻 Requirements

| Software | Version | Link |
|---|---|---|
| JDK | 17 or higher | https://adoptium.net |
| Apache NetBeans | Latest | https://netbeans.apache.org |
| MySQL Server + Workbench | 8.0 | https://dev.mysql.com/downloads/installer/ |
| MySQL Connector/J | 8.0+ (Platform Independent) | https://dev.mysql.com/downloads/connector/j/ |

> `rs2xml.jar` is **no longer needed** — tables are now built with the project's own generic `TableModelBuilder`.

---

## ⚙️ Setup Guide

### Step 1 — Create the database
1. Open MySQL Workbench and connect to **Local instance MySQL80**.
2. **File → Open SQL Script** → `ebs_schema.sql` → click **⚡ Execute**.
3. Refresh **Schemas** — the `ebs` database with 5 tables should appear.

> Already created the database for the old version? Skip this step — the schema is unchanged and existing data keeps working.

### Step 2 — Create the application's database user
Run this once in a new Workbench query tab:
```sql
CREATE USER 'ebsuser'@'localhost' IDENTIFIED BY 'ebs123';
GRANT ALL PRIVILEGES ON ebs.* TO 'ebsuser'@'localhost';
FLUSH PRIVILEGES;
```
(If the user already exists you will see an error — that's fine.)

### Step 3 — Check the database settings
Open `src/db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/ebs?useSSL=false&allowPublicKeyRetrieval=true
db.user=ebsuser
db.password=ebs123
```
Change `db.user` / `db.password` only if you use a different MySQL account.

### Step 4 — Open in NetBeans
1. **File → Open Project** → select the `Electricity-Billing-System` folder.
2. The project expects the connector at `D:\libs\mysql-connector-j-26.7.0.jar`.
   If your JAR is elsewhere or has a different name: right-click project → **Properties → Libraries** → remove the broken entry → **+ → Add JAR/Folder** → select your connector JAR.
3. Main class is already set to `electricity.billing.system.Splash`.

---

## ▶️ How to Run

1. Right-click the project → **Clean and Build** → wait for `BUILD SUCCESSFUL`.
2. Press **F6** or click **▶ Run**.
3. Splash screen → Login window.

---

## 🔑 Default Login

| Username | Password | Logging in as |
|---|---|---|
| `admin` | `admin` | `Admin` |

Customers are not created directly: the admin adds the customer first, then the customer uses **Signup** with the assigned meter number.

---

## 🔄 Usage Flow

1. Log in as **Admin** → **Master → New Customer** → fill details (email and 10-digit phone are validated) → note the meter number.
2. Fill in the meter information. Choose **Industrial** to see the different pricing.
3. **Master → Calculate Bill** → pick meter, month, units → a pop-up shows the itemised bill.
4. **Exit** → **Signup** → **Customer** → enter the meter number → create username/password.
5. Log in as **Customer** → **Report → Generate Bill**, **User → Pay Bill**, **User → Bill Details**.
6. Log back in as Admin → **Master → Deposit Details** to see payment status (use **Show All** to reset a search).

---

## 🧮 Bill Calculation

```
Total = Energy Charge + Meter Rent + Service Charge + Service Tax + Swacch Bharat Cess + Fixed Tax
```

| Bill type | Energy charge |
|---|---|
| Normal | `units × cost_per_unit` |
| Industrial | `units × cost_per_unit` **+ 20% surcharge** |

**Example** — 100 units with the sample rates (9, 47, 22, 57, 6, 18):

| | Normal | Industrial |
|---|---|---|
| Energy charge | 900 | 1080 |
| Fixed charges | 150 | 150 |
| **Total** | **₹1050** | **₹1230** |

---

## 🗄 Database Schema

Database: **`ebs`** — tables are linked by `meter_no`.

| Table | Columns |
|---|---|
| `login` | meter_no, username, name, password, user |
| `customer` | name, **meter_no (PK)**, address, city, state, email, phone |
| `meter_info` | meter_no, meter_location, meter_type, phase_code, bill_type, days |
| `bill` | meter_no, month, units, totalbill, status |
| `tax` | cost_per_unit, meter_rent, service_charge, service_tax, swacch_bharat_cess, fixed_tax |

> The `tax` table must contain exactly **one** row of whole numbers.

---

## 🩺 Troubleshooting

Errors now appear as **pop-ups**; full details are also printed in the NetBeans **Output** panel.

| Message | Fix |
|---|---|
| *Could not connect to the database … Access denied* | Wrong user/password in `src/db.properties` → fix it, then **Clean and Build** |
| *Could not connect … Communications link failure* | Start the **MySQL80** service (Windows → Services) |
| *No suitable driver* / *Connector/J jar is not on the classpath* | Add the connector JAR in **Properties → Libraries** |
| *Unknown database 'ebs'* | Run `ebs_schema.sql` |
| *No tax rates found* | Insert one row into the `tax` table |
| *A bill for meter … already exists* | Expected — one bill per meter per month |
| Changed `db.properties` but nothing changed | Run **Clean and Build** so the new file is copied into the build |

---

## 📦 Building a Distributable JAR

1. **Clean and Build** → `dist/Electricity_Billing_System.jar` plus `dist/lib/` are created.
2. Ship both together and run:
```bash
java -jar Electricity_Billing_System.jar
```
To share one database across machines, host MySQL online (Aiven, Railway, …) and change `db.url` in `db.properties`.

---

## ⚠️ Known Limitations

- Passwords are stored in plain text (should be hashed, e.g. with BCrypt).
- Payment is simulated — the bill is marked *Paid* without a real transaction.
- Numeric values are stored as `VARCHAR` for compatibility with the original schema.
- Fixed pixel layout — windows do not adapt to resizing.
- Notepad/Calculator shortcuts work only on Windows.

---

## 📄 License

For educational and academic use.
