/*
 * Tests for UCUM SPARQL filter functions.
 *
 * Each test creates a small in-memory RDF model, runs a SPARQL query
 * using the cdt: functions, and verifies the result.
 *
 * Test categories:
 *   - Comparison: sameDimension, equals, greaterThan, lessThan
 *   - Arithmetic: add, subtract, multiply, divide
 *   - Conversion: convert, toSI
 *   - Accessors:  getValue, getUnit
 *   - Integration: model data with FILTER, ORDER BY, BIND
 */
package org.apache.jena.datatypes;

import static org.junit.Assert.*;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lindt.ucum.UCUMConfig;

public class TestSPARQLFunctions {

    @BeforeClass
    public static void setup() {
        UCUMConfig.init();
    }

    // ── Helper ────────────────────────────────────────────────────

    /**
     * Creates a model with two height triples for testing.
     */
    private Model createHeightModel() {
        Model model = ModelFactory.createDefaultModel();
        String ns = "http://example.org/";

        // Tower A: 500 m
        Resource towerA = model.createResource(ns + "towerA");
        towerA.addProperty(
            model.createProperty(ns + "height"),
            model.createTypedLiteral("500 m", 
                org.apache.jena.datatypes.TypeMapper.getInstance()
                    .getTypeByName("https://w3id.org/cdt/length"))
        );

        // Tower B: 1.2 km
        Resource towerB = model.createResource(ns + "towerB");
        towerB.addProperty(
            model.createProperty(ns + "height"),
            model.createTypedLiteral("1.2 km", 
                org.apache.jena.datatypes.TypeMapper.getInstance()
                    .getTypeByName("https://w3id.org/cdt/length"))
        );

        return model;
    }

    private boolean askQuery(Model model, String sparql) {
        try (QueryExecution qe = QueryExecution.model(model).query(sparql).build()) {
            return qe.execAsk();
        }
    }

    private String selectSingleValue(Model model, String sparql, String varName) {
        try (QueryExecution qe = QueryExecution.model(model).query(sparql).build()) {
            ResultSet rs = qe.execSelect();
            if (rs.hasNext()) {
                QuerySolution sol = rs.next();
                RDFNode node = sol.get(varName);
                return node != null ? node.toString() : null;
            }
            return null;
        }
    }

    // ── Tests: sameDimension ──────────────────────────────────────

    @Test
    public void testSameDimension_true() {
        Model m = ModelFactory.createDefaultModel();
        String q = """
            PREFIX cdt: <https://w3id.org/cdt/>
            ASK {
                BIND("5 km"^^cdt:length AS ?a)
                BIND("100 m"^^cdt:length AS ?b)
                FILTER(cdt:sameDimension(?a, ?b))
            }
            """;
        assertTrue("km and m should be same dimension", askQuery(m, q));
    }

    @Test
    public void testSameDimension_false() {
        Model m = ModelFactory.createDefaultModel();
        String q = """
            PREFIX cdt: <https://w3id.org/cdt/>
            ASK {
                BIND("5 km"^^cdt:length AS ?a)
                BIND("10 kg"^^cdt:mass AS ?b)
                FILTER(cdt:sameDimension(?a, ?b))
            }
            """;
        assertFalse("length and mass should not be same dimension", askQuery(m, q));
    }

    // ── Tests: equals ─────────────────────────────────────────────

    @Test
    public void testEquals_sameUnit() {
        Model m = ModelFactory.createDefaultModel();
        String q = """
            PREFIX cdt: <https://w3id.org/cdt/>
            ASK {
                BIND("5 km"^^cdt:length AS ?a)
                BIND("5 km"^^cdt:length AS ?b)
                FILTER(cdt:equals(?a, ?b))
            }
            """;
        assertTrue(askQuery(m, q));
    }

    @Test
    public void testEquals_unitConversion() {
        Model m = ModelFactory.createDefaultModel();
        String q = """
            PREFIX cdt: <https://w3id.org/cdt/>
            ASK {
                BIND("1 km"^^cdt:length AS ?a)
                BIND("1000 m"^^cdt:length AS ?b)
                FILTER(cdt:equals(?a, ?b))
            }
            """;
        assertTrue("1 km should equal 1000 m", askQuery(m, q));
    }

    // ── Tests: greaterThan / lessThan ─────────────────────────────

    @Test
    public void testGreaterThan() {
        Model m = ModelFactory.createDefaultModel();
        String q = """
            PREFIX cdt: <https://w3id.org/cdt/>
            ASK {
                BIND("2 km"^^cdt:length AS ?a)
                BIND("500 m"^^cdt:length AS ?b)
                FILTER(cdt:greaterThan(?a, ?b))
            }
            """;
        assertTrue("2 km > 500 m", askQuery(m, q));
    }

    @Test
    public void testLessThan() {
        Model m = ModelFactory.createDefaultModel();
        String q = """
            PREFIX cdt: <https://w3id.org/cdt/>
            ASK {
                BIND("500 m"^^cdt:length AS ?a)
                BIND("2 km"^^cdt:length AS ?b)
                FILTER(cdt:lessThan(?a, ?b))
            }
            """;
        assertTrue("500 m < 2 km", askQuery(m, q));
    }

    // ── Tests: add / subtract ─────────────────────────────────────

    @Test
    public void testAdd() {
        Model m = ModelFactory.createDefaultModel();
        String q = """
            PREFIX cdt: <https://w3id.org/cdt/>
            ASK {
                BIND("5 km"^^cdt:length AS ?a)
                BIND("3000 m"^^cdt:length AS ?b)
                BIND(cdt:add(?a, ?b) AS ?sum)
                FILTER(cdt:equals(?sum, "8 km"^^cdt:length))
            }
            """;
        assertTrue("5 km + 3000 m = 8 km", askQuery(m, q));
    }

    @Test
    public void testSubtract() {
        Model m = ModelFactory.createDefaultModel();
        String q = """
            PREFIX cdt: <https://w3id.org/cdt/>
            ASK {
                BIND("5 km"^^cdt:length AS ?a)
                BIND("3000 m"^^cdt:length AS ?b)
                BIND(cdt:subtract(?a, ?b) AS ?diff)
                FILTER(cdt:equals(?diff, "2 km"^^cdt:length))
            }
            """;
        assertTrue("5 km - 3000 m = 2 km", askQuery(m, q));
    }

    // ── Tests: multiply / divide ──────────────────────────────────

    @Test
    public void testMultiplyByScalar() {
        Model m = ModelFactory.createDefaultModel();
        String q = """
            PREFIX cdt: <https://w3id.org/cdt/>
            ASK {
                BIND("5 km"^^cdt:length AS ?a)
                BIND(cdt:multiply(?a, 3) AS ?result)
                FILTER(cdt:equals(?result, "15 km"^^cdt:length))
            }
            """;
        assertTrue("5 km * 3 = 15 km", askQuery(m, q));
    }

    @Test
    public void testDivideByScalar() {
        Model m = ModelFactory.createDefaultModel();
        String q = """
            PREFIX cdt: <https://w3id.org/cdt/>
            ASK {
                BIND("10 km"^^cdt:length AS ?a)
                BIND(cdt:divide(?a, 2) AS ?result)
                FILTER(cdt:equals(?result, "5 km"^^cdt:length))
            }
            """;
        assertTrue("10 km / 2 = 5 km", askQuery(m, q));
    }

    // ── Tests: convert ────────────────────────────────────────────

    @Test
    public void testConvert() {
        Model m = ModelFactory.createDefaultModel();
        String q = """
            PREFIX cdt: <https://w3id.org/cdt/>
            ASK {
                BIND("5 km"^^cdt:length AS ?a)
                BIND(cdt:convert(?a, "m") AS ?result)
                FILTER(cdt:equals(?result, "5000 m"^^cdt:length))
            }
            """;
        assertTrue("5 km converted to m = 5000 m", askQuery(m, q));
    }

    // ── Tests: getValue / getUnit ─────────────────────────────────

    @Test
    public void testGetValue() {
        Model m = ModelFactory.createDefaultModel();
        String q = """
            PREFIX cdt: <https://w3id.org/cdt/>
            ASK {
                BIND("5.5 km"^^cdt:length AS ?a)
                FILTER(cdt:getValue(?a) = 5.5)
            }
            """;
        assertTrue("getValue of '5.5 km' should be 5.5", askQuery(m, q));
    }

    @Test
    public void testGetUnit() {
        Model m = ModelFactory.createDefaultModel();
        String q = """
            PREFIX cdt: <https://w3id.org/cdt/>
            SELECT (cdt:getUnit("5 km"^^cdt:length) AS ?unit)
            WHERE {}
            """;
        String unit = selectSingleValue(m, q, "unit");
        assertNotNull("Unit should not be null", unit);
        assertTrue("Unit should be km", unit.contains("km"));
    }

    // ── Tests: toSI ───────────────────────────────────────────────

    @Test
    public void testToSI() {
        Model m = ModelFactory.createDefaultModel();
        String q = """
            PREFIX cdt: <https://w3id.org/cdt/>
            ASK {
                BIND("5 km"^^cdt:length AS ?a)
                FILTER(cdt:toSI(?a) = 5000.0)
            }
            """;
        assertTrue("toSI of 5 km should be 5000.0", askQuery(m, q));
    }

    // ── Tests: with model data ────────────────────────────────────

    @Test
    public void testFilterWithModelData() {
        Model model = createHeightModel();
        String q = """
            PREFIX ex: <http://example.org/>
            PREFIX cdt: <https://w3id.org/cdt/>
            SELECT ?tower WHERE {
                ?tower ex:height ?h .
                FILTER(cdt:greaterThan(?h, "600 m"^^cdt:length))
            }
            """;
        try (QueryExecution qe = QueryExecution.model(model).query(q).build()) {
            ResultSet rs = qe.execSelect();
            assertTrue("Should find tower B (1.2 km > 600 m)", rs.hasNext());
            String tower = rs.next().getResource("tower").getURI();
            assertTrue("Should be towerB", tower.endsWith("towerB"));
            assertFalse("Should only find one tower", rs.hasNext());
        }
    }

    @Test
    public void testAddWithModelData() {
        Model model = createHeightModel();
        String q = """
            PREFIX ex: <http://example.org/>
            PREFIX cdt: <https://w3id.org/cdt/>
            SELECT (cdt:add(?h1, ?h2) AS ?total) WHERE {
                ex:towerA ex:height ?h1 .
                ex:towerB ex:height ?h2 .
            }
            """;
        String total = selectSingleValue(model, q, "total");
        assertNotNull("Total should not be null", total);
        // 500 m + 1.2 km = 500 + 1200 = 1700 m
        System.out.println("[test] add result: " + total);
    }

    @Test
    public void testOrderByToSI() {
        Model model = createHeightModel();
        String q = """
            PREFIX ex: <http://example.org/>
            PREFIX cdt: <https://w3id.org/cdt/>
            SELECT ?tower ?h WHERE {
                ?tower ex:height ?h .
            }
            ORDER BY cdt:toSI(?h)
            """;
        try (QueryExecution qe = QueryExecution.model(model).query(q).build()) {
            ResultSet rs = qe.execSelect();
            // First should be 500 m (towerA), then 1.2 km (towerB)
            assertTrue(rs.hasNext());
            String first = rs.next().getResource("tower").getURI();
            assertTrue("First should be towerA (500m)", first.endsWith("towerA"));
            assertTrue(rs.hasNext());
            String second = rs.next().getResource("tower").getURI();
            assertTrue("Second should be towerB (1.2km)", second.endsWith("towerB"));
        }
    }
}