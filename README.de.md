[🇻🇳 Tiếng Việt](README.md) | [🇬🇧 English](README.en.md) | [🇩🇪 Deutsch](README.de.md)

# ABC News — Online-Nachrichtenportal & Redaktionssystem (CMS)

Ein Java-Webprojekt auf Basis von Jakarta EE (Servlet & JSP), das ein Online-Nachrichtenportal für Leser und ein Content-Management-System (CMS) für Redaktionsmitglieder bereitstellt. Das Projekt entstand im Jahr 2025 und wurde mit responsivem Design, Dunkelmodus (Dark Mode) sowie sicherer Dateiverwaltung optimiert.

**Autor:** Nguyen Khanh Duy  
**Fachbereich:** Softwareentwicklung — FPT Polytechnic College Ho-Chi-Minh-Stadt  
**Jahr:** 2025 (Optimierte Version)

---

## Bildschirmfotos

### 1. Leser-Oberfläche (Öffentlicher Bereich)

| Helles Design (Smaragdgrün) | Dunkles Design (Augenkomfort) |
|:---:|:---:|
| ![Startseite Light Mode](.github/images/home_light.png) | ![Startseite Dark Mode](.github/images/home_dark.png) |

| Kategoriefilter-Ansicht (Tech & KI) | Artikel-Detailansicht & Teilen |
|:---:|:---:|
| ![Kategorie Tech](.github/images/category_tech.png) | ![Artikel-Detailansicht](.github/images/article_detail.png) |

### 2. Authentifizierungsbereich

| Universelle Anmeldeseite (Admin & Reporter) |
|:---:|
| ![Anmeldeseite](.github/images/login_page.png) |

### 3. Redaktionsverwaltung (Admin CMS)

| Analyse-Dashboard & Redaktionsübersicht (Vollansicht) |
|:---:|
| ![Admin Dashboard](.github/images/admin_dashboard.png) |

| Artikelverwaltung & Verfassen (CKEditor) | Benutzer- & Rollenverwaltung |
|:---:|:---:|
| ![Artikelverwaltung](.github/images/admin_news.png) | ![Benutzerverwaltung](.github/images/admin_users.png) |

| Journalistenprofil & Intelligenter Avatar-Upload |
|:---:|
| ![Admin Profile](.github/images/admin_profile.png) |



---

## Hauptfunktionen

### Leserbereich (Public)
- **Nachrichten lesen:** Startseite mit Top-Nachrichten, aktuellen Beiträgen und meistgelesenen Artikeln.
- **Kategorien:** Strukturierung nach 5 Themenbereichen (Wirtschaft & Finanzen, Technologie & KI, Sport, Leben & Wissenschaft, Bildung).
- **Artikel-Detailansicht:** Vollständiger Inhalt, Autor, Veröffentlichungsdatum, Aufrufzähler, geschätzte Lesezeit und Social-Sharing-Buttons (Facebook, X, Telegram).
- **Theme-Umschalter:** Nahtloser Wechsel zwischen hellem Modus (Smaragdgrün) und dunklem Modus (Dark Mode).
- **Newsletter:** E-Mail-Registrierung für aktuelle Nachrichtenbenachrichtigungen.

### Authentifizierung & Sicherheit
- **An- und Abmeldung:** Lokale Authentifizierung mit BCrypt-Passwortverschlüsselung; optionale Google-Anmeldung über OAuth2.
- **Passwortwiederherstellung (OTP):** Anforderung eines 6-stelligen OTP-Codes per E-Mail (5 Minuten gültig) zur sicheren Passwort-Rücksetzung.
- **Sicherheitsfilter (AuthFilter):** Rollenbasierte Zugriffskontrolle für alle `/admin/*`-Pfade.

### Administrationsbereich (Admin CMS)
- **Dashboard:** Statistische Übersicht über Artikel, Benutzer, Kategorien und Newsletter-Abonnenten mit Chart.js-Diagrammen.
- **Artikelverwaltung:** Neue Artikel mit Rich-Text-Editor erstellen, bestehende Beiträge bearbeiten, löschen, nach Kategorie/Autor filtern und Top-Artikel auf der Startseite anpinnen.
- **Kategorienverwaltung:** Verwaltung der Themenkategorien (Kategorie-ID, Name).
- **Benutzerverwaltung:** Übersicht der Benutzerkonten, Rollenvergabe (Admin / Reporter) und Kontosperrung/-aktivierung.
- **Benutzerprofil:** Einsehen der Profildaten und Aktualisieren des Avatars mit Sofortvorschau (Instant Preview) und sicherer Ordnerpartitionierung (`SafeImageStorage`).
- **Datenexport:** Export von Berichten in den Formaten Excel, CSV und PDF.

---

## Testkonten

In der Datenbank sind vorbereitete Testkonten vorhanden:

| Rolle | E-Mail | Passwort | Berechtigungen |
|---|---|:---:|---|
| **Administrator (Admin)** | `admin@abcnews.com` | `123456` | Vollzugriff auf Artikel, Kategorien, Benutzer und Profil |
| **Reporter** | `reporter1@abcnews.com` | `123456` | Verfassen von Artikeln, Verwaltung eigener Beiträge und Profilaktualisierung |

---

## Verwendete Technologien

- **Plattform:** Java 17 LTS, Jakarta EE 10 (Servlet 6.0, JSP 3.1, JSTL)
- **Anwendungsserver:** Apache Tomcat 10.1
- **Datenbank:** Microsoft SQL Server 2022 (HikariCP Connection-Pool, Hibernate ORM JPA)
- **Sicherheit:** BCrypt-Passwort-Hashing (OWASP-Standard), Google Identity Services, Jakarta Mail (SMTP TLS für OTP)
- **Frontend:** HTML5, CSS3, Bootstrap 5.3, FontAwesome 6, Chart.js, CKEditor
- **Bereitstellung:** Docker & Docker Compose

---

## Architektur & Technische Highlights

- **Standardisiertes Model-2-MVC-Muster:** Saubere Trennung von Controller (Jakarta Servlets), Präsentationsschicht (modulare JSP/JSTL-Komponenten) und Datenzugriffsschicht (DAO & JPA Hibernate).
- **Hochleistungsfähige Persistenz:** **HikariCP Connection-Pool** minimiert Latenzzeiten bei Datenbankverbindungen; Hibernate ORM gewährleistet typsichere Entitätsabbildungen; Indexoptimierungen auf der Newstabelle beschleunigen das Filtern nach Kategorien und Lesezahlen.
- **Mehrschichtige Sicherheit:**
  - Einweg-Passwort-Hashing mit **BCrypt** verhindert Rainbow-Table- und Brute-Force-Angriffe.
  - **AuthFilter** setzt rollenbasierte Zugriffskontrolle (RBAC) für alle `/admin/*`-Routen durch.
  - Das Modul **SafeImageStorage** validiert MIME-Typen, begrenzt Dateigrößen, schützt vor Path-Traversal-Angriffen (`../`) und bereinigt veraltete Avatar-Dateien bei Updates.
  - Sichere Passwortwiederherstellung über zeitlich begrenzte 6-stellige **OTP-Token** per Gmail SMTP TLS (5 Minuten Gültigkeit).
- **Benutzererlebnis & Barrierefreiheit (UX/UI):**
  - Modernes Design-Token-System auf Basis von CSS Custom Properties im smaragdgrünen Look.
  - Kontrastreicher Dark Mode nach WCAG-AAA-Standard für ermüdungsfreies Lesen.
  - Interaktives **Chart.js** Telemetrie-Dashboard und integrierter **CKEditor 5** WYSIWYG-Editor.
- **Produktionsreife Containerisierung:** Vollständige **Docker-Compose**-Orchestrierung (Tomcat + SQL Server) mit automatisierten Healthchecks, Schema-Initialisierung und Testdatenbereitstellung mit nur einem Befehl: `docker compose up -d`.


---

## Installation & Start

### Option 1: Start mit Docker Compose (Empfohlen)

Voraussetzung: Installiertes [Docker Desktop](https://www.docker.com/products/docker-desktop/).

```bash
# 1. Repository klonen
git clone https://github.com/nkhanhduy/ABCNews.git
cd ABCNews

# 2. Anwendung und SQL Server starten
docker compose up -d

# 3. Im Browser öffnen:
# - Startseite: http://localhost:8088/home
# - Anmeldung: http://localhost:8088/login
```

Zum Beenden:
```bash
docker compose down
```

### Option 2: Lokale Entwicklung

Voraussetzungen: JDK 17, Maven 3.8+, SQL Server 2019+ und Apache Tomcat 10.1.

1. Erstellen Sie die Datenbank `ABCNews` in SQL Server, führen Sie `schema/ABCNews.sql` aus und laden Sie Beispieldaten aus `schema/seed_data.sql`.
2. Passen Sie die Verbindungsparameter in `src/main/resources/app.properties` an.
3. Anwendung kompilieren und paketieren:
   ```bash
   mvn clean package -DskipTests
   ```
4. Erzeugte `.war`-Datei aus dem Ordner `target/` in das Verzeichnis `webapps` von Tomcat 10.1 kopieren.

---

## Autor

- **Name:** Nguyen Khanh Duy
- **Fachbereich:** Softwareentwicklung — FPT Polytechnic College Ho-Chi-Minh-Stadt
- **GitHub:** [github.com/nkhanhduy](https://github.com/nkhanhduy)
- **E-Mail:** khanhndts02168@gmail.com
- **Jahr:** 2025
