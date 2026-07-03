# jena-ucum

**UCUM custom datatypes for Apache Jena** - unit-aware equality, comparison, and arithmetic in Java, with limited SPARQL support.

[![Java](https://img.shields.io/badge/java-17%2B-blue)](https://www.java.com/)
[![Jena](https://img.shields.io/badge/jena-5.x-orange)](https://jena.apache.org/)
[![License](https://img.shields.io/badge/license-Apache%202.0-green)](LICENSE)

## Overview

Add `jena-ucum` to your classpath and Jena gains the ability to store and compare physical quantities. In Java, the `UCUMOperations` class gives you unit-aware equality, comparison, arithmetic, and conversion across mixed units. In SPARQL, only `cdt:sameDimension` is supported as a real extension - arithmetic, ordering, and value-space equality inside `FILTER`/`BIND`/`ORDER BY` do not work the way you'd expect from a fully integrated datatype. See [SPARQL Support](#sparql-support) and [Limitations](#limitations) before relying on this in a query.

A CDT quantity literal looks like `"90 km/h"^^cdt:ucum` - a number, a space, and a UCUM unit code. **The original unit is preserved, not normalized.** `"1.5 km"^^cdt:ucum` stays `"1.5 km"` - it is not rewritten to `"1500 m"`. Cross-unit values are still recognized as equal (`"1 km"` and `"1000 m"` compare equal in Java), but the stored lexical form is whatever was written. The full spec is at [ci.mines-stetienne.fr/lindt/v4/custom_datatypes](https://ci.mines-stetienne.fr/lindt/v4/custom_datatypes).

Two datatypes are supported: `cdt:ucum` for quantity literals and `cdt:ucumunit` for bare unit expressions.

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

If the JAR is on the classpath inside a Jena application, `UCUMSubsystem` is picked up automatically. No `init()` call needed.

## Java API

`UCUMOperations` is a static utility class for arithmetic, comparison, and conversion outside of SPARQL. One import, everything available.

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

`UCUMOperations.equals()`/`.compare()` are **order-dependent** for certain cross-unit pairs - see [Limitations](#limitations).

## SPARQL Support

Only `cdt:sameDimension` is a real, working SPARQL extension. It can be used in `FILTER` or `BIND`.

```java
Model model = ModelFactory.createDefaultModel();
Property distance = model.createProperty("https://example.org/distance");
model.add(model.createResource("https://example.org/s1"), distance, model.createTypedLiteral("1 km", CDTUCUM.theTypeURI));
model.add(model.createResource("https://example.org/s2"), distance, model.createTypedLiteral("1 kg", CDTUCUM.theTypeURI));
model.add(model.createResource("https://example.org/s3"), distance, model.createTypedLiteral("500 m", CDTUCUM.theTypeURI));

Query query = QueryFactory.create("""
    PREFIX ex: <https://example.org/>
    PREFIX cdt: <https://w3id.org/cdt/>
    SELECT ?s WHERE {
        ?s ex:distance ?d .
        FILTER(cdt:sameDimension(?d, "1 m"^^<https://w3id.org/cdt/ucum>))
    }
""");
```

```
?s = ex:s1   (1 km  - same dimension as 1 m)
?s = ex:s3   (500 m - same dimension as 1 m)
```
`ex:s2` (`1 kg`) is correctly excluded.

### `FILTER(?a = ?b)` does not do value-space equality

Do not use this expecting cross-unit matching - it falls back to comparing lexical forms.

```java
model.add(model.createResource("https://example.org/s1"), distance, model.createTypedLiteral("1 km", CDTUCUM.theTypeURI));
model.add(model.createResource("https://example.org/s2"), distance, model.createTypedLiteral("1000 m", CDTUCUM.theTypeURI));

Query query = QueryFactory.create("""
    PREFIX ex: <https://example.org/>
    SELECT ?s WHERE {
        ?s ex:distance ?d .
        FILTER(?d = "1 km"^^<https://w3id.org/cdt/ucum>)
    }
""");
```

```
?s = ex:s1   (matches - identical lexical form)
```
`ex:s2` (`1000 m`, the same physical distance, written differently) does **not** match, despite `UCUMOperations.equals()` correctly treating these two as equal in Java. If you need cross-unit filtering in SPARQL, convert candidates to a common unit before loading them, or post-filter in Java.

Arithmetic, ordering, and `ORDER BY` are similarly unsupported for `cdt:ucum` literals in SPARQL - see [Limitations](#limitations).

## Limitations

**SPARQL arithmetic, ordering, and `ORDER BY` don't work on CDT literals.** Use `UCUMOperations` in Java instead.

**`FILTER(?a = ?b)` does not do value-space equality.** It matches only identical lexical forms - see [SPARQL Support](#sparql-support) above. A query written expecting cross-unit equality will silently return fewer rows than it should, with no error.

**`UCUMOperations.equals()`/`.compare()` are order-dependent for cross-unit conversions with non-terminating decimal factors.** For a pair like `3.6 km/h` and `1 m/s`, one conversion direction is exact while the other isn't - so the same two quantities only compare equal in one argument order:

```java
UCUMOperations.equals(lit("3.6 km/h"), lit("1 m/s"))   // true
UCUMOperations.equals(lit("1 m/s"), lit("3.6 km/h"))   // false
```

This is a known, unresolved defect, kept as a visible failing test rather than hidden.

**Conversions with non-integer factors have limited precision**, for the same reason:

```java
UCUMOperations.convert(lit("3.6 km/h"), "m/s")
// "1.00000000000000000000000000000000008 m/s"^^cdt:ucum
```

Confirmed exact conversions: `km <-> m`, `g <-> kg`, `h <-> s`, `min <-> s`, `MHz <-> Hz`, `mV <-> V`, `N <-> kg.m/s2`.

## Testing

```bash
mvn test
```

180 tests, one intentionally failing (the order-dependency bug above, kept visible rather than hidden).

## References

- Lefrançois, M. & Zimmermann, A. (2016). *Supporting Arbitrary Custom Datatypes in RDF and SPARQL*. ESWC 2016.
- Lefrançois, M. & Zimmermann, A. (2018). *The Unified Code for Units of Measure in RDF: cdt:ucum and other UCUM Datatypes*. ESWC 2018 (Demo).
- [CDT specification v4](https://ci.mines-stetienne.fr/lindt/v4/custom_datatypes) - formal definition of `cdt:ucum` and the quantity datatype vocabulary
- [UCUM specification](https://ucum.org/ucum) - the standard for unit codes
- [rdflib-ucum](https://github.com/Irfan-Ullah-cs/rdflib-ucum) - Python sibling library
- [rdflib.js-ucum](https://github.com/Irfan-Ullah-cs/rdflib.js-ucum) - JavaScript sibling library

## License

Apache License 2.0