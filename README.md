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

| Component | Algorithm | Mode | Rationale |
|-----------|-----------|------|----------|
| **Database / file‑level storage** | **AES‑256** | **GCM (Galois/Counter Mode)** | Provides confidentiality **and** authenticity (integrity) in a single primitive. GCM’s built‑in authentication tag protects against ciphertext tampering. |
| **Derived secret keys** | **PBKDF2‑HMAC‑SHA‑256** | – | A password‑based key‑derivation function that turns the user’s PIN (or pass‑phrase) into a strong 256‑bit key. A per‑wallet random salt and a high iteration count (≥ 200 000) make offline brute‑force attacks computationally expensive. |
| **Transient session keys** | **AES‑256‑GCM** | – | Used for encrypting data that is only needed for the lifetime of a presentation or a remote QES flow. Session keys are generated on‑the‑fly and destroyed after use. |

> **Note:** All encrypted payloads include the GCM authentication tag (16 bytes) and the IV (12 bytes) stored alongside the ciphertext. The IV is generated with a cryptographically secure random number generator.

---

### 2. Hardware‑Backed Key Management  

* **StrongBox (Android Keystore)** – When the device supports StrongBox, the wallet stores the **master encryption key** inside the hardware‑isolated Trusted Execution Environment (TEE). The key never leaves the TEE in clear text and can only be accessed after successful user authentication.  

* **Software fallback** – On devices lacking StrongBox, keys are stored in the regular Android Keystore, which is still hardware‑backed on most modern phones (Secure Element / TEE).  

* **Biometric‑protected key access** – The wallet registers a **BiometricPrompt** that gates the retrieval of the decryption key from the Keystore. The biometric check is combined with the user‑chosen PIN (see next section) to provide **two‑factor authentication (2FA)**.

---

### 3. Multi‑Factor Authentication (MFA)

| Factor | Implementation | Security contribution |
|--------|----------------|------------------------|
| **PIN** | User‑chosen numeric PIN (minimum 6 digits) is processed by PBKDF2 to derive an **encryption key**. | Provides something the user *knows*. PBKDF2 makes offline attacks costly. |
| **Biometrics** | Fingerprint / Face‑recognition via Android’s **BiometricPrompt**. The biometric credential is never exposed to the app; only a cryptographic token from the TEE is returned. | Provides something the user *is*. The token can be combined with the PIN‑derived key, ensuring that both are required to unlock the wallet. |
| **Device attestation** (optional) | SafetyNet / Play Integrity API checks that the device is genuine and unmodified. | Raises the bar against rooted or tampered devices attempting to extract secrets. |

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
