[🇻🇳 Tiếng Việt](README.md) | [🇬🇧 English](README.en.md) | [🇩🇪 Deutsch](README.de.md)

# ABC News — Online Journalism & Content Management System (CMS)

A Java Web project built with Jakarta EE (Servlet & JSP) providing an online news reading portal for readers and a Content Management System (CMS) for editorial staff. Originally developed in 2025 and recently updated with responsive UI improvements, Dark Mode support, and secure image storage.

**Author:** Nguyen Khanh Duy  
**Major:** Software Development — FPT Polytechnic College Ho Chi Minh City  
**Year:** 2025 (Optimized Version)

---

## Screenshots

### 1. Reader Interface (Public Portal)

| Light Theme (Emerald Green) | Dark Theme (Eye Comfort) |
|:---:|:---:|
| ![Home Light Mode](.github/images/home_light.png) | ![Home Dark Mode](.github/images/home_dark.png) |

| Category Filter View (Tech & AI) | Article Detail & Social Share |
|:---:|:---:|
| ![Category Tech](.github/images/category_tech.png) | ![Article Detail](.github/images/article_detail.png) |

### 2. Authentication Portal

| Universal Sign-In Page (Admin & Reporter) |
|:---:|
| ![Login Page](.github/images/login_page.png) |

### 3. Editorial CMS (Admin & Management)

| Analytics & Editorial Dashboard (Full Overview) |
|:---:|
| ![Admin Dashboard](.github/images/admin_dashboard.png) |

| Article Management & Publishing (CKEditor) | User & Role Management |
|:---:|:---:|
| ![News Management](.github/images/admin_news.png) | ![User Management](.github/images/admin_users.png) |

| Journalist Profile & Smart Avatar Upload |
|:---:|
| ![Admin Profile](.github/images/admin_profile.png) |



---

## Key Features

### Reader Portal (Public)
- **News Reading:** Home page featuring top highlighted stories, latest articles, and most-read news.
- **Categories:** Organized across 5 topics (Economy & Finance, Tech & AI, Sports, Science & Life, Education).
- **Article Details:** Full content view, author metadata, publication date, view counter, estimated reading time, and social sharing links (Facebook, X, Telegram).
- **Theme Switcher:** Easy toggle between Light Mode (Emerald Green) and Dark Mode.
- **Newsletter Subscription:** Email signup form for news updates.

### Authentication & Security
- **Login / Logout:** Local account authentication with BCrypt password hashing; optional Google Sign-In via OAuth2.
- **Password Recovery (OTP):** Request a 6-digit OTP code sent via Gmail SMTP (expires in 5 minutes) to securely reset passwords.
- **Access Filter (AuthFilter):** Role-based access control protecting all `/admin/*` routes.

### Editorial CMS (Admin)
- **Dashboard:** Overview metrics showing total counts of articles, users, categories, and newsletter subscribers; Chart.js charts.
- **Article Management:** Add new articles with rich text editing, edit existing posts, delete articles, filter by category/author, and pin featured stories to the homepage.
- **Category Management:** Manage news categories (Category ID, Name).
- **User Management:** View user accounts, edit details, assign roles (Admin / Reporter), and toggle account active/locked status.
- **User Profile:** View personal account details and update profile picture with instant preview and secure folder-partitioned storage (`SafeImageStorage`).
- **Data Export:** Export reports to Excel, CSV, and PDF formats.

---

## Test Accounts

The seed database includes ready-to-use accounts for testing:

| Role | Email | Password | Permissions |
|---|---|:---:|---|
| **Admin** | `admin@abcnews.com` | `123456` | Full administrative access to articles, categories, users, and profile |
| **Reporter** | `reporter1@abcnews.com` | `123456` | Article creation, personal news management, and profile update |

---

## Tech Stack

- **Platform:** Java 17 LTS, Jakarta EE 10 (Servlet 6.0, JSP 3.1, JSTL)
- **Application Server:** Apache Tomcat 10.1
- **Database:** Microsoft SQL Server 2022 (HikariCP connection pool, Hibernate ORM JPA)
- **Security:** BCrypt password hashing (OWASP standard), Google Identity Services, Jakarta Mail (SMTP TLS for OTP)
- **Frontend:** HTML5, CSS3, Bootstrap 5.3, FontAwesome 6, Chart.js, CKEditor
- **Deployment:** Docker & Docker Compose

---

## Architecture & Engineering Highlights

- **Standard Model 2 MVC Pattern:** Strict separation of concerns across Controller (Jakarta Servlets), View (modular JSP/JSTL components), and Data Access (DAO & JPA Hibernate).
- **High-Performance Persistence:** **HikariCP Connection Pooling** eliminates connection acquisition latency; Hibernate ORM enforces clean entity mappings; index optimization on the News table speeds up category filtering and hot article ranking.
- **Multi-Layered Security:**
  - One-way **BCrypt** password hashing prevents rainbow table and credential brute-force attacks.
  - **AuthFilter** enforces role-based access control (RBAC) across all `/admin/*` routes to block privilege escalation.
  - **UUID v4 Standardization (RFC 4122):** Adopts 128-bit cryptographically secure UUID v4 strings (36 characters) for all news article identifiers across the database and public URLs (`/detail?id=...`). This completely eliminates predictable sequential IDs and protects the system against ID Enumeration and Insecure Direct Object References (IDOR).
  - **SafeImageStorage** module validates MIME types, enforces upload size limits, guards against Path Traversal (`../`) attacks, and purges obsolete avatar files on updates.
  - Secure password reset via time-bounded 6-digit **OTP tokens** delivered over Gmail SMTP TLS (5-minute expiration).
- **Refined UX & Accessibility:**
  - Modern Design System built on CSS Custom Properties featuring a polished Emerald Green identity.
  - High-contrast Dark Mode complying with WCAG AAA readability standards.
  - Interactive **Chart.js** telemetry dashboard and **CKEditor 5** rich-text authoring experience.
- **Production-Ready Containerization:** Multi-container **Docker Compose** orchestration (Tomcat + SQL Server) with automated health checks, schema generation, and seed data initialization with a single `docker compose up -d` command.


---

## Installation & Running

### Option 1: Run with Docker Compose (Recommended)

Requires [Docker Desktop](https://www.docker.com/products/docker-desktop/).

```bash
# 1. Clone repository
git clone https://github.com/nkhanhduy/ABCNews.git
cd ABCNews

# 2. Start app and SQL Server database
docker compose up -d

# 3. Access in browser:
# - Homepage: http://localhost:8088/home
# - Login: http://localhost:8088/login
```

To stop containers:
```bash
docker compose down
```

### Option 2: Local Development

Prerequisites: JDK 17, Maven 3.8+, SQL Server 2019+, and Apache Tomcat 10.1.

1. Create the `ABCNews` database in SQL Server, run `schema/ABCNews.sql`, and populate sample data from `schema/seed_data.sql`.
2. Configure database credentials in `src/main/resources/app.properties`.
3. Build the project:
   ```bash
   mvn clean package -DskipTests
   ```
4. Deploy the generated `.war` file from `target/` to Apache Tomcat 10.1 `webapps` folder.

---

## Author

- **Name:** Nguyen Khanh Duy
- **Major:** Software Development — FPT Polytechnic College Ho Chi Minh City
- **GitHub:** [github.com/nkhanhduy](https://github.com/nkhanhduy)
- **Email:** khanhndts02168@gmail.com
- **Year:** 2025
