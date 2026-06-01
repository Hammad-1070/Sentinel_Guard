# 🛡️ Sentinel Guard
**Military-Grade Encrypted Desktop Vault**

Sentinel Guard is a secure, standalone desktop application engineered to vault classified text documents. It features a multi-layered security architecture, including AES-256 envelope encryption, BCrypt cryptographic hashing, and a custom asynchronous UI designed to prevent unauthorized session access.

**Lead Architect:** Muhammad Hammad Saleem  
**Version:** 1 (Production Build)

---

## 🏗️ Core Architecture & Tech Stack

| Component | Technology Used |
| :--- | :--- |
| **Frontend GUI** | Java Swing (AWT / Event Dispatch Thread) |
| **Backend Engine** | Java (JDK 8+) |
| **Database** | MySQL Relational Database |
| **Cryptography** | `jBCrypt` (Blowfish), `javax.crypto` (AES-256) |
| **Build & Deployment** | Apache Maven, Launch4j (Automated `.exe` generation) |

---

## 🔒 Security Protocols

Sentinel Guard operates on a "Zero Trust" model, ensuring that a compromised database does not result in compromised vault contents.

* **Layer 1: BCrypt Hashing:** User passwords are never stored in plaintext. They are salted and hashed using the BCrypt algorithm, neutralizing dictionary and rainbow-table attacks.
* **Layer 2: Dual-Check Security Verification:** Account recovery utilizes a custom security question. The answer is hashed, and the engine utilizes a custom Dual-Check protocol to account for case-sensitivity edge cases without compromising cryptographic integrity.
* **Layer 3: Envelope Encryption:** Each user is assigned a unique, dynamically generated `Personal Master Key`. This key is then encrypted using a system-wide AES-256 `SYSTEM_KEY` stored securely in a local `.env` file before being committed to the database.
* **Layer 4: Idle Auto-Kill Timer:** A global `AWTEventListener` tripwire monitors system-wide I/O events (mouse/keyboard). If a 5-minute idle threshold is breached, the engine violently terminates the session and purges active memory.

---

## 💻 Asynchronous GUI Features

The user interface was built to mirror a high-security terminal, utilizing custom Java threading to prevent the Event Dispatch Thread (EDT) from freezing during complex operations.

* **Matrix Typewriter Engine:** Utilizes a `javax.swing.Timer` to asynchronously print classified vault documents letter-by-letter (15ms delay) without locking UI interactions.
* **Airlock Boot Sequence:** A dedicated loading interface that simulates cryptographic handshake sequences before granting vault access.
* **Dynamic Component Routing:** Employs a custom `switchPanel` architecture to cleanly destroy and instantiate UI memory blocks when moving between the Login, Vault, and Settings rooms.

---

## 🚀 Deployment Pipeline

Sentinel Guard features a fully automated build pipeline. The application does not require manual file copying for deployment.

1.  **Clean the Factory:** Run `mvn clean` to purge the old target cache.
2.  **Execute the Pipeline:** Run `mvn package`.
3.  **Automated Wrapping:** The `launch4j-maven-plugin` intercepts the Maven build phase, absorbs the "Fat JAR" (including all SQL and BCrypt dependencies), attaches the system `.ico` logo, and compiles a native Windows `SentinelGuard.exe`.

---

## ⚙️ Setup & Installation (Developer Mode)

1. Ensure MySQL Server is running locally on port `3306`.
2. Execute the provided `database_setup.sql` script to structure the `users` and `notes` tables.
3. Place your secure `.env` file containing the `SYSTEM_KEY` in the root deployment directory.
4. Run the executable to initiate the Sentinel protocol.