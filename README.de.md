[🇻🇳 Tiếng Việt](README.md) | [🇬🇧 English](README.en.md) | [🇩🇪 Deutsch](README.de.md)

# ABC News — Online-Nachrichtenportal & Redaktionssystem

<p align="left">
  <a href="https://github.com/nkhanhduy/ABCNews/actions/workflows/maven.yml"><img src="https://github.com/nkhanhduy/ABCNews/actions/workflows/maven.yml/badge.svg" alt="Java CI Build"></a>
  <img src="https://img.shields.io/badge/Java-17%20LTS-ED8B00?style=flat-square&logo=openjdk&logoColor=white" alt="Java 17 LTS">
  <img src="https://img.shields.io/badge/Jakarta%20EE-10-F37024?style=flat-square&logo=eclipsevert.x&logoColor=white" alt="Jakarta EE 10">
  <img src="https://img.shields.io/badge/Apache%20Tomcat-10.1-F8DC75?style=flat-square&logo=apachetomcat&logoColor=black" alt="Tomcat 10.1">
  <img src="https://img.shields.io/badge/SQL%20Server-2022-CC292B?style=flat-square&logo=microsoftsqlserver&logoColor=white" alt="SQL Server 2022">
  <img src="https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker&logoColor=white" alt="Docker">
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-2ea44f?style=flat-square" alt="License MIT"></a>
</p>

Ein Java-Webprojekt auf Basis von Jakarta EE (Servlet & JSP), das ein modernes Online-Nachrichtenportal für Leser und ein Redaktionssystem bereitstellt. Entwickelt im Jahr 2025 mit responsivem Design, Dunkelmodus sowie sicherer Dateiverwaltung.

**Autor:** Nguyen Khanh Duy  
**Fachbereich:** Softwareentwicklung — FPT Polytechnic College Ho-Chi-Minh-Stadt  
**Jahr:** 2025

---

## Bildschirmfotos

### 1. Leser-Oberfläche

| Helles Design | Dunkles Design |
|:---:|:---:|
| ![Startseite Light Mode](.github/images/home_light.png) | ![Startseite Dark Mode](.github/images/home_dark.png) |

| Kategoriefilter-Ansicht | Artikel-Detailansicht |
|:---:|:---:|
| ![Kategorie Tech](.github/images/category_tech.png) | ![Artikel-Detailansicht](.github/images/article_detail.png) |

| Lesermeinungen & Kommentarbereich |
|:---:|
| ![Leserkommentare](.github/images/feature_comments_public.png) |

### 2. Authentifizierungsbereich

| Universelle Anmeldeseite |
|:---:|
| ![Anmeldeseite](.github/images/login_page.png) |

### 3. Redaktionsverwaltung

| Analyse-Dashboard & Redaktionsübersicht |
|:---:|
| ![Admin Dashboard](.github/images/admin_dashboard.png) |

| Artikelverwaltung & Verfassen | Benutzer- & Rollenverwaltung |
|:---:|:---:|
| ![Artikelverwaltung](.github/images/admin_news.png) | ![Benutzerverwaltung](.github/images/admin_users.png) |

| Journalistenprofil & Avatar-Upload | Moderation der Leserkommentare |
|:---:|:---:|
| ![Admin Profile](.github/images/admin_profile.png) | ![Kommentarmoderation](.github/images/feature_comments_admin.png) |

---

## Hauptfunktionen

### Leserbereich
- **Nachrichten lesen:** Startseite mit Top-Nachrichten, aktuellen Beiträgen und meistgelesenen Artikeln.
- **Kategorien:** Strukturierung nach 5 Themenbereichen: Wirtschaft & Finanzen, Technologie & KI, Sport, Leben & Wissenschaft, Bildung.
- **Artikel-Detailansicht:** Vollständiger Inhalt, Autor, Veröffentlichungsdatum, Aufrufzähler, geschätzte Lesezeit und Social-Sharing-Buttons.
- **Lesezeichen & Später lesen:** Ermöglicht Lesern das Speichern interessanter Artikel direkt im Browser-`localStorage` und die komfortable Verwaltung über eine Offcanvas-Seitenleiste ohne vorherige Anmeldung.
- **Leserkommentare & Diskussionen:** Interaktives Einsenden von Meinungen zu Artikeln mit moderiertem Freigabeprozess und Spam-Schutz.
- **Theme-Umschalter:** Nahtloser Wechsel zwischen hellem und dunklem Modus direkt in der Menüleiste.
- **Newsletter:** E-Mail-Registrierung für aktuelle Nachrichtenbenachrichtigungen.

### Authentifizierung & Sicherheit
- **An- und Abmeldung:** Lokale Authentifizierung mit BCrypt-Passwortverschlüsselung; optionale Google-Anmeldung über OAuth2.
- **Passwortwiederherstellung & OTP:** Anforderung eines 6-stelligen OTP-Codes per E-Mail (5 Minuten gültig) zur sicheren Passwort-Rücksetzung.
- **Anti-Spam Rate Limiting:** 60-Sekunden-Abklingzeit für OTP-Anfragen über Gmail SMTP sowie Frequenzbegrenzung bei Kommentaren.
- **XSS-Bereinigung:** Filterung aller Eingaben mit der Jsoup-Bibliothek zum Eliminieren von schädlichen Skripten und Injektionen.
- **Sicherheitsfilter (AuthFilter):** Rollenbasierte Zugriffskontrolle für alle `/admin/*`-Pfade.

### Administrationsbereich
- **Dashboard:** Statistische Übersicht über Artikel, Benutzer, Kategorien, ausstehende Kommentare und Newsletter-Abonnenten mit Chart.js-Diagrammen.
- **Artikelverwaltung:** Neue Artikel mit Rich-Text-Editor erstellen, bestehende Beiträge bearbeiten, löschen, filtern und Top-Artikel auf der Startseite anpinnen.
- **Automatische Entwurfsspeicherung:** Alle 30 Sekunden automatisches Sichern des Artikelentwurfs im `localStorage` inklusive Wiederherstellungsdialog.
- **Kommentar-Moderation:** Zentrale Verwaltung von Leserkommentaren (Ausstehend, Genehmigt, Abgelehnt) mit Audit-Protokollierung.
- **Kategorienverwaltung:** Verwaltung der Themenkategorien und Bezeichner.
- **Benutzerverwaltung:** Übersicht der Benutzerkonten, Rollenvergabe und Kontosperrung/-aktivierung.
- **Benutzerprofil:** Einsehen der Profildaten und Aktualisieren des Avatars mit Sofortvorschau und sicherer Ordnerpartitionierung.
- **Datenexport:** Export von Berichten in den Formaten Excel, CSV und PDF.

---

## Schnelle Test-Zugänge

| Rolle | E-Mail | Passwort | Berechtigungen |
|---|---|:---:|---|
| **Chefredakteur** | `admin@abcnews.com` | `123456` | Vollzugriff: Inhalte, Kommentarmoderation, Benutzerverwaltung |
| **Reporter** | `reporter1@abcnews.com` | `123456` | Verfassen von Artikeln, eigene Beiträge, Leserkommentare |

---

## Verwendete Technologien

- **Plattform:** Java 17 LTS, Jakarta EE 10 (Servlet 6.0, JSP 3.1, JSTL)
- **Anwendungsserver:** Apache Tomcat 10.1
- **Datenbank:** Microsoft SQL Server 2022 (HikariCP Connection-Pool, Hibernate ORM JPA)
- **Sicherheit & Sanitization:** BCrypt-Passwort-Hashing, Google Identity Services, Jakarta Mail (SMTP TLS für OTP), Jsoup 1.17.2 (HTML Whitelist Sanitization)
- **Frontend:** HTML5, CSS3, Bootstrap 5.3, FontAwesome 6, Chart.js, CKEditor 5
- **Bereitstellung:** Docker & Docker Compose

---

## Architektur & Technische Highlights

- **Standardisiertes Model-2-MVC-Muster:** Saubere Trennung von Controller (Jakarta Servlets), Präsentationsschicht (modulare JSP/JSTL-Komponenten) und Datenzugriffsschicht (DAO & JPA Hibernate).
- **Hochleistungsfähige Persistenz:** **HikariCP Connection-Pool** minimiert Latenzzeiten bei Datenbankverbindungen; Hibernate ORM gewährleistet typsichere Entitätsabbildungen; Indexoptimierungen auf der Newstabelle beschleunigen das Filtern.
- **Mehrschichtige Sicherheit:**
  - Einweg-Passwort-Hashing mit **BCrypt** verhindert Rainbow-Table- und Brute-Force-Angriffe.
  - **AuthFilter** setzt rollenbasierte Zugriffskontrolle (RBAC) für alle `/admin/*`-Routen durch.
  - **Kryptographisch sichere Bezeichner:** Implementierung von 128-Bit-UUID-v4-Bezeichnern für alle Nachrichtenartikel in der Datenbank und auf öffentlichen URLs, wodurch vorhersehbare IDs eliminiert werden.
  - Das Modul **SafeImageStorage** validiert MIME-Typen, begrenzt Dateigrößen, schützt vor Path-Traversal-Angriffen und bereinigt veraltete Avatar-Dateien.
  - Sichere Passwortwiederherstellung über zeitlich begrenzte 6-stellige **OTP-Token** per Gmail SMTP TLS.
- **Benutzererlebnis & Barrierefreiheit:**
  - Modernes Design-Token-System auf Basis von CSS Custom Properties im smaragdgrünen Look.
  - Kontrastreicher Dark Mode nach Barrierefreiheitsstandards für ermüdungsfreies Lesen.
  - Interaktives **Chart.js** Telemetrie-Dashboard und integrierter Rich-Text-Editor.
  - **SEO- & Open-Graph-Protokoll-Integration:** Vollständige Open-Graph-Metadaten (`og:title`, `og:image`, `og:description`, `og:url`) und Twitter Cards (`summary_large_image`) für alle Artikeldetailseiten.
- **Produktionsreife Containerisierung:** Vollständige **Docker-Compose**-Orchestrierung (Tomcat + SQL Server) mit automatisierten Healthchecks, Schema-Initialisierung und Testdatenbereitstellung mit nur einem Befehl: `docker compose up -d`.

---

## Installation & Start

### Option 1: Start mit Docker Compose

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
