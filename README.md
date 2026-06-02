# EUDI Android Wallet Reference Application

⚠️ **Important!** Before you proceed, please read the
[EUDI Wallet Reference Implementation project description](https://github.com/eu-digital-identity-wallet/.github/blob/main/profile/reference-implementation.md).

---

## Table of Contents

- [EUDI Android Wallet Reference Application](#eudi-android-wallet-reference-application)
  - [Table of Contents](#table-of-contents)
  - [Overview](#overview)
  - [Protocols](#protocols)
  - [Security](#security)
    - [1. Encryption of Sensitive Data](#1-encryption-of-sensitive-data)
    - [2. Hardware‑Backed Key Management](#2-hardwarebacked-key-management)
    - [3. Multi‑Factor Authentication (MFA)](#3-multifactor-authentication-mfa)
    - [4. Anti‑Spoofing \& Identity‑Falsification Mitigation](#4-antispoofing--identityfalsification-mitigation)
    - [5. Credential Issuance \& Self‑Issued Credentials](#5-credential-issuance--selfissued-credentials)
    - [6. Brute‑Force and Cryptographic Attack Mitigations](#6-bruteforce-and-cryptographic-attack-mitigations)
  - [Visão Geral do Fluxo de Segurança](#visão-geral-do-fluxo-de-segurança)
- [Security Flow Overview](#security-flow-overview)
  - [License](#license)
    - [License details](#license-details)

## Overview

The **EUDI Wallet Reference Implementation** is built based on the
[Architecture Reference Framework](https://github.com/eu-digital-identity-wallet/eudi-doc-architecture-and-reference-framework/blob/main/docs/architecture-and-reference-framework-main.md)
and aims to showcase a robust and interoperable platform for digital
identification, authentication, and electronic signatures based on common
standards across the European Union.

The implementation follows a **modular architecture** composed of
business‑agnostic, reusable components that evolve incrementally and can be
re‑used across multiple projects.

The EUDI Wallet enables users to:

1. Obtain, store, and present PID and mDL.
2. Verify credential presentations.
3. Share data in proximity scenarios.
4. Support remote QES and other use cases via the bundled modules.
5. Perform anti‑spoofing checks, verify credentials, and manage holder
   configuration.

The **EUDIW** project provides this repository with an Android app.
Please refer to the repositories listed in the subsequent sections for
more detailed information on how to get started, contribute, and engage with
the EUDI Wallet Reference Implementation.

## Protocols

- **OpenID Federation** for OpenID Connect 1.1
- **OpenID Connect** for Verifiable Credential Issuance
- **OpenID Connect** for Verifiable Presentations

## Security

The EUDI Android Wallet adopts a defense‑in‑depth approach that combines strong
cryptography, hardware‑backed key storage, and multiple layers of
authentication/verification.  The main pillars are described below.

### 1. Encryption of Sensitive Data

| Component                                | Algorithm                        | Mode                                | Rationale                                                                                                                                                                                                                                            |
| ---------------------------------------- | -------------------------------- | ----------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Database / file‑level storage** | **AES‑256**               | **GCM (Galois/Counter Mode)** | Provides confidentiality**and** authenticity (integrity) in a single primitive. GCM’s built‑in authentication tag protects against ciphertext tampering.                                                                                     |
| **Derived secret keys**            | **PBKDF2‑HMAC‑SHA‑256** | –                                  | A password‑based key‑derivation function that turns the user’s PIN (or pass‑phrase) into a strong 256‑bit key. A per‑wallet random salt and a high iteration count (≥ 200 000) make offline brute‑force attacks computationally expensive. |
| **Transient session keys**         | **AES‑256‑GCM**          | –                                  | Used for encrypting data that is only needed for the lifetime of a presentation or a remote QES flow. Session keys are generated on‑the‑fly and destroyed after use.                                                                               |

> **Note:** All encrypted payloads include the GCM authentication tag (16 bytes) and the IV (12 bytes) stored alongside the ciphertext. The IV is generated with a cryptographically secure random number generator.

---

### 2. Hardware‑Backed Key Management

* **StrongBox (Android Keystore)** – When the device supports StrongBox, the wallet stores the **master encryption key** inside the hardware‑isolated Trusted Execution Environment (TEE). The key never leaves the TEE in clear text and can only be accessed after successful user authentication.
* **Software fallback** – On devices lacking StrongBox, keys are stored in the regular Android Keystore, which is still hardware‑backed on most modern phones (Secure Element / TEE).
* **Biometric‑protected key access** – The wallet registers a **BiometricPrompt** that gates the retrieval of the decryption key from the Keystore. The biometric check is combined with the user‑chosen PIN (see next section) to provide **two‑factor authentication (2FA)**.

---

### 3. Multi‑Factor Authentication (MFA)

| Factor                                  | Implementation                                                                                                                                                                      | Security contribution                                                                                                                         |
| --------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| **PIN**                           | User‑chosen numeric PIN (minimum 6 digits) is processed by PBKDF2 to derive an**encryption key**.                                                                            | Provides something the user*knows*. PBKDF2 makes offline attacks costly.                                                                    |
| **Biometrics**                    | Fingerprint / Face‑recognition via Android’s**BiometricPrompt**. The biometric credential is never exposed to the app; only a cryptographic token from the TEE is returned. | Provides something the user*is*. The token can be combined with the PIN‑derived key, ensuring that both are required to unlock the wallet. |
| **Device attestation** (optional) | SafetyNet / Play Integrity API checks that the device is genuine and unmodified.                                                                                                    | Raises the bar against rooted or tampered devices attempting to extract secrets.                                                              |

If any factor fails, the wallet enforces **exponential back‑off** and **temporary lock‑out** (e.g., 30 s → 1 min → 5 min → permanent wipe after configurable attempts).

---

### 4. Anti‑Spoofing & Identity‑Falsification Mitigation

* **Liveness detection** – The biometric APIs perform built‑in liveness checks (e.g., challenge‑response for face or anti‑finger‑lift for fingerprints).
* **Secure element attestation** – The StrongBox/Tee provides attestation certificates that prove the key material resides in a trusted environment, making it harder for malware to spoof a legitimate credential.
* **Presentation binding** – Verifiable Credentials are signed by trusted issuers (X.509 or JSON‑Web‑Signature) and are **cryptographically bound** to the holder’s private key stored in the Keystore. Any tampering results in a verification failure on the verifier side.
* **Replay protection** – Each presentation includes a **nonce** (or timestamp) issued by the verifier, ensuring that captured presentations cannot be replayed.

---

### 5. Credential Issuance & Self‑Issued Credentials

* **Issuer‑signed credentials** – The wallet can receive credentials from external trusted issuers via the OIDC‑VC flow. All received VCs are validated (signature chain, revocation status, expiration) before being stored.
* **Self‑issued credentials** – The wallet supports the **Self‑Issued OpenID Provider** pattern, enabling users to create **personal identifiers** (e.g., a “wallet address” or “self‑asserted PID”). These credentials are **self‑signed** with the wallet’s private key (hardware‑protected) and can be presented to services that accept self‑issued VCs, subject to appropriate policy checks on the verifier side.

All issued credentials are stored encrypted (AES‑GCM) and are only accessible after successful MFA.

---

### 6. Brute‑Force and Cryptographic Attack Mitigations

* **High‑entropy salts & iteration count** for PBKDF2 make offline dictionary attacks infeasible.
* **Rate limiting** on PIN entry (exponential back‑off, temporary lock‑out).
* **Hardware rate‑limiting** – StrongBox and the TEE enforce a limit on the number of key usage attempts per second, preventing rapid trial of derived keys.
* **Constant‑time comparisons** for authentication tokens to avoid timing attacks.
* **Secure random number generation** (via `SecureRandom`/`KeyGenParameterSpec`) for all IVs, salts, and nonces.

---

## Visão Geral do Fluxo de Segurança

| Etapa          | Abreviação                               | O que acontece                                                                                                                              | Ferramentas recomendadas                                                                                                                           |
| -------------- | ------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------- |
| **SADT** | Structured Analysis & Design Technique     | Modelagem de requisitos, diagramas de fluxo e especificação de controles de segurança (confidencialidade, integridade, disponibilidade). | **OpenSSF Scorecard** – validação de boas práticas de repositório; **Threat‑Model** (Microsoft SDL, OWASP‑ASVS).              |
| **SCAS** | Software Composition & Architecture Scan   | Identificação de dependências de terceiros, licenças, vulnerabilidades conhecidas e políticas de vetorização de risco.               | **OWASP Dependency‑Check**, **GitHub Dependabot**, **OpenSSF Scorecard** (e.g., `security-updates`, `branch-protection`). |
| **SAST** | Static Application Security Testing        | Análise de código fonte em busca de bugs, anti‑patterns e vulnerabilidades lógicas.                                                     | **SonarQube** (regra Kotlin), **CodeQL** (`kotlin` queries).                                                                         |
| **DAST** | Dynamic Application Security Testing       | Varredura da aplicação em execução (API, UI) para descobrir falhas de injeção, XSS, autenticação, autorização etc.                | **OWASP ZAP**, **Burp Suite Community**, **Gauntlt**.                                                                         |
| **SALS** | Secure Application Lifecycle & Stewardship | Pós‑deploy: monitoramento, gerenciamento de incidentes, auditoria de logs e renovação de dependências.                                 | **OpenTelemetry**, **Elastic SIEM**, **Prometheus Alertmanager**, **O**                                                  |

# Security Flow Overview

A concise, end‑to‑end picture of how security is woven into the software lifecycle.
Each stage supplies a **gate** that must be passed before the next one can start, and a set of **recommended tools** that are proven in the industry.

| Stage                                                | Abbreviation   | What Happens (main activity)                                                                                                                                                                                                                                                                                                                                                                              | Recommended Tools                                                                                                                                                                                                                                                                                                                                                                                                     |
| ---------------------------------------------------- | -------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Structured Analysis & Design Technique**     | **SADT** | • Model functional and non‑functional requirements.``• Produce data‑flow diagrams (DFD), sequence diagrams, and component diagrams.``• Define security controls (confidentiality, integrity, availability) and governance policies.``• Build a Threat Model (e.g., STRIDE, PASTA).                                                                                                                  | **OpenSSF Scorecard** – checks for good‑repo practices (branch protection, signed commits, etc.).``Threat‑Modeling – Microsoft SDL, OWASP‑ASVS, Microsoft Threat Modeling Tool, OWASP Threat Dragon, PlantUML for diagramming.                                                                                                                                                                           |
| **Software Composition & Architecture Scan**   | **SCAS** | • Create an inventory of every third‑party component (libraries, containers, OS images).``• Analyse licenses and compliance (MIT, GPL, Apache, etc.).``• Scan public vulnerability databases (CVE) for those components and base images.``• Evaluate architectural risk (critical components, single points of failure).                                                                             | **OWASP Dependency‑Check** (Gradle/Maven plugin).``GitHub Dependabot – automatic pull‑requests for updates.``Snyk, **WhiteSource**, **OSS Index** – complementary SCA services.``OpenSSF Scorecard – additional checks such as `security-updates` and `maintained`.                                                                                                                      |
| **Static Application Security Testing**        | **SAST** | • Perform static analysis of source code to locate bugs, anti‑patterns, and logical vulnerabilities.``• Enforce secure‑coding rules (e.g., OWASP ASVS Level 2).``• Generate **Quality Gates** that block merges when thresholds are not met.                                                                                                                                                | **SonarQube** – Kotlin‑specific rules (Detekt, Kotlin‑Lint, code smells).``CodeQL – custom Kotlin queries (SQL injection, path traversal, insecure deserialization).``SpotBugs, **Detekt**, **Checkmarx**, **Fortify** (optional/enterprise).                                                                                                                                             |
| **Dynamic Application Security Testing**       | **DAST** | • Scan the**running** application (API, UI, WebSocket).``• Run automated penetration tests: code injection, XSS, CSRF, broken authentication, broken access control, etc.``• Perform brute‑force, fuzzing of parameters and file uploads.                                                                                                                                                       | **OWASP ZAP** – baseline scan + active scan.``Burp Suite Community – intruder & scanner modules.``Gauntlt – security tests expressed as BDD scenarios.``Nikto, **Wapiti**, **Arachni** – additional scanners for completeness.                                                                                                                                                               |
| **Secure Application Lifecycle & Stewardship** | **SALS** | • Continuously monitor security‑related metrics (latency, error rates, anomalies).``• Collect distributed telemetry and trace critical transactions.``• Centralise **immutable** logs, correlate events, and emit incident alerts.``• Automate vulnerable‑dependency upgrades and rotate secrets/keys.``• Operate a formal Incident‑Response Plan (IRP) and maintain forensic audit trails. | **OpenTelemetry** – instrumentation + Collector.``Prometheus+**Alertmanager** – metric collection & alerting.``Grafana – dashboards for real‑time visibility.``Elastic SIEM – write‑once logs, correlation, security dashboards.``Loki or **Fluentd** – log aggregation.``HashiCorp Vault – automatic token/secret rotation.``Argo Rollouts or **Flux** – safe, automated image upgrades. |

## License

### License details

Copyright (c) 2026 European Commission

Licensed under the European Union Public Licence (EUPL) version 1.2 or,
as soon as it is approved by the European Commission, any subsequent
version of the EUPL (the “Licence”). You may not use this work except in
compliance with the Licence.

You may obtain a copy of the Licence at:
https://joinup.ec.europa.eu/software/page/eupl

Unless required by applicable law or agreed to in writing, software
distributed under the Licence is distributed on an “AS IS” basis,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the Licence for the specific language governing permissions and
limitations under the Licence.
