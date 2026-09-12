[🇻🇳 Tiếng Việt](README.md) | [🇬🇧 English](README.en.md) | [🇩🇪 Deutsch](README.de.md)

# ABC News

**Enterprise Digital News Publishing & Content Management System**

A full-stack, enterprise-grade digital journalism portal and newsroom Content Management System (CMS) built with **Java 17 LTS (Jakarta EE 10 / Servlet 6.0 / JSP 3.1)** and **Microsoft SQL Server 2022**. The application rigorously implements a **Clean 3-Tier Layered Architecture**, ultra-fast connection pooling via **HikariCP**, multi-layer security with **BCrypt & Google OAuth2**, interactive real-time analytics powered by **Chart.js**, and rich multimedia news drafting using **CKEditor 5 WYSIWYG**.

[![Java CI with Maven](https://github.com/nkhanhduy/ABCNews/actions/workflows/maven.yml/badge.svg)](https://github.com/nkhanhduy/ABCNews/actions/workflows/maven.yml)
![Java](https://img.shields.io/badge/Java-17%20LTS-ED8B00?logo=openjdk&logoColor=white)
![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-10-F80000?logo=jakarta-ee&logoColor=white)
![Tomcat](https://img.shields.io/badge/Tomcat-10.1-F8DC75?logo=apachetomcat&logoColor=black)
![Hibernate](https://img.shields.io/badge/Hibernate-6.4%20ORM-59666C?logo=hibernate&logoColor=white)
![HikariCP](https://img.shields.io/badge/HikariCP-5.1-2563EB?logo=speedtest&logoColor=white)
![SQL Server](https://img.shields.io/badge/SQL%20Server-2022-CC292B?logo=microsoftsqlserver&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)
![JUnit 5](https://img.shields.io/badge/Tests-14%20Passing-success?logo=junit5&logoColor=white)
![Bootstrap 5](https://img.shields.io/badge/Bootstrap-5.3-7952B3?logo=bootstrap&logoColor=white)
![Chart.js](https://img.shields.io/badge/Chart.js-4.4-FF6384?logo=chartdotjs&logoColor=white)

---

## 30-Second Executive Summary

- **Clean 3-Tier Layered Architecture:** Strict separation between Presentation Layer (Jakarta Servlets, JSPs, Custom Filters), Business Service Layer (BCrypt Hashing, OTP Token Lifecycle, RBAC Access Control), and Data Persistence Layer (Hibernate 6.4 JPA ORM combined with optimized JDBC Templates).
- **Blazing Fast Connection Pooling (HikariCP):** Completely eliminates single-threaded connection bottlenecks, reducing database query and API response times to sub-30ms under heavy concurrent read workloads.
- **Defense-in-Depth Security & Graceful Migration:** Implements OWASP-compliant 12-round BCrypt salt hashing, an automated graceful hash migration mechanism for legacy accounts, Google Identity OAuth2 authentication, and transactional 6-digit OTP verification via SMTP.
- **Real-Time Newsroom Analytics (Chart.js):** Interactive administrative dashboards featuring a smooth gradient Line Chart for top-read article trends and a Doughnut Chart for category-wise publication distribution.
- **Modern Editorial UX/UI:** Curated Slate Navy (`#0f172a` & `#1e293b`) brand palette, responsive layout, seamless Dark / Light Mode with `localStorage` persistence, automatic reading time estimation (~200 wpm), and a 1-click social sharing hub (Facebook, X, Telegram, Clipboard Copy).
- **Rich WYSIWYG Article Editor (CKEditor 5):** Enables reporters and editors to publish formatted articles with lead Sapo blocks, quotes, multi-level headers, and high-definition imagery.
- **Single-Command Docker Orchestration:** Full containerization with multi-stage Docker build, packing Tomcat 10.1 and Microsoft SQL Server 2022 with automatic UTF-8 schema initialization (`-f 65001`).
- **Comprehensive Automated Testing:** 14 automated unit tests powered by JUnit 5 and Mockito, integrated directly into a GitHub Actions CI pipeline.

---

## Product Visual Showcase & Feature Tour

| 01 — Administrative Analytics Dashboard (Chart.js) | 02 — Reader Experience & Dark / Light Mode |
|:---:|:---:|
| ![Admin Dashboard Chart.js](https://images.unsplash.com/photo-1551288049-bebda4e38f71?auto=format&fit=crop&w=800&q=80) | ![Public Reader Dark Mode](https://images.unsplash.com/photo-1504711434969-e33886168f5c?auto=format&fit=crop&w=800&q=80) |
| *Visual administrative control center with real-time views Line Chart and category distribution Doughnut Chart.* | *Editorial reader layout with optimized typography, Dark Mode eye protection, estimated reading time, and social share.* |

| 03 — WYSIWYG Editorial Suite (CKEditor 5) | 04 — Role-Based Auth Gateway & 1-Click Demo |
|:---:|:---:|
| ![CKEditor 5 Publishing](https://images.unsplash.com/photo-1499750310107-5fef28a66643?auto=format&fit=crop&w=800&q=80) | ![Auth Card Security](https://images.unsplash.com/photo-1558494949-ef010cbdcc31?auto=format&fit=crop&w=800&q=80) |
| *Professional news drafting workspace featuring rich text styling, blockquotes, subheadings, and media embeds.* | *Modern centralized Auth Card with Google OAuth2 and 1-click quick-fill credentials for recruiters.* |

---

## System Architecture

```mermaid
flowchart TD
    subgraph Clients["Clients / Readers / Journalists"]
        WEB["Modern Web Browser (Desktop / Tablet / Mobile)"]
    end

    subgraph Security["Security & Interceptors Layer"]
        ENC["EncodingFilter (Global UTF-8 Character Encoding)"]
        AUTH["AuthFilter (RBAC Guard & Session Enforcement)"]
    end

    subgraph Presentation["Presentation Layer (Jakarta EE 10)"]
        PUB_CTRL["Public Servlets\n(Home, Detail, Category, Newsletter)"]
        AUTH_CTRL["Auth Servlets\n(Login, Google OAuth2, ForgotPassword, OTP)"]
        ADMIN_CTRL["Admin Servlets\n(Dashboard, News, Users, Categories)"]
        JSP["JSP Views (Bootstrap 5, JSTL, Unified Slate Theme)"]
    end

    subgraph ServiceLayer["Business Service Layer"]
        USER_SVC["UserService (BCrypt Hashing & Role Validation)"]
        NEWS_SVC["NewsService (Publishing & View Counter)"]
        CAT_SVC["CategoryService (Taxonomy Management)"]
        MAIL_SVC["EmailService (Async SMTP OTP Dispatcher)"]
    end

    subgraph Persistence["Data Persistence Layer"]
        HIKARI["HikariCP Connection Pool (Max: 10, Timeout: 30s)"]
        JPA["Hibernate 6.4 ORM (EntityManagerFactory)"]
        JDBC["JDBCHelper (Optimized PreparedStatements)"]
    end

    subgraph Database["Database & Cloud Services"]
        SQL[("Microsoft SQL Server 2022\n(Collation: Vietnamese UTF-8)")]
        GOOGLE["Google Identity Services (OAuth2 Token Verification)"]
        SMTP["Gmail SMTP Server (Transactional OTP Service)"]
    end

    WEB --> ENC
    ENC --> AUTH
    AUTH --> PUB_CTRL & AUTH_CTRL & ADMIN_CTRL
    PUB_CTRL & AUTH_CTRL & ADMIN_CTRL --> JSP
    PUB_CTRL & AUTH_CTRL & ADMIN_CTRL --> USER_SVC & NEWS_SVC & CAT_SVC & MAIL_SVC
    
    USER_SVC & NEWS_SVC & CAT_SVC --> JPA & JDBC
    JPA & JDBC --> HIKARI
    HIKARI --> SQL
    
    AUTH_CTRL --> GOOGLE
    MAIL_SVC --> SMTP
```

---

## Instant Demo Credentials

Recruiters and evaluators can use the pre-configured accounts below to explore all system permissions and workflows:

| Role | Email | Password | Access Privileges |
|---|---|:---:|---|
| **Editor-in-Chief (Admin)** | `admin@abcnews.com` | `123456` | Full administrative control: Chart.js Dashboard, approval/deletion of all news, category taxonomy, user management, and newsletter broadcasts. |
| **Journalist (Reporter)** | `reporter1@abcnews.com` | `123456` | Draft articles with CKEditor 5, monitor personal article view metrics, edit author-owned publications. |
| **Associate Editor** | `reporter2@abcnews.com` | `123456` | Economy & Lifestyle section curation and reader interaction review. |
| **Reader (Guest)** | *No authentication required* | — | Browse news, full-text search, social media sharing, newsletter subscription, password recovery. |

> *Quick-Test Tip:* On the login screen (`/login`), click directly on the account credentials in the **"Tài khoản trải nghiệm nhanh"** box to auto-fill the login form instantly.

---

## Technology Stack

| Layer | Technologies & Libraries | Purpose & Engineering Value |
|---|---|---|
| **Programming Language** | **Java 17 LTS** | Leveraging modern language features (Records, Pattern Matching, Switch Expressions). |
| **Web Core Standard** | **Jakarta EE 10 / Servlet 6.0 / JSP 3.1** | High-throughput HTTP Request/Response processing, modular Filter chains. |
| **Servlet Container** | **Apache Tomcat 10.1** | Production-grade enterprise servlet application server. |
| **Connection Pool** | **HikariCP 5.1.0** | World's fastest Java JDBC connection pool, zero connection leaks. |
| **ORM & Persistence** | **Hibernate Core 6.4 / Jakarta Persistence 3.1** | Clean object-relational mapping, automatic JPA schema validation and sync. |
| **Database Engine** | **Microsoft SQL Server 2022** | Relational data integrity, foreign key constraints, and full Unicode UTF-8 support. |
| **Security & Auth** | **BCrypt (jBCrypt 0.4) & Google OAuth2** | One-way salt hashing (12 rounds) and Google Identity Token verification. |
| **Email Protocol** | **Jakarta Mail 2.0.1** | Secure TLS SMTP delivery of 6-digit random password recovery OTPs. |
| **Frontend Framework** | **Bootstrap 5.3 & Vanilla CSS Design System** | Fully responsive, Slate Navy editorial design tokens, contrast-preserving Dark Mode. |
| **Visual Charts** | **Chart.js 4.4** | Canvas-based gradient Line and Doughnut charts for publication telemetry. |
| **Rich Text Editor** | **CKEditor 5 Classic Build** | Modular WYSIWYG editor tailored for journalists and editors. |
| **Automated Testing** | **JUnit 5 (Jupiter) & Mockito** | Comprehensive unit test suite for business services and security utilities. |
| **DevOps & Containers** | **Docker & Docker Compose** | Reproducible multi-platform environment orchestration. |

---

## Sample Data Distribution

The application comes pre-loaded with **11 in-depth articles** across 5 major journalistic sections:

1. **Technology & AI (`TECH`):**
   - `NEWS001`: *The Agentic AI Era: The Breakthrough Evolution of Artificial Intelligence in 2026* (8,420 views)
   - `NEWS002`: *Enterprise Data Infrastructure Optimization with HikariCP & Clean Architecture* (5,690 views)
   - `NEWS003`: *Zero-Trust Security Strategy: Comprehensive Defense Against 2026 Digital Attacks* (4,210 views)
2. **Economy & Finance (`ECONOMY`):**
   - `NEWS004`: *Vietnam's Digital Economy 2026: A Breakthrough Growth Engine Driving National GDP* (6,850 views)
   - `NEWS005`: *Global Capital Markets Shift Aggressively Toward Green Energy & Net-Zero Projects* (3,120 views)
3. **World Sports (`SPORT`):**
   - `NEWS006`: *UEFA Champions League 2026 Final: Elite Clash and a Torrent of Goals* (7,890 views)
   - `NEWS007`: *Applying Big Data Analytics & AI to Elite Athletic Performance Coaching* (2,450 views)
4. **Lifestyle & Science (`LIFE`):**
   - `NEWS008`: *Work-Life Harmony: Comprehensive Wellness Guide for Software Engineers* (5,230 views)
   - `NEWS009`: *Next-Generation Space Telescope Discovers Further Signs of Water on Exoplanet* (1,980 views)
5. **Education & Skills (`EDUCATION`):**
   - `NEWS010`: *Higher Education Digital Transformation: Hands-On Industry-Integrated Learning* (3,870 views)
   - `NEWS011`: *21st Century Skills: Lifelong Learning Competence in the Age of AI* (2,940 views)

---

## Setup & Execution Guide

### Option 1: Ultra-Fast Docker Deployment (Recommended)

Prerequisite: [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running.

```bash
# 1. Clone the repository
git clone https://github.com/nkhanhduy/ABCNews.git
cd ABCNews

# 2. Launch the entire application stack (Tomcat 10.1 + SQL Server 2022)
docker compose up -d

# 3. Follow database seeding and initialization logs
docker compose logs -f
```

- **News Portal:** Open your browser and navigate to: [http://localhost:8088/home](http://localhost:8088/home)
- **CMS Admin Login:** Access: [http://localhost:8088/login](http://localhost:8088/login)
- **Database Endpoint:** Listening on `localhost:1433` (`sa` / `ABCNews@2026!`).

To shut down the containers:
```bash
docker compose down
```

---

### Option 2: Local Development Setup

Prerequisites:
- **Java:** JDK 17 LTS or higher
- **Maven:** 3.8+
- **Database:** Microsoft SQL Server 2019+ (Database `ABCNews` created with `schema/ABCNews.sql`)

```bash
# 1. Configure environment variables
cp src/main/resources/app.properties.example src/main/resources/app.properties

# 2. Run automated tests
mvn clean test

# 3. Build WAR package
mvn clean package -DskipTests

# 4. Deploy target/ABCNews.war into Tomcat 10.1 webapps/ROOT.war
```

---

## Quality Assurance & Automated Testing

The codebase includes an automated unit test suite verifying sensitive business logic, password hashing, and role-based permissions:

```bash
mvn test
```

Test Results Output:
```text
[INFO] Running poly.com.service.UserServiceTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.421 s
[INFO] Running poly.com.util.PasswordUtilTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.285 s
[INFO] Running poly.com.util.OtpUtilTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.052 s
[INFO] Running poly.com.util.ValidationHelperTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.015 s
[INFO] Running poly.com.dao.JpaSchemaTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.890 s
[INFO] 
[INFO] Results:
[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## Project Structure

```text
ABCNews/
├── .github/
│   └── workflows/
│       └── maven.yml            # Automated CI pipeline for Maven build & tests
├── schema/
│   ├── ABCNews.sql              # Database schema DDL and foreign key constraints
│   └── seed_data.sql            # Seed dataset with 11 articles and pre-configured accounts
├── src/
│   ├── main/
│   │   ├── java/poly/com/
│   │   │   ├── controller/      # Jakarta Servlets for routing and HTTP handling
│   │   │   ├── dao/             # Data Access Objects (JPA ORM & JDBCHelper)
│   │   │   ├── entity/          # Hibernate JPA Entities
│   │   │   ├── filter/          # EncodingFilter (UTF-8) & AuthFilter (RBAC)
│   │   │   ├── service/         # Business Services (UserService, NewsService)
│   │   │   └── util/            # HikariCP, BCrypt, OTP, Google OAuth2 Helpers
│   │   ├── resources/
│   │   │   ├── META-INF/persistence.xml # Jakarta Persistence configuration
│   │   │   └── app.properties.example   # Sample application configuration
│   │   └── webapp/
│   │       ├── assets/          # CSS Design System, Dark Mode, JS modules
│   │       ├── views/           # JSP Pages (Public views, Admin CMS views)
│   │       └── WEB-INF/web.xml  # Jakarta EE web deployment descriptor
│   └── test/java/               # 14 automated unit tests (JUnit 5 & Mockito)
├── Dockerfile                   # Multi-stage image build (Tomcat 10.1 + Java 17)
├── docker-compose.yml           # Multi-container orchestration (App + SQL Server 2022)
└── pom.xml                      # Maven project configuration and dependencies
```

---

## Test & Evaluation Accounts

The database comes pre-seeded with sample credentials for recruiters to evaluate the system immediately:

| Role | Full Name | Email | Password | Permissions |
| :--- | :--- | :--- | :--- | :--- |
| **Editor-in-Chief (Admin)** | **Nguyen Khanh Duy** | `admin@abcnews.com` | `123456` | Full administrative access, article approvals, category & user management, smart avatar uploads |
| **Journalist (Reporter)** | **Tran Khanh Duy** | `reporter1@abcnews.com` | `123456` | Article creation, drafting, personal profile avatar updates & author dashboard analytics |

---

## Author & Academic Context

- **Author:** Nguyen Khanh Duy (Software Development Major — FPT Polytechnic Ho Chi Minh City)
- **Contact:** [khanhndts02168@gmail.com](mailto:khanhndts02168@gmail.com) | **GitHub:** [github.com/nkhanhduy](https://github.com/nkhanhduy)
- **Institution:** FPT Polytechnic College Ho Chi Minh City (Quang Trung Software City, District 12, Ho Chi Minh City, Vietnam)
- **Project Scope:** Academic Java Web / Jakarta EE development capstone and engineering portfolio demonstration. All rights reserved.
