# Orders Management System

A Java Swing application for managing clients, products and orders, backed by a PostgreSQL database. Placing an order checks the available stock, updates the inventory and writes an immutable bill to a log table.

## Features

- Create, view, edit and delete **clients** and **products**
- Place and delete **orders**; ordering more than the available stock is rejected with an under-stock message
- A **bill** is generated for every order and stored in a log table (insert and select only)
- Input validation for clients: e-mail format, 10-digit phone number
- Tables in the UI are built automatically from the model classes using reflection

## Architecture

A four-layer design with one package per layer:

| Layer | Package | Contents |
|---|---|---|
| Presentation | `presentation` | Swing views (`ClientView`, `ProductView`, `OrderView`), `Controller`, `ReflectionTableUtil` |
| Business logic | `business_logic` | `ClientBLL`, `ProductBLL`, `OrderBLL`, `validators` (`EmailValidator`, `PhoneValidator`, `StockValidator`) |
| Data access | `data_access` | Generic `AbstractDAO<T>` plus `ClientDAO`, `ProductDAO`, `OrderDAO`, `BillDAO` |
| Model | `model` | `Client`, `Product`, `Order`, and `Bill` (a Java `record`) |

Highlights:

- **Reflection-based generic DAO** - `AbstractDAO<T>` builds the SQL for `findAll`, `findById`, `insert`, `update` and `delete` and maps `ResultSet` rows to objects by inspecting the fields of `T`
- **Singleton `ConnectionFactory`** for JDBC connections
- **Validator pattern** - small `Validator<T>` implementations that can be combined per entity
- The code is documented with Javadoc (generate it with `mvn javadoc:javadoc`) and a UML diagram is in [`docs/`](docs/)

## Getting started

Requirements: JDK 25 (as configured in `pom.xml`; lower `maven.compiler.source/target` if you use an older JDK), Maven and PostgreSQL.

### 1. Create the database

```bash
createdb ordersdb
psql -d ordersdb -f src/main/resources/ordersdb.sql
```

The script creates the `client`, `product`, `orders` and `bill` tables and inserts a few sample rows.

### 2. Configure the connection

No credentials are stored in the code. The connection is configured through **environment variables** or a local **`db.properties`** file; the password is required and has no default.

| Setting | Environment variable | `db.properties` key | Default |
|---|---|---|---|
| JDBC URL | `ORDERSDB_URL` | `db.url` | `jdbc:postgresql://localhost:5432/ordersdb` |
| User | `ORDERSDB_USER` | `db.user` | `postgres` |
| Password | `ORDERSDB_PASSWORD` | `db.password` | *none - must be set* |

Option A - environment variable:

```bash
# Linux / macOS
export ORDERSDB_PASSWORD='your-password'
# Windows (PowerShell)
$env:ORDERSDB_PASSWORD = 'your-password'
```

Option B - properties file (already listed in `.gitignore`, so it will not be committed):

```bash
cp db.properties.example db.properties
# then edit db.properties and set db.password
```

Environment variables take priority over `db.properties`.

### 3. Run

```bash
mvn compile exec:java -Dexec.mainClass=org.tucn.pt.Start
```

Or open the project in IntelliJ IDEA and run `org.tucn.pt.Start` (set the environment variable in the run configuration, or place `db.properties` in the project root).
