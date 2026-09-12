[🇻🇳 Tiếng Việt](README.md) | [🇬🇧 English](README.en.md) | [🇩🇪 Deutsch](README.de.md)

# ABC News — Online Journalism & Editorial Management

<p align="left">
  <a href="https://github.com/nkhanhduy/ABCNews/actions/workflows/maven.yml"><img src="https://github.com/nkhanhduy/ABCNews/actions/workflows/maven.yml/badge.svg" alt="Java CI Build"></a>
  <img src="https://img.shields.io/badge/Java-17%20LTS-ED8B00?style=flat-square&logo=openjdk&logoColor=white" alt="Java 17 LTS">
  <img src="https://img.shields.io/badge/Jakarta%20EE-10-F37024?style=flat-square&logo=eclipsevert.x&logoColor=white" alt="Jakarta EE 10">
  <img src="https://img.shields.io/badge/Apache%20Tomcat-10.1-F8DC75?style=flat-square&logo=apachetomcat&logoColor=black" alt="Tomcat 10.1">
  <img src="https://img.shields.io/badge/SQL%20Server-2022-CC292B?style=flat-square&logo=microsoftsqlserver&logoColor=white" alt="SQL Server 2022">
  <img src="https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker&logoColor=white" alt="Docker">
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-2ea44f?style=flat-square" alt="License MIT"></a>
</p>

A Java Web platform built with Jakarta EE (Servlet & JSP) providing an online news portal for readers and a modern editorial administration system. Developed in 2025 with responsive design, Dark Mode support, and secure image storage.

---

## Screenshots

### 1. Reader Interface

| Light Theme | Dark Theme |
|:---:|:---:|
| ![Home Light Mode](.github/images/home_light.png) | ![Home Dark Mode](.github/images/home_dark.png) |

| Category Filter View | Article Detail & Social Share |
|:---:|:---:|
| ![Category Tech](.github/images/category_tech.png) | ![Article Detail](.github/images/article_detail.png) |

| Reader Comments & Discussion Area |
|:---:|
| ![Reader Comments](.github/images/feature_comments_public.png) |

### 2. Authentication Portal

| Universal Sign-In Page |
|:---:|
| ![Login Page](.github/images/login_page.png) |

### 3. Editorial Management System

| Analytics & Editorial Dashboard |
|:---:|
| ![Admin Dashboard](.github/images/admin_dashboard.png) |

| Article Management & Publishing | User & Role Management |
|:---:|:---:|
| ![News Management](.github/images/admin_news.png) | ![User Management](.github/images/admin_users.png) |

| Journalist Profile & Avatar Upload | Moderated Reader Comments |
|:---:|:---:|
| ![Admin Profile](.github/images/admin_profile.png) | ![Admin Comments](.github/images/feature_comments_admin.png) |

---

## Key Features

### Reader Portal
- **News Reading:** Home page featuring top highlighted stories, latest articles, and most-read news.
- **Categories:** Organized across 5 topics: Economy & Finance, Tech & AI, Sports, Science & Life, Education.
- **Article Details:** Full content view, author metadata, publication date, view counter, estimated reading time, and social sharing links.
- **Bookmarks & Read Later:** Allows readers to save favorite articles for later reading directly via browser `localStorage` and manage them effortlessly via an Offcanvas drawer without mandatory login.
- **Reader Comments & Discussions:** Interactive comment submission with real-time feedback, protected by moderation workflow and rate limiting.
- **Theme Switcher:** Easy toggle between Light Mode and Dark Mode with navbar action.
- **Newsletter Subscription:** Email signup form for news updates.

### Authentication & Security
- **Login & Logout:** Local account authentication with BCrypt password hashing; optional Google Sign-In via OAuth2.
- **Password Recovery & OTP:** Request a 6-digit OTP code sent via Gmail SMTP (expires in 5 minutes) to securely reset passwords.
- **Anti-Spam Rate Limiting:** Enforces a 60-second cooldown on forgot-password OTP requests via Gmail SMTP and limits comment submission rates.
- **Defense-in-Depth XSS Sanitization:** Employs Jsoup HTML sanitization with relaxed whitelist on rich-text inputs and comment submissions, actively stripping dangerous scripts and injections.
- **Access Filter (AuthFilter):** Role-based access control protecting all `/admin/*` routes.

### Editorial Administration
- **Dashboard:** Overview metrics showing total counts of articles, users, categories, pending comments, and newsletter subscribers; Chart.js charts.
- **Article Management:** Add new articles with rich text editing, edit existing posts, delete articles, filter by category/author, and pin featured stories to the homepage.
- **Auto-save Drafts:** Periodically backs up draft articles every 30 seconds to `localStorage`, with draft recovery detection on form load.
- **Moderated Comments Management:** Dedicated comment moderation hub for approving, rejecting, or deleting public feedback, with audit logging.
- **Category Management:** Manage news categories and topic slugs.
- **User Management:** View user accounts, edit details, assign roles, and toggle account active/locked status.
- **User Profile:** View personal account details and update profile picture with instant preview and secure folder storage.
- **Data Export:** Export reports to Excel, CSV, and PDF formats.

---

## Quick Demo Accounts
 
| Role | Email | Password | Permissions |
|---|---|:---:|---|
| **Editor-in-Chief** | `admin@abcnews.com` | `123456` | Full administrative control: content, comment moderation, users |
| **Reporter** | `reporter1@abcnews.com` | `123456` | Article creation, personal news management, comments |

---

## Tech Stack

- **Platform:** Java 17 LTS, Jakarta EE 10 (Servlet 6.0, JSP 3.1, JSTL)
- **Application Server:** Apache Tomcat 10.1
- **Database:** Microsoft SQL Server 2022 (HikariCP connection pool, Hibernate ORM JPA)
- **Security & Sanitization:** BCrypt password hashing, Google Identity Services, Jakarta Mail (SMTP TLS for OTP), Jsoup 1.17.2 (HTML Whitelist Sanitization)
- **Frontend:** HTML5, CSS3, Bootstrap 5.3, FontAwesome 6, Chart.js, CKEditor 5
- **Deployment:** Docker & Docker Compose

---

## Architecture & Engineering Highlights

- **Standard Model 2 MVC Pattern:** Strict separation of concerns across Controller (Jakarta Servlets), View (modular JSP/JSTL components), and Data Access (DAO & JPA Hibernate).
- **High-Performance Persistence:** **HikariCP Connection Pooling** eliminates connection acquisition latency; Hibernate ORM enforces clean entity mappings; index optimization on the News table speeds up queries.
- **Multi-Layered Security:**
  - One-way **BCrypt** password hashing prevents rainbow table and credential brute-force attacks.
  - **AuthFilter** enforces role-based access control (RBAC) across all `/admin/*` routes.
  - **Cryptographically Secure Identifiers:** Adopts 128-bit secure UUID v4 strings for all news article identifiers across the database and public URLs, eliminating predictable sequential IDs.
  - **SafeImageStorage** module validates MIME types, enforces upload size limits, guards against Path Traversal attacks, and purges obsolete avatar files on updates.
  - Secure password reset via time-bounded 6-digit **OTP tokens** delivered over Gmail SMTP TLS.
- **Refined UX & Accessibility:**
  - Modern Design System built on CSS Custom Properties featuring a polished Emerald Green identity.
  - High-contrast Dark Mode complying with accessibility readability standards.
  - Interactive **Chart.js** telemetry dashboard and rich-text authoring experience.
  - **SEO & Open Graph Protocol Integration:** Complete Open Graph metadata (`og:title`, `og:image`, `og:description`, `og:url`) and Twitter Cards (`summary_large_image`) for all articles.
- **Production-Ready Containerization:** Multi-container **Docker Compose** orchestration (Tomcat + SQL Server) with automated health checks, schema generation, and seed data initialization with a single `docker compose up -d` command.

---

## Installation & Running

### Option 1: Run with Docker Compose

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
