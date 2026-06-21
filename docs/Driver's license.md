title: Driver's license
version: v1.0.0-draft
status: draft
editor: @hugofr21

# Driver's License

The driver's license constitutes a structured personal identifier, whose attributes are categorized and numbered from 1 to 9. The fundamental information is divided into the primary identification block (points 1 to 3: identity and date of birth), institutional issuance data (points 4 to 8), and the vehicle categories for which the holder is licensed (point 9).

In the context of the digital transition and the creation of a Verifiable Credential, the data extracted from this physical document is mapped in strict compliance with the normative namespace `org.iso.18013.5.1.mDL`.

For the execution of this project, a continuous video frame reading technique was implemented, utilizing the ML Kit (Android) library for Optical Character Recognition (OCR). The architecture is based on a set of deterministic rules integrated into a processing pipeline, which uses the Levenshtein Distance metric to determine the most accurate capture hypothesis from a temporal history of frames.

## Challenges and Technological Approach

Considering the requirement for rigorous data validation and the need for autonomous OCR error correction, the system was designed to operate automatically, eliminating any manual interference or insertion by the holder.

During the optical capture process, the system frequently encounters physical and environmental anomalies, such as blurring, partial occlusions, and specular reflections (typical of laminated identification documents). Consequently, the implementation of a robust validation and heuristic correction subsystem is imperative to ensure the accuracy and reliability of the extracted data.

### Image Processing and Spatial Delimitation

The flow begins with the precise extraction of the Region of Interest (ROI). Subsequent image processing stages include normalization, binarization, and visual noise immunization. The exact cropping of the ROI is fundamental, as it drastically reduces the computational load and focuses the analytical processing capacity exclusively on the document areas containing useful data.

### Temporal Analysis and Multiple Hypotheses

Modern OCR engines, such as ML Kit, can generate multiple reading hypotheses for the same field, especially under suboptimal capture conditions. Building a temporal history of these hypotheses allows the application of string comparison algorithms, such as the Levenshtein Distance, to identify the most consistent reading over time.

This statistical approach significantly increases extraction accuracy, mitigating the effects of transient errors caused by camera movement or lighting variations. The parallel application of field-specific heuristic rules—such as strict validation of date formats or check digit verification—reinforces the guarantee that only semantically valid information is injected into the verifiable credential.

### Strict Filtering and Heuristic Correction

The pipeline acts primarily through a pre-validation filter using Regular Expressions (Regex). This filter immediately discards OCR-generated hypotheses that violate the expected format. For example, in a date of birth field, only captures compatible with standardized formats (e.g., `DDMMYYYY` or `YYYYMMDD`) advance to the Levenshtein Distance calculation. This prior screening optimizes the system by reducing unnecessary computations and focusing the similarity analysis on structurally valid candidates.

After passing through the Regex and Levenshtein validators, the system enters the final phase of heuristic substitution. If the best-selected hypothesis presents a minimal discrepancy compared to the expected value (low Levenshtein distance) but contains logical errors, the system iteratively attempts to replace characters classically confused by the OCR (such as the letter "O" with the digit "0", or the letter "I" with the digit "1"). The system then recalculates the structural validity of the new hypothesis. This iterative cycle of correction and validation ensures the resilience of the extraction engine against the degradation of the captured image quality.

## Data Extraction Pipeline

The processing architecture can be summarized in the following operational sequence:

1. **Spatial Delimitation:** Extraction and cropping of the Region of Interest (ROI).
2. **Optical Treatment:** Application of normalization, noise immunization, and image binarization.
3. **Neural Inference:** Execution of pattern recognition and primary text extraction (OCR).
4. **State Storage:** In-memory recording of the temporal history of reading hypotheses.
5. **Filtering and Cleansing:** Application of Regular Expressions (Regex) for format validation and noise elimination.
6. **Similarity Calculation:** Application of the Levenshtein Distance algorithm to determine the most consistent hypothesis over time.
7. **Automatic Correction:** Application of heuristic substitution rules for ambiguous characters.