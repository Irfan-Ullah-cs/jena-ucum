# jena-ucum-lib

A standalone library that adds [UCUM](https://ucum.org/) quantity datatypes to [Apache Jena](https://jena.apache.org/) 5.x via its public extension points.

## What It Does

Enables RDF literals with physical quantities and automatic unit conversion in SPARQL:

```turtle
@prefix cdt: <https://w3id.org/cdt/> .
@prefix ex:  <http://example.org/> .

ex:sensor1 ex:temperature "36.6 Cel"^^cdt:temperature .
ex:bridge  ex:length      "1.2 km"^^cdt:length .
ex:resistor ex:resistance "4.7 kOhm"^^cdt:electricResistance .
```

A single triple like `"1.2 km"^^cdt:length` replaces 3–4 triples needed by ontology-based approaches (QUDT, OM), while supporting automatic unit-aware equality (e.g., `"1 km"` equals `"1000 m"`).

## Supported Datatypes (33)

| Domain | Datatypes |
|--------|-----------|
| **Universal** | `ucum`, `ucumunit` |
| **Mechanics & Motion** | `acceleration`, `force`, `mass`, `power`, `pressure`, `speed`, `energy` |
| **Electrical** | `electricCapacitance`, `electricCharge`, `electricConductance`, `electricCurrent`, `electricInductance`, `electricPotential`, `electricResistance` |
| **Electromagnetic** | `magneticFlux`, `magneticFluxDensity` |
| **Radiation** | `radiationDoseAbsorbed`, `radiationDoseEffective`, `radioactivity` |
| **Light & Optics** | `illuminance`, `luminousFlux`, `luminousIntensity` |
| **Spatial & Temporal** | `angle`, `area`, `length`, `solidAngle`, `time`, `volume` |
| **Other** | `amountOfSubstance`, `catalyticActivity`, `dimensionless`, `frequency`, `temperature` |

## SPARQL Functions

Custom SPARQL functions registered under the `cdt:` namespace:

| Category | Functions |
|----------|-----------|
| **Comparison** | `cdt:sameDimension`, `cdt:eq`, `cdt:gt`, `cdt:lt` |
| **Arithmetic** | `cdt:add`, `cdt:subtract`, `cdt:multiply`, `cdt:divide` |
| **Conversion** | `cdt:convert`, `cdt:toSI` |
| **Accessors** | `cdt:getValue`, `cdt:getUnit` |

Example:
```sparql
PREFIX cdt: <https://w3id.org/cdt/>
SELECT ?tower (cdt:toSI(?h) AS ?height_si)
WHERE { ?tower ex:height ?h }
ORDER BY cdt:toSI(?h)
```

## Architecture

```
jena-ucum-lib/
├── pom.xml
├── src/main/
│   ├── resources/META-INF/services/
│   │   └── org.apache.jena.sys.JenaSubsystemLifecycle   ← Auto-discovery
│   └── java/org/lindt/ucum/
│       ├── UCUMSubsystem.java          ← ServiceLoader entry point
│       ├── UCUMConfig.java             ← One-time initialization
│       ├── datatype/
│       │   ├── CDTDatatype.java        ← Abstract base class
│       │   └── quantity/
│       │       ├── QuantityDatatype.java   ← Core parse/unparse/isEqual logic
│       │       ├── UnitDatatype.java       ← Unit-only literals
│       │       ├── CDTLength.java          ← 1 of 33 concrete types
│       │       ├── ...                     ← (31 more)
│       │       ├── CDTUCUM.java            ← Universal quantity type
│       │       └── CDTUCUMUnit.java        ← Universal unit type
│       └── sparql/
│           ├── QuantityWrapper.java        ← NodeValue ↔ Quantity bridge
│           ├── UCUMFunctions.java          ← FunctionRegistry registration
│           └── functions/                  ← 12 SPARQL function implementations
└── src/test/
    └── java/org/apache/jena/datatypes/
        ├── TestQuantityDatatypes.java
        ├── TestUnitDatatypes.java
        └── TestSPARQLFunctions.java
```

## How It Works

1. **ServiceLoader** — Jena discovers `UCUMSubsystem` at startup via `META-INF/services`
2. **TypeMapper registration** — All 33 datatypes are registered with Jena's `TypeMapper`
3. **SPARQL function registration** — All 12 functions registered in Jena's `FunctionRegistry`
4. **Transparent** — Any Jena program with this JAR on the classpath automatically gains UCUM support

No modifications to Jena source code. Works with vanilla Apache Jena 5.x.

## Prerequisites

- Java 17+
- Maven 3.8+

## Build & Test

```bash
mvn clean install
```

## Usage

Add the dependency to your project:

```xml
<dependency>
    <groupId>org.lindt</groupId>
    <artifactId>jena-ucum</artifactId>
    <version>2.0.0-SNAPSHOT</version>
</dependency>
```

Then just use Jena normally — datatypes and functions are registered automatically:

```java
Model model = ModelFactory.createDefaultModel();
TypeMapper tm = TypeMapper.getInstance();

Literal length = model.createTypedLiteral("1.2 km", tm.getTypeByName("https://w3id.org/cdt/length"));
Literal same   = model.createTypedLiteral("1200 m", tm.getTypeByName("https://w3id.org/cdt/length"));

// Unit-aware equality: returns true
length.sameValueAs(same); // true
```

## Key Dependencies

| Dependency | Purpose |
|-----------|---------|
| [Apache Jena 5.x](https://jena.apache.org/) | RDF framework |
| [JSR-385 (unit-api)](https://unitsofmeasurement.github.io/unit-api/) | Units of Measurement API |
| [Indriya 2.x](https://github.com/unitsofmeasurement/indriya) | JSR-385 reference implementation |
| [systems-ucum](https://github.com/unitsofmeasurement/uom-systems) | UCUM unit system provider |

## References

- Lefrançois & Zimmermann (2016) — [Supporting Arbitrary Custom Datatypes in RDF and SPARQL](https://www.emse.fr/~zimmermann/Papers/eswc2016.pdf), ESWC 2016
- Lefrançois & Zimmermann (2018) — [The cdt:ucum Datatype](https://hal-emse.ccsd.cnrs.fr/emse-01883261/document)
- [Original jena-ucum implementation](https://github.com/OpenSensingCity/jena-ucum) — OpenSensingCity (Jena 3.6.0 fork)
- [UCUM Specification](https://ucum.org/ucum)
- [cdt:ucum namespace](https://w3id.org/cdt/)

## License

Apache License 2.0