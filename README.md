# MTaaS Java

Annotation-based adaptation of the Metamorphic Testing-as-a-Service (MTaaS) architectural pattern for the Java programming language.

The project implements an annotation-driven approach for describing metamorphic relations, automatic relation discovery, source generation, and optional service integration for metamorphic testing workflows.

This repository contains a research prototype focused on adapting the original C# MTaaS concept to Java.

---

## Overview

Metamorphic testing is a software testing technique designed to address the oracle problem by validating relations between multiple executions instead of checking a single expected output.

The original MTaaS concept introduced decomposition of metamorphic relations into separate components together with automatic generation of executable testing infrastructure.

This project adapts that idea to Java using:

- Java annotations
- Annotation Processing API
- Compile-time source generation
- YAML-based relation specification generation
- Optional integration-layer generation

---

## Project Structure

```text
mtaas-core/
    Core annotations
    Annotation processor
    Relation collection and validation
    Source generators
    YAML generation

mtaas-integration/
    Integration-layer generation support

mtaas-sample/
    Example metamorphic relations and generated artifacts

mtaas-sample-integration/
    Integration usage examples

research/
    Supplementary research-related materials
```

---

## Annotation-Based Relation Model

The framework decomposes a metamorphic relation into several independent roles.

### Supported annotations

| Annotation | Purpose |
|---|---|
| `@ArtifactEntry` | Entry point of the tested artifact |
| `@DataGenerator` | Generates source test data |
| `@InputMetamorphosis` | Transforms input data |
| `@OutputMetamorphosis` | Transforms output data |
| `@OutputModelComparer` | Compares transformed outputs |

Relations are grouped using the `relationName` parameter.

Example:

```java
@OutputMetamorphosis(relationName = "order-total")
```

---

## Processing Pipeline

The framework operates during compilation using Java Annotation Processing.

### Main stages

1. Annotation discovery
2. Relation grouping by `relationName`
3. Semantic validation
4. Source generation
5. YAML specification generation

---

## Generated Artifacts

During compilation, the framework automatically generates:

- `MetamorphicRelation_*`
- `MetamorphicFunction_*`
- `MetamorphicServiceAdapter_*`
- `MetamorphicServiceRegistry`
- `spec.yaml`

Example generated artifacts are available in:

```text
docs/generated/
```

Generated files located inside `target/` are intentionally excluded from version control.

---

## Build

### Requirements

- Java 17+
- Maven 3.9+

### Build command

```bash
mvn clean install
```

---

## Examples

The repository contains two separate sample modules.

### `mtaas-sample`

This module demonstrates local usage of the generated MTaaS artifacts without a service layer.

It includes examples based on:

- order total calculation;
- convex hull computation.

These examples are executed as local tests and show how annotation-defined metamorphic relations can be discovered, generated, and validated during the build process.

### `mtaas-sample-integration`

This module demonstrates integration-oriented usage of the framework.

It includes a text normalization example and shows how generated service adapters can be used together with a Spring-based controller.

The module demonstrates:

- annotation-based relation definition;
- generation of integration adapters;
- execution through a local adapter;
- execution through a Spring controller.

---

## Research Context

This repository represents a research-oriented prototype created for evaluating the feasibility of adapting the MTaaS architectural pattern to Java using annotation processing and compile-time code generation.

The project also includes infrastructure for experimental evaluation using object-oriented software quality metrics.

Additional research information is available in:

```text
research/
```

---

## Original MTaaS Concept

- [Original MTaaS Repository](https://github.com/yakivyusin/MTaaS)

