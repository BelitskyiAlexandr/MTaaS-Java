# Research Artifacts

This directory contains supplementary research-related materials associated with the MTaaS Java prototype.

These materials are not required to build or run the project. They are provided only to describe the research context, architecture, and experimental evaluation of the prototype.

## Diagrams

The `diagrams/` directory contains architectural diagrams used to explain the proposed approach.

Recommended diagrams include:

- `generation-pipeline.png` — annotation processing and artifact generation workflow;
- `component-structure.png` — uml class diagram of the MTaaS.

## Metrics Evaluation

During the experimental evaluation of MTaaS Java, several object-oriented software metrics were collected and analyzed in order to compare the annotation-based MTaaS approach with a manual metamorphic testing implementation.

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

## About CSV Files

Raw metric output files are intentionally excluded from version control.

The generated CSV files contain environment-specific absolute paths. Because of this, only the metric collection configuration is included in the repository, while the generated outputs themselves are omitted.

## Publication Materials

The `papers/` directory contain publication describing the theoretical background and research results of this prototype.