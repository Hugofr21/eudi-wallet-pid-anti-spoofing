# Prototype Development

## Context and Rationale

Within the context of the European Digital Identity Wallet (EUDI Wallet), architectural design must be grounded in rigorous empirical analysis and critical evaluation of existing protocols. As core regulatory and technical frameworks, including the Architecture and Reference Framework (ARF) and eIDAS 2.0, continue to evolve, integration challenges and potential vulnerability vectors remain. These factors require the implementation of robust security mechanisms capable of mitigating threats such as malware, credential theft, and extraction attacks.

Should future revisions of the ARF introduce systemic traceability requirements or tracking-by-default mechanisms, a significant risk of non-compliance with the General Data Protection Regulation (GDPR) and the European Union's digital sovereignty principles may emerge. Consequently, Privacy by Design constitutes a non-negotiable architectural principle.

Development is governed by Regulation (EU) 2024/1183 (eIDAS 2.0), specifically Articles 5A, 5B, and 5C, the Implementing Regulations (EU) 2024/2979 and 2024/2977, the Architecture and Reference Framework (ARF), and recommendations issued by the European Wallet Consortium (EWC). As a fundamental pillar of transparency, auditability, and public trust, the client application source code shall be developed and maintained under an Open Source license.

## 1. Core Capabilities

This domain describes the wallet's native functional modules and defines the user's interaction capabilities within the digital identity ecosystem.

### eIDAS Identity Management and Authentication

Login and authentication for online and offline services (Relying Parties - RPs) shall operate through national electronic identification schemes (eIDAS), supporting federated authentication through approved identity providers in accordance with ARF Section 2.2.

### Verifiable Credential Lifecycle Management

Full support shall be provided for credential issuance, storage, and presentation (verification) workflows.

Compatibility shall be maintained with the following credential formats:

* SD-JWT
* mDoc (ISO/IEC 18013-5)
* JWT-based credentials
* JSON-LD-based credentials

### Proximity Connectivity and Presentation Mechanisms

Support shall be provided for:

* Bluetooth Low Energy (BLE)
* Near Field Communication (NFC)
* QR Code interactions

These technologies shall be used to establish secure device engagement channels between holders and verifiers.

### Qualified Electronic Signatures (QES)

Native integration with Qualified Trust Service Providers (QTSPs) shall be supported.

The wallet shall be capable of calculating cryptographic document hashes (SHA-256) locally and generating the metadata required to request legally binding electronic signatures.

### Self-Issued Identity Attributes

Users may generate and store self-declared attributes (e.g., instant photographs), provided that local signatures are generated and protected by the device security subsystem (WSC Device / WUA) and meet the High Level of Assurance (High LoA) requirements.

### Account Audit and Activity History

The wallet shall record and display transaction histories and data access events through audit logs structured according to eIDAS 2.0 legal requirements.

---

## 2. Functional Requirements

### FR01 – Initial eID Provisioning

The national electronic identity credential (eID) shall be the first credential provisioned and installed through the EAA or eIDAS Connector process, ensuring immediate binding of the minimum civil identity dataset to the physical device during wallet activation.

### FR02 – Selective Disclosure Control

The system shall provide granular control over which attributes from the Personal Identification Data (PID) and other attestations are disclosed to a Relying Party.

Where legally permissible, pseudonymization mechanisms shall be supported to minimize nominal identification requirements.

### FR03 – Proximity Data Sharing

The application shall support offline credential exchange through secure and encrypted communication channels.

The workflow shall begin through QR Code presentation or NFC tap-based pairing, generating an ephemeral symmetric key, followed by data transfer through BLE, Wi-Fi Aware, or NFC.

### FR04 – Consent Management and Revocation

The user interface shall provide mechanisms allowing users to:

* Review previous interactions
* Request deletion of processed personal data
* Request termination of tracking activities by Relying Parties

These capabilities shall comply with Article 17 of the GDPR.

The wallet shall additionally provide reporting mechanisms for fraudulent entities.

### FR05 – Identity Portability and Recovery

Users shall be able to transfer their Personal Identification Data (PID) to another device under their control and recover identity access in cases of local authentication failure, device loss, or device replacement.

Recovery mechanisms shall rely on secure recovery procedures and Digital Identity Data Infrastructure (DDI) capabilities.

---

## 3. Non-Functional Requirements

### 3.1 Security, Cryptography, and Access Control

#### NFR01 – Authenticator Protection (PIN)

The wallet shall enforce an alphanumeric PIN with controlled entropy requirements.

Minimum requirements include:

* 6 to 8 characters minimum
* Rejection of common sequences and predictable patterns
* Protection against brute-force attacks

Access failure tolerance shall be strictly limited, requiring lockout or advanced recovery procedures after 3 to 5 consecutive failed attempts.

Storage of the PIN in plaintext or unprotected hash form within the operating system file system is strictly prohibited.

PIN material shall be protected through secure hardware-backed environments such as TEE or StrongBox.

#### NFR02 – Strong Customer Authentication (SCA)

Wallet unlocking and credential presentation signing shall require Strong Customer Authentication (multi-factor authentication combining PIN and biometrics), complying with the High Level of Assurance requirements defined in Article 5A(11).

Support for FIDO2 and WebAuthn standards shall be incorporated.

#### NFR03 – Privacy by Design

The architecture shall prohibit secondary use of personal data and the creation of user behavioral profiles.

Data disclosures shall be limited to the minimum required information and shall only occur following explicit and granular user consent under a Consent-First model.

### 3.2 Regulatory Compliance and Interoperability Standards

#### NFR04 – Identity Protocol Support

The platform shall support:

* OpenID4VP
* OpenID4VCI
* OIDC4VC
* DIDComm

for secure identity exchange and messaging.

#### NFR05 – EU Trust Mark

The user interface shall continuously display the official EU Trust Mark, indicating wallet certification and regulatory compliance.

#### NFR06 – National Extensibility

The architecture shall remain modular and support country-specific extensions (e.g., Portugal's Chave Móvel Digital), provided such extensions do not violate voluntariness principles under Article 5A(15) or conflict with European privacy requirements.

### 3.3 Usability and User Experience

#### NFR07 – Inclusive Accessibility

Application design and user interfaces shall comply with Directive (EU) 2019/882 (European Accessibility Act) and applicable WCAG requirements for mobile platforms.

#### NFR08 – Voluntary Usage Principle

The architecture shall not contain technical mechanisms that prevent citizens from accessing public services if they choose not to use the EUDI Wallet.

The system shall prevent any form of digital discrimination resulting from non-adoption.

---

## 4. Technology Stack

Engineering decisions are based on a native mobile development strategy.

### Programming Language

**Kotlin** serves as the primary development language.

Officially adopted by Google as a first-class Android language in 2017, Kotlin provides:

* Concise and expressive syntax
* Structural null safety
* Improved maintainability
* Native coroutine support

Coroutines are fundamental for scalable concurrency, enabling asynchronous execution of cryptographic operations, network communication, and database access without blocking the user interface.

### Integrated Development Environment (IDE)

**Android Studio** serves as the primary development environment.

The platform provides:

* Deep Kotlin compiler integration
* Advanced debugging capabilities
* CPU and memory profiling tools
* Static code analysis (Lint)
* XML and Jetpack Compose UI tooling
* Gradle dependency management
* Automated CI/CD workflow integration
* Comprehensive testing and deployment support

## References

1. EU Digital Identity Wallet – Architecture and Reference Framework Issues Repository.
2. Kotlin Coroutines on Android – Android Developers Documentation.
