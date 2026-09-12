[🇻🇳 Tiếng Việt](README.md) | [🇬🇧 English](README.en.md) | [🇩🇪 Deutsch](README.de.md)

# ABCNews — Nachrichtenportal & Redaktionsverwaltung

<p align="left">
  <a href="https://github.com/nkhanhduy/ABCNews/actions/workflows/maven.yml"><img src="https://github.com/nkhanhduy/ABCNews/actions/workflows/maven.yml/badge.svg" alt="Java CI Build"></a>
  <img src="https://img.shields.io/badge/Java-17%20LTS-ED8B00?style=flat-square&logo=openjdk&logoColor=white" alt="Java 17 LTS">
  <img src="https://img.shields.io/badge/Jakarta%20EE-10-F37024?style=flat-square&logo=eclipsevert.x&logoColor=white" alt="Jakarta EE 10">
  <img src="https://img.shields.io/badge/Apache%20Tomcat-10.1-F8DC75?style=flat-square&logo=apachetomcat&logoColor=black" alt="Tomcat 10.1">
  <img src="https://img.shields.io/badge/SQL%20Server-2022-CC292B?style=flat-square&logo=microsoftsqlserver&logoColor=white" alt="SQL Server 2022">
  <img src="https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker&logoColor=white" alt="Docker">
  <img src="https://img.shields.io/badge/Tests-97%20passed-success?style=flat-square&logo=junit5&logoColor=white" alt="Tests 97 passed">
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-2ea44f?style=flat-square" alt="License MIT"></a>
</p>

## Über das Projekt

ABCNews ist ein persönliches Lernprojekt, das ich während meines Studiums im Bereich Softwareentwicklung am FPT Polytechnic College entwickelt habe, um Java-Webentwicklung und grundlegende Backend-Kenntnisse praktisch anzuwenden. Das System umfasst ein Online-Nachrichtenportal für Leser und einen redaktionellen Verwaltungsbereich mit rollenbasierter Autorisierung, Benutzer-Authentifizierung und sicheren Datenhaltungsmethoden.

---

## Kerntechnologien

`Java 17` · `Jakarta EE 10` · `Servlet` · `JSP` · `SQL Server 2022` · `JDBC` · `HikariCP` · `Maven`

| Komponente | Technologie und Bibliothek | Technische Hinweise |
|---|---|---|
| Plattform und Sprache | Java 17 LTS, Jakarta EE 10 | Servlet 6.0, JSP 3.1, JSTL 3.0 |
| Primärer Datenzugriff | JDBC mit HikariCP 5.1 | Datenzugriff über PreparedStatement und Connection-Pool |
| Unterstützendes Entity-Mapping | Hibernate ORM 6.4 | Entitätsdefinitionen und Schema-Validierung |
| Anwendungsserver | Apache Tomcat 10.1 | Laufzeitumgebung für Servlet und JSP |
| Datenbank | Microsoft SQL Server 2022 | Relationale Datenhaltung mit versionierten Migrationen |
| Authentifizierung und Hashing | BCrypt, SecureRandom, Google API Client | Passwort-Hashing, 256-Bit Remember-Me Token, Google ID-Token Überprüfung |
| Web-Sicherheit | CsrfFilter, Jsoup 1.17.2, SafeImageStorage | CSRF-Prüfung für POST, HTML-Bereinigung und Datei-Validierung |
| Automatisierte Tests | JUnit 5, Mockito | 97 automatisierte Tests für zentrale Geschäfts- und Sicherheitsabläufe |
| Build und CI | Apache Maven, GitHub Actions | WAR-Paketierung und automatisierte Testausführung bei Push oder Pull Request |
| Betriebsumgebung | Docker, Docker Compose | Standardisierte lokale Container-Umgebung mit Tomcat und SQL Server |

---

## Hauptfunktionen

### Leserfunktionen
- Nachrichten auf der Startseite mit Top-Meldungen, neuesten Beiträgen und meistgelesenen Artikeln.
- Kategorienaufruf über suchmaschinenfreundliche URLs nach dem Muster `/category/{slug}`.
- Artikel-Detailansicht mit Autorenangaben, Veröffentlichungsdatum, Aufrufzähler und geschätzter Lesezeit.
- Lesezeichenfunktion zum späteren Lesen direkt im Browser über localStorage.
- Einsenden von Leserkommentaren mit ausstehendem Moderationsstatus.
- Umschalten zwischen hellem und dunklem Design direkt in der Navigationsleiste.
- E-Mail-Abonnement für redaktionelle Benachrichtigungen.

### Authentifizierung und Sicherheit
- Lokale Anmeldung mit Passwort-Hashing über den BCrypt-Algorithmus.
- Google-Anmeldung mit serverseitiger Überprüfung des ID-Tokens.
- Passwort ändern: Angemeldete Benutzer können ihr Passwort ändern, nachdem sie ihr aktuelles Passwort bestätigt haben. Bestehende Remember-Me-Tokens werden nach der Passwortänderung widerrufen.
- Passwort-Rücksetzung über 6-stellige OTP-Codes per Gmail SMTP mit 5 Minuten Gültigkeit und Begrenzung auf maximal 3 Fehlversuche.
- Dauerhafte Anmeldung über kryptographische 256-Bit-Zufallstoken, SHA-256 Datenbank-Hashing und Token-Rotation bei erfolgreicher automatischer Anmeldung.
- Prüfung des Kontostatus während der Authentifizierung und im Zugriffsfilter.

### Rollenbasierte Autorisierung
- Das System verwendet die Rollen Reporter, Admin und Super Admin zur Eingrenzung verfügbarer Aktionen.
- Der Filter AuthFilter sichert alle Pfade unter `/admin/*` ab.
- Detaillierte Autorisierungsregeln werden serverseitig in Controllern, Services und SecurityHelper durchgesetzt.
- Reporter können ausschließlich Artikel bearbeiten oder löschen, bei denen sie selbst als Autor eingetragen sind.
- Administratoren verwalten alle Artikel, Kategorien und die Moderation von Kommentaren.
- Der Super-Admin-Status wird über das Feld IsSuperAdmin in der Datenbank festgelegt, erlaubt die Verwaltung anderer Admin-Konten und verhindert das Selbstlöschen oder Selbstsperren.
- Benutzerverwaltung: Administratoren können Kontoinformationen aktualisieren, Rollen zuweisen, den Kontostatus ändern und Passwörter für Konten zurücksetzen, die sie verwalten dürfen.

### Redaktionsverwaltung
- Administrations-Dashboard mit Kennzahlen zu Artikeln, Benutzerkonten, Kategorien, ausstehenden Kommentaren und Chart.js Diagrammen.
- Artikelverwaltung mit Rich-Text-Editor, Kategoriefiltern und Anpinnfunktion für die Startseite.
- Automatisches Speichern von Entwürfen im Browser-localStorage alle 30 Sekunden zur Reduzierung von Datenverlusten.
- Moderationsbereich für Leserkommentare mit den Zuständen Ausstehend, Genehmigt und Abgelehnt.
- Profilverwaltung und Hochladen von Profilbildern über das Modul SafeImageStorage.

### Kategorien und Slug-Routing
- Automatische Erzeugung lesbarer Slugs aus vietnamesischen Bezeichnungen, beispielsweise `Công nghệ & AI` zu `cong-nghe-ai`.
- Konfliktbehandlung bei Duplikaten durch Anhängen numerischer Suffixe bei maximal 200 Zeichen Gesamtlänge.
- Weiterleitung älterer URLs nach dem Muster `/category?id=...` auf die neuen Slug-Endpunkte.
- Fremdschlüsselbeziehung zwischen Artikeln und Kategorien bleibt über CategoryId für relationale Integrität erhalten.
- Artikel-Detailseiten nutzen derzeit weiterhin `/detail?id={uuid}`.

### Berichte und Datenexport
- Datenexport in die Formate Excel über Apache POI, CSV und PDF über iText7.
- Zentrale Verwaltung von E-Mail-Abonnenten des Newsletters.

---

## Bildschirmfotos

### Leser-Oberfläche

#### Startseite im hellen und dunklen Design
| Helles Design | Dunkles Design |
|:---:|:---:|
| ![Startseite Light Mode](.github/images/home_light.png) | ![Startseite Dark Mode](.github/images/home_dark.png) |

#### Kategorie-Ansicht und Artikel-Details
| Kategorie über Slug | Artikel-Details und Social Sharing |
|:---:|:---:|
| ![Kategorie Tech](.github/images/category_tech.png) | ![Artikel-Details](.github/images/article_detail.png) |

#### Lesezeichen und Leserkommentare
| Offcanvas Seitenleiste Lesezeichen | Kommentarbereich unter Artikeln |
|:---:|:---:|
| ![Lesezeichen](.github/images/feature_bookmarks_drawer.png) | ![Leserkommentare](.github/images/feature_comments_public.png) |

### Administrationsbereich

#### Anmeldeseite und Dashboard
| Anmeldeseite des Systems | Redaktions-Dashboard |
|:---:|:---:|
| ![Anmeldeseite](.github/images/login_page.png) | ![Admin Dashboard](.github/images/admin_dashboard.png) |

#### Artikel- und Kategorieverwaltung
| Artikelverwaltung und Editor | Kategorien und Slugs |
|:---:|:---:|
| ![Artikelverwaltung](.github/images/admin_news.png) | ![Kategorieverwaltung](.github/images/admin_categories.png) |

#### Benutzerverwaltung und Kommentarmoderation
| Benutzerkonten und Rollen | Kommentarmoderation |
|:---:|:---:|
| ![Benutzerverwaltung](.github/images/admin_users.png) | ![Kommentarmoderation](.github/images/admin_comments.png) |

#### Administratorprofil
| Profileinstellungen und Passwortänderung |
|:---:|
| ![Admin Profile](.github/images/admin_profile.png) |

---

## Systemarchitektur

```
[ Webbrowser Client ]
           │
           ▼
[ Servlet Filter: EncodingFilter, AuthFilter, CsrfFilter ]
           │
           ▼
[ Controller und Servlet Schicht ]
           │
           ▼
[ Service Schicht: Geschäftslogik und Validierung ]
           │
           ▼
[ Data Access Object - DAO ]
           │
           ▼
[ JDBCHelper und Connection-Pool HikariCP ]
           │
           ▼
[ Microsoft SQL Server 2022 ]
```

Das Projekt folgt einer Schichtenarchitektur nach dem MVC-Muster mit einer Service-Schicht für zentrale Geschäftsregeln; einige einfache Lese- und Abfrageoperationen greifen noch direkt auf DAO-Komponenten zu:
- Controller Schicht: Verarbeitet HTTP-Anfragen, prüft Parameter, verwaltet Sitzungszustände und leitet Daten an JSP-Ansichten weiter.
- Service Schicht: Behandelt Kernlogiken wie Authentifizierung, OTP-Prüfung, Ausstellung und Rotation von Remember-Me-Tokens, Slug-Generierung sowie Prüfung von Inhaberberechtigungen.
- DAO Schicht: Kapselt SQL-Abfragen und greift über PreparedStatement auf die Datenbank zu, um SQL-Injection-Risiken zu minimieren.
- Datenzugriff: JDBC mit HikariCP ist der primäre Datenzugriffsmechanismus. Hibernate ORM dient als unterstützende Komponente für Entitätsdefinitionen und Schema-Prüfungen.

---

## Sicherheitsmechanismen

Die Schutzmaßnahmen im Projekt orientieren sich an grundlegenden defensiven Prinzipien:
- Passwort-Hashing: Benutzerpasswörter werden vor der Speicherung in der Datenbank mit dem Einweg-Algorithmus BCrypt gehasht.
- Passwortänderung: Das aktuelle Passwort wird vor der Aktualisierung überprüft; neue Passwörter werden als BCrypt-Hash gespeichert.
- Sichere Remember-Me Tokens: Zufällige 256-Bit-Tokens werden über SecureRandom erzeugt. Die Datenbank speichert ausschließlich den SHA-256 Hash. Browser-Cookies nutzen die Flags HttpOnly und SameSite=Lax. Nach jeder erfolgreichen automatischen Anmeldung wird eine Token-Rotation durchgeführt, um das Risiko von Replay-Angriffen zu verringern.
- E-Mail-OTP: Sechsstellige OTP-Codes sind 5 Minuten lang gültig. Der Vergleich erfolgt über den zeitkonstanten Vergleichsalgorithmus MessageDigest.isEqual zur Vermeidung von Timing-Angriffen, gekoppelt mit einer Begrenzung auf maximal 3 Fehlversuche.
- Rollenbasierte Autorisierung: Der Super-Admin-Status wird über das Feld IsSuperAdmin in der Datenbank ermittelt. Reguläre Administratoren können andere Administratorkonten weder bearbeiten, sperren noch löschen.
- CSRF-Schutz bei POST-Anfragen: Zustandsverändernde Aktionen im Verwaltungsbereich werden durch CsrfFilter geschützt, welcher Tokens aus dem Formularfeld `_csrf` oder dem Header `X-CSRF-TOKEN` prüft.
- HTML-Bereinigung: Die Jsoup-Bibliothek filtert Artikelinhalte und öffentliche Kommentare anhand einer Whitelist, bevor Inhalte gespeichert oder ausgegeben werden.
- Datei-Validierung beim Upload: SafeImageStorage prüft Dateiendungen sowie MIME-Typen und vergibt zufällige UUID-Dateinamen vor der Ablage in partitionierten Verzeichnissen.
- Server-Protokollierung: Verwendet java.util.logging.Logger zur internen Erfassung von Ausnahmen auf dem Server.

---

## Kategorie-Slug und URL-Routing

Das System bietet lesefreundliche Kategorie-Pfade für Besucher:
- Slug-Generierung: Vietnamesische Titel mit Akzenten werden in kleingeschriebene Bindestrich-Zeichenketten umgewandelt, beispielsweise `Công nghệ & AI` zu `cong-nghe-ai`, erreichbar unter `/category/cong-nghe-ai`.
- Zeichen-Normalisierung: Tonzeichen werden entfernt, Buchstaben đ und Đ werden zu d normalisiert und Sonderzeichen durch Bindestriche ersetzt.
- Längenbegrenzung: Der vollständige Slug überschreitet auch bei numerischen Suffixen zur Duplikatauflösung niemals die Grenze von 200 Zeichen.
- Rückwärtskompatibilität: Alte Links nach dem Schema `/category?id=TECH` werden automatisch auf die entsprechende Slug-URL weitergeleitet.
- Datenbank-Beziehung: Artikel und Kategorien bleiben für relationale Integrität über den Fremdschlüssel CategoryId verknüpft.
- Artikel-URLs: Detailansichten von Artikeln nutzen aktuell weiterhin `/detail?id={uuid}`, da Slugs für Artikel noch nicht umgesetzt sind.

---

## Datenbank

- Datenbankverwaltungssystem: Microsoft SQL Server 2022
- Verbindungsmechanismus: JDBC mit HikariCP Connection-Pool
- Haupttabellen im System:
  - `Users`: Benutzerkonten, Rollen, Aktivierungsstatus und IsSuperAdmin-Flag.
  - `Categories`: Themenkategorien mit Id, Name und eindeutigem Slug.
  - `News`: Artikeldaten, Titel, Zusammenfassung, Autor, Aufrufe und Startseitenstatus.
  - `Comments`: Leserkommentare mit Moderationsstatus Ausstehend, Genehmigt oder Abgelehnt.
  - `OtpTokens`: OTP-Codes, Gültigkeit, Zähler für Fehlversuche und Nutzungsstatus.
  - `RememberTokens`: SHA-256 Hashes der dauerhaften Anmeldetokens und Ablaufdaten.
  - `ActivityLogs`: Systemprotokoll für wichtige redaktionelle Vorgänge.
  - `Newsletters`: E-Mail-Abonnements für Benachrichtigungen.
- Datenbank-Skripte:
  - `schema/ABCNews.sql`: Datenbankerstellung und Tabellendefinitionen.
  - `schema/seed_data.sql`: Initiale Beispieldaten mit nativer UTF-8 Unterstützung.
  - `schema/migrations/001_security_refactor.sql`: Migration für IsSuperAdmin und RememberTokens Tabelle.
  - `schema/migrations/002_category_slug.sql`: Migration für die Spalte Slug und eindeutigen Index.

---

## Konfiguration

Das System lädt Konfigurationswerte über ConfigHelper in folgender Prioritätsreihenfolge:
1. Umgebungsvariablen des Betriebssystems
2. System-Properties
3. Konfigurationsdatei `src/main/resources/app.properties`
4. Standardwerte für die lokale Entwicklungsumgebung

Beispielkonfiguration aus `src/main/resources/app.properties.example`:

```properties
# Microsoft SQL Server Datenbank
db.host=localhost
db.port=1433
db.name=ABCNews
db.user=sa
db.password=your_database_password_here

# Gmail SMTP für OTP und Newsletter
mail.smtp.host=smtp.gmail.com
mail.smtp.port=587
mail.smtp.auth=true
mail.smtp.starttls.enable=true
mail.smtp.user=your_email@gmail.com
mail.smtp.password=your_gmail_app_password_here

# Google Identity Services
google.client.id=your_google_client_id_here

# Basis-URL der Anwendung für kanonische Links
app.base.url=http://localhost:8088
```

---

## Installation und Start

### Start mit Docker Compose

Voraussetzung: Docker Desktop ist installiert.

```bash
# 1. Repository klonen
git clone https://github.com/nkhanhduy/ABCNews.git
cd ABCNews

# 2. Umgebungsdatei aus der Vorlage erstellen (.env.example)
cp .env.example .env

# 3. Gesamtsystem aus Tomcat und SQL Server 2022 starten
docker compose up --build -d

# 4. Status der Container prüfen
docker compose ps

# 5. Im Webbrowser aufrufen:
# - Leserseite: http://localhost:8088/home
# - Anmeldung:  http://localhost:8088/login
```

Dienste beenden und entfernen:
```bash
docker compose down
```

### Lokale Entwicklung

Voraussetzungen: JDK 17, Maven 3.8 oder höher, Microsoft SQL Server 2019 oder höher und Apache Tomcat 10.1.

Schritte zur Einrichtung:
1. Datenbank erstellen und Beispieldaten in SQL Server importieren:
   - Neuinstallation: Skripte nacheinander ausführen: `schema/ABCNews.sql` (Schema und Tabellen) und anschließend `schema/seed_data.sql` (Demodaten).
   - *Hinweis: Das Verzeichnis `schema/migrations/` wird nur für inkrementelle Aktualisierungen älterer Datenbankversionen verwendet.*
2. Datei `src/main/resources/app.properties` anhand der Vorlage `app.properties.example` erstellen und Zugangsdaten eintragen.
3. Tests ausführen und das WAR-Paket bauen:
   ```bash
   mvn clean test
   mvn clean package -DskipTests
   ```
4. Die Datei `target/ABCNews.war` in das Verzeichnis `webapps` von Apache Tomcat 10.1 kopieren und den Server starten.

---

## Automatisierte Tests

Das Projekt setzt JUnit 5 und Mockito ein, um Geschäftsregeln und Sicherheitsabläufe abzusichern:
- Authentifizierung und dauerhafte Anmeldung: Tests für erfolgreiche Anmeldung, fehlerhafte Versuche, gesperrte Konten, Token-Ausstellung und Token-Rotation.
- E-Mail-OTP: Tests für korrekte Codes, falsche Eingaben, Überschreitung von Versuchsgrenzen, abgelaufene Tokens und Einmalverwendung.
- Autorisierung: Tests für Berechtigungsgrenzen von Reportern bei fremden Artikeln, Zugriffsbeschränkungen für reguläre Admins auf Super-Admin-Konten und Verbot des Selbstlöschens.
- CSRF-Filter: Tests für Token-Erzeugung bei GET, Ablehnung mit HTTP 403 bei fehlendem oder ungültigem Token bei POST, Prüfung über Formularparameter oder Header und Ausnahme für Google-Callbacks.
- Kategorien und Slugs: Tests für vietnamesische Zeichen-Normalisierung, Duplikatauflösung und Einhaltung der Maximallänge von 200 Zeichen.

Befehl zum Ausführen aller Tests:
```bash
mvn clean test
```

Bestätigtes Testergebnis auf dem aktuellen Quellcode: **97/97 tests passed** mit 0 Fehlern und 0 übersprungenen Tests.

---

## Continuous Integration

Das Projekt verfügt über einen automatisierten Continuous-Integration-Workflow mit GitHub Actions unter `.github/workflows/maven.yml`:
- Wird bei Push oder Pull Request auf dem Hauptzweig main ausgelöst.
- Bereitstellung einer Ubuntu-Umgebung mit Eclipse Temurin JDK 17 und Maven Dependency-Caching.
- Führt `mvn -B clean test --file pom.xml` aus, um sicherzustellen, dass neuer Code alle Tests besteht, bevor er integriert wird.

---

## Testkonten

In `schema/seed_data.sql` sind vordefinierte Testkonten für Evaluierungszwecke enthalten:

> [!NOTE]
> Diese Konten sind ausschließlich für lokale Demo- und Entwicklungsumgebungen bestimmt.

| Rolle | Anmelde-E-Mail | Standardpasswort | Berechtigungsumfang |
|---|---|:---:|---|
| Super Admin | `superadmin@abcnews.com` | `123456` | Voller Systemzugriff, Verwaltung anderer Administratoren und Rollenvergabe |
| Admin | `admin@abcnews.com` | `123456` | Artikelverwaltung, Themenkategorien, Kommentarmoderation und Reporterkonten |
| Reporter | `reporter1@abcnews.com` | `123456` | Verfassen und Verwalten eigener veröffentlichter Artikel |

Hinweis: Standardpasswörter werden beim ersten erfolgreichen Anmeldevorgang automatisch in BCrypt-Hashes konvertiert.

---

## Was ich praktisch angewendet habe

Durch die Entwicklung dieses persönlichen Lernprojekts habe ich folgende Kenntnisse vertieft:
- Praktische Erfahrung mit dem Request-Lifecycle von Jakarta Servlet und Filter.
- Strukturierung von Quellcode nach dem Layered-MVC-Muster mit einer dedizierten Service-Schicht für Geschäftslogik.
- Datenzugriff mit nativem JDBC und Verbindungsoptimierung über HikariCP auf Microsoft SQL Server.
- Umsetzung grundlegender Web-Sicherheitsmuster wie BCrypt Passwort-Hashing, Remember-Me Token-Rotation, zeitkonstanter OTP-Vergleich, CSRF-Filterung und Jsoup HTML-Bereinigung.
- Erstellung automatisierter Tests mit JUnit 5 und Mockito zur Absicherung zentraler Abläufe.
- Containerisierung der Entwicklungsumgebung mit Docker und Docker Compose.
- Einrichtung eines grundlegenden Continuous-Integration-Ablaufs mit GitHub Actions.

---

## Autor

- Name: Nguyen Duy Khanh
- Rolle: Student im Bereich Softwareentwicklung — FPT Polytechnic College
- GitHub: [github.com/nkhanhduy](https://github.com/nkhanhduy)
- E-Mail: [khanhndts02168@gmail.com](mailto:khanhndts02168@gmail.com)
- Zeitraum: 2025 – 2026
