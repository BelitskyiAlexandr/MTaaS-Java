# Research Artifacts

This directory contains supplementary research-related materials associated with the MTaaS Java prototype.

## Metrics Evaluation

During the experimental evaluation of the MTaaS, several object-oriented software metrics were collected and analyzed in order to compare the annotation-based MTaaS approach with a manual metamorphic testing implementation.

The evaluation included metrics such as:

- LOC (Lines of Code)
- WMC (Weighted Methods per Class)
- CBO (Coupling Between Objects)
- RFC (Response For Class)
- LCOM (Lack of Cohesion of Methods)
- NOM (Number of Methods)

## Tooling

The metrics were collected using the CK static analysis tool for Java:

- CK: https://github.com/mauricioaniche/ck

The Maven configuration required to reproduce the measurements is preserved in the project configuration files.

## About CSV Files

Raw metric output files are intentionally excluded from version control.

The generated CSV files contain environment-specific absolute paths.
Because of this, only the metric collection configuration is included in the repository, while the generated outputs themselves are omitted.