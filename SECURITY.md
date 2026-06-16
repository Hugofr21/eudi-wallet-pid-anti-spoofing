# EU Digital Identity Wallet Vulnerability Disclosure Policy (VDP)

The European Commission considers the security of its Communication and Information Systems a top priority, in line with Commission Decision EC 2017/46. However, vulnerabilities can never be fully eliminated despite all preventive measures. If exploited, such vulnerabilities may affect the confidentiality, integrity, or availability of Commission systems and the information they process.

To identify and remediate vulnerabilities as early as possible, we value input from external security researchers acting in good faith and encourage responsible vulnerability disclosure. This document defines what is considered good faith in vulnerability research and reporting, as well as what reporters can expect in return.

---

## Scope

This policy applies to:

- The Architecture and Reference Framework (ARF)
- Source code hosted in the [eu-digital-identity-wallet](https://github.com/eu-digital-identity-wallet) public repositories

---

## Reporting a Vulnerability

If you have identified a vulnerability, please follow these steps:

- Send your findings by email to: <EC-VULNERABILITY-DISCLOSURE@ec.europa.eu>, indicating whether you consent to your name or pseudonym being publicly acknowledged as the discoverer of the issue.
- Encrypt your report using the official PGP key to ensure secure transmission of sensitive information: https://ec.europa.eu/assets/digit/pgpkey/ec-vulnerability-disclosure-pgp.txt
- Provide sufficient information to reproduce the issue. In most cases, this includes the affected URL or IP address and a clear description of the vulnerability. Complex issues may require additional technical details or proof-of-concept code.
- Submit your report in English, preferably, or in any official language of the European Union.
- Clearly state whether you consent to public attribution for the discovery.

---

## Out of Scope Behavior (Do Not Perform)

The following activities are strictly prohibited during security testing:

- Exploiting vulnerabilities beyond what is necessary to demonstrate their existence.
- Downloading, modifying, or deleting data that does not belong to you.
- Disclosing vulnerability details or affected data to third parties before remediation.
- Introducing malware (e.g., viruses, worms, trojans).
- Altering system configurations or user data.
- Repeated or excessive access to systems.
- Attempting to access other systems using obtained credentials or access.
- Modifying access rights of other users.
- Using automated scanning tools or brute-force techniques.
- Conducting denial-of-service or social engineering attacks (phishing, vishing, spam, etc.).
- Attempting to compromise physical security controls.

---

## Our Commitments

- We will acknowledge receipt of your report within three business days and provide an initial assessment.
- We will handle all reports with strict confidentiality.
- Where possible, we will inform you once the vulnerability has been resolved.
- Personal data provided (such as email address or name) will be processed in accordance with applicable data protection legislation and will not be shared with third parties without consent.
- If you have agreed to attribution, your name or pseudonym will be included in public disclosures related to the vulnerability.