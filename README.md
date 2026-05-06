# jena-ucum

**UCUM custom datatypes for Apache Jena** - unit-aware equality, comparison, arithmetic, and SPARQL support.

[![Java](https://img.shields.io/badge/java-17%2B-blue)](https://www.java.com/)
[![Jena](https://img.shields.io/badge/jena-5.x-orange)](https://jena.apache.org/)
[![License](https://img.shields.io/badge/license-Apache%202.0-green)](LICENSE)

## Overview

Add `jena-ucum` to your classpath and Jena gains the ability to store, compare, sort, and compute with physical quantities both in Java and inside SPARQL queries.

A CDT quantity literal looks like `"90 km/h"^^cdt:ucum` - a number, a space, and a UCUM unit code. Under the hood, everything normalizes to SI base units, so `"1 km"` and `"1000 m"` are treated as equal. The full spec is at [ci.mines-stetienne.fr/lindt/v4/custom_datatypes](https://ci.mines-stetienne.fr/lindt/v4/custom_datatypes).

Two datatypes are supported: `cdt:ucum` for quantity literals and `cdt:ucumunit` for bare unit expressions. The library doesn't try to cover every CDT dimension type.

No modifications to Jena source code. It registers through Jena's public extension points: `TypeMapper` for datatypes and `FunctionRegistry` for SPARQL functions.

Unit parsing and conversion runs on:

- **[JSR-385 / unit-api 2.2](https://unitsofmeasurement.github.io/unit-api/)** - standard API for units of measurement
- **[Indriya 2.2.3](https://github.com/unitsofmeasurement/indriya)** - JSR-385 reference implementation for dimensional analysis, conversion, and arithmetic
- **[systems-ucum 2.2](https://github.com/unitsofmeasurement/uom-systems)** - UCUM unit definitions
- **[systems-quantity 2.2](https://github.com/unitsofmeasurement/uom-systems)** - quantity types used alongside systems-ucum

Values are parsed as `BigDecimal`, preserving arbitrary precision for integer-ratio conversions.

## Installation

Clone and install locally:

```bash
git clone https://github.com/Irfan-Ullah-cs/jena-ucum.git
cd jena-ucum
mvn install
```

Then add to your project:

```xml
<dependency>
    <groupId>org.lindt</groupId>
    <artifactId>jena-ucum</artifactId>
    <version>2.0.0-SNAPSHOT</version>
</dependency>
```

**Requirements:** Java 17+, Maven 3.8+, Apache Jena 5.2.0

## Quick Start

```java
UCUMConfig.init(); // explicit init outside Jena's lifecycle

TypeMapper tm = TypeMapper.getInstance();
RDFDatatype ucum = tm.getTypeByName("https://w3id.org/cdt/ucum");

Model model = ModelFactory.createDefaultModel();
Literal a = model.createTypedLiteral("1 km",   ucum);
Literal b = model.createTypedLiteral("1000 m", ucum);

a.sameValueAs(b); // true - unit-aware equality
```

If the JAR is on the classpath inside a Jena application, `UCUMSubsystem` is picked up automatically via `JenaSystem`. No `init()` call needed.

## Java API

`UCUMOperations` is a static utility class for post-query processing, aggregation, and unit conversion outside of SPARQL. One import, everything available.

```java
import org.lindt.ucum.UCUMOperations;

Literal lit(String lex) {
    return ResourceFactory.createTypedLiteral(lex, CDTUCUM.theType);
}

// arithmetic - cross-unit, result in left operand's unit
UCUMOperations.add(lit("1 kJ"), lit("500 J"))              // "1.5 kJ"^^cdt:ucum

// arithmetic - dimension-changing
UCUMOperations.divide(lit("10 V"), lit("2 A"))             // "5 V/A"^^cdt:ucum  (== 5 Ohm)

// conversion
UCUMOperations.convert(lit("1 N"), "kg.m/s2")              // "1 kg.m/s2"^^cdt:ucum

// accessors
UCUMOperations.getValue(lit("1 km"), "m")                  // BigDecimal("1000")
UCUMOperations.getUnit(lit("9.8 m/s2"))                    // "m/s2"

// comparative
UCUMOperations.equals(lit("1 N"), lit("1 kg.m/s2"))        // true
UCUMOperations.compare(lit("500 m"), lit("1 km"))          // negative
UCUMOperations.sameDimension(lit("1 m"), lit("1 kg"))      // false
```

Incompatible dimensions throw on `add` and `subtract`. Incompatible units throw on `convert`.

## SPARQL Support

The `cdt:sameDimension` function works in SPARQL `FILTER` and `BIND`:

```sparql
PREFIX cdt: <https://w3id.org/cdt/>
PREFIX ex:  <https://example.org/>

SELECT ?s WHERE {
    ?s ex:measurement ?v .
    FILTER(cdt:sameDimension(?v, "1 m"^^<https://w3id.org/cdt/ucum>))
}
```

Unit-aware equality works through Jena's `isEqual` contract, so `FILTER(?a = ?b)` matches across units. Both `"1 km"^^cdt:ucum` and `"1000 m"^^cdt:ucum` match `FILTER(?l = "1000 m"^^<https://w3id.org/cdt/ucum>)`.

## Limitations

**SPARQL arithmetic and ordering operators don't work on CDT literals.** Use `UCUMOperations` in Java for arithmetic, and `cdt:sameDimension` with `FILTER(?a = ?b)` for filtering.

**Conversions with non-integer factors have limited precision.** A conversion like `3.6 km/h -> m/s` involves `5/18`, which can't be represented exactly in decimal. The result carries a small artifact:

```java
UCUMOperations.convert(lit("3.6 km/h"), "m/s")
// "1.00000000000000000000000000000000008 m/s"^^cdt:ucum
```

Conversions with integer-ratio factors are exact: `km <-> m`, `h <-> s`, `kPa <-> Pa`, `MHz <-> Hz`, and so on.

## Testing

```bash
mvn test
```

The test suite covers parsing and registration (`Test01`), namespace correctness (`Test02`), cross-unit equality (`Test03`), ordering (`Test04`), and `UCUMOperations` (`Test05`).

## References

- Lefrançois, M. & Zimmermann, A. (2016). *Supporting Arbitrary Custom Datatypes in RDF and SPARQL*. ESWC 2016.
- Lefrançois, M. & Zimmermann, A. (2018). *The Unified Code for Units of Measure in RDF: cdt:ucum and other UCUM Datatypes*. ESWC 2018 (Demo).
- [CDT specification v4](https://ci.mines-stetienne.fr/lindt/v4/custom_datatypes) - formal definition of `cdt:ucum` and the quantity datatype vocabulary
- [UCUM specification](https://ucum.org/ucum) - the standard for unit codes
- [rdflib-ucum](https://github.com/Irfan-Ullah-cs/rdflib-ucum) - Python sibling library
- [rdflib.js-ucum](https://github.com/Irfan-Ullah-cs/rdflib.js-ucum) - JavaScript sibling library

## License

Apache License 2.0