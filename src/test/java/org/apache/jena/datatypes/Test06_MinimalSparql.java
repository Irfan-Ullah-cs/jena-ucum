package org.apache.jena.datatypes;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lindt.ucum.UCUMConfig;
import org.lindt.ucum.datatype.quantity.CDTUCUM;

public class Test06_MinimalSparql {

    private static final String EX = "https://example.org/";

    @BeforeClass
    public static void setup() {
        UCUMConfig.init();
    }

    // -- cdt:sameDimension, through real SPARQL dispatch --

    @Test
    public void samedimension_filter_selects_compatible_length() {
        Model model = ModelFactory.createDefaultModel();
        Property distance = model.createProperty(EX + "distance");
        model.add(model.createResource(EX + "s1"), distance, model.createTypedLiteral("1 km", CDTUCUM.theTypeURI));
        model.add(model.createResource(EX + "s2"), distance, model.createTypedLiteral("1 kg", CDTUCUM.theTypeURI));
        model.add(model.createResource(EX + "s3"), distance, model.createTypedLiteral("500 m", CDTUCUM.theTypeURI));

        Query query = QueryFactory.create(
            "PREFIX ex: <" + EX + "> PREFIX cdt: <https://w3id.org/cdt/> "
          + "SELECT ?s WHERE { ?s ex:distance ?d . "
          + "FILTER(cdt:sameDimension(?d, \"1 m\"^^<https://w3id.org/cdt/ucum>)) }");

        Set<String> matched = new HashSet<>();
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet rs = qexec.execSelect();
            while (rs.hasNext()) {
                QuerySolution sol = rs.next();
                matched.add(sol.getResource("s").getURI());
            }
        }

        assertTrue(matched.contains(EX + "s1"));
        assertTrue(matched.contains(EX + "s3"));
        assertFalse(matched.contains(EX + "s2"));
    }

    @Test
    public void samedimension_filter_derived_unit_matches() {
        Model model = ModelFactory.createDefaultModel();
        Property force = model.createProperty(EX + "force");
        model.add(model.createResource(EX + "s1"), force, model.createTypedLiteral("1 N", CDTUCUM.theTypeURI));
        model.add(model.createResource(EX + "s2"), force, model.createTypedLiteral("1 kg.m/s2", CDTUCUM.theTypeURI));
        model.add(model.createResource(EX + "s3"), force, model.createTypedLiteral("1 J", CDTUCUM.theTypeURI));

        Query query = QueryFactory.create(
            "PREFIX ex: <" + EX + "> PREFIX cdt: <https://w3id.org/cdt/> "
          + "SELECT ?s WHERE { ?s ex:force ?f . "
          + "FILTER(cdt:sameDimension(?f, \"1 N\"^^<https://w3id.org/cdt/ucum>)) }");

        Set<String> matched = new HashSet<>();
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet rs = qexec.execSelect();
            while (rs.hasNext()) {
                QuerySolution sol = rs.next();
                matched.add(sol.getResource("s").getURI());
            }
        }

        assertTrue(matched.contains(EX + "s1"));
        assertTrue(matched.contains(EX + "s2"));
        assertFalse(matched.contains(EX + "s3"));
    }

    // -- Equality via FILTER(?d = "..."^^cdt:ucum) --
    // Confirmed: does not do value-space comparison for cdt:ucum.
    // FILTER(?d = "1 km") matched only the identical lexical form,
    // not the cross-unit-equal "1000 m" - same for mass below.

    @Test
    public void equal_filter_identical_lexical_form() {
        Model model = ModelFactory.createDefaultModel();
        Property distance = model.createProperty(EX + "distance");
        model.add(model.createResource(EX + "s1"), distance, model.createTypedLiteral("1000 m", CDTUCUM.theTypeURI));
        model.add(model.createResource(EX + "s2"), distance, model.createTypedLiteral("500 m", CDTUCUM.theTypeURI));

        Query query = QueryFactory.create(
            "PREFIX ex: <" + EX + "> "
          + "SELECT ?s WHERE { ?s ex:distance ?d . "
          + "FILTER(?d = \"1000 m\"^^<https://w3id.org/cdt/ucum>) }");

        Set<String> matched = new HashSet<>();
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet rs = qexec.execSelect();
            while (rs.hasNext()) {
                QuerySolution sol = rs.next();
                matched.add(sol.getResource("s").getURI());
            }
        }

        assertTrue(matched.contains(EX + "s1"));
        assertFalse(matched.contains(EX + "s2"));
    }

    @Test
    public void equal_filter_cross_unit_length_does_not_match() {
        Model model = ModelFactory.createDefaultModel();
        Property distance = model.createProperty(EX + "distance");
        model.add(model.createResource(EX + "s1"), distance, model.createTypedLiteral("1 km", CDTUCUM.theTypeURI));
        model.add(model.createResource(EX + "s2"), distance, model.createTypedLiteral("1000 m", CDTUCUM.theTypeURI));
        model.add(model.createResource(EX + "s3"), distance, model.createTypedLiteral("500 m", CDTUCUM.theTypeURI));

        Query query = QueryFactory.create(
            "PREFIX ex: <" + EX + "> "
          + "SELECT ?s WHERE { ?s ex:distance ?d . "
          + "FILTER(?d = \"1 km\"^^<https://w3id.org/cdt/ucum>) }");

        Set<String> matched = new HashSet<>();
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet rs = qexec.execSelect();
            while (rs.hasNext()) {
                QuerySolution sol = rs.next();
                matched.add(sol.getResource("s").getURI());
            }
        }

        assertTrue(matched.contains(EX + "s1"));
        assertFalse(matched.contains(EX + "s2"));
        assertFalse(matched.contains(EX + "s3"));
    }

    @Test
    public void equal_filter_cross_unit_mass_does_not_match() {
        Model model = ModelFactory.createDefaultModel();
        Property mass = model.createProperty(EX + "mass");
        model.add(model.createResource(EX + "s1"), mass, model.createTypedLiteral("1 kg", CDTUCUM.theTypeURI));
        model.add(model.createResource(EX + "s2"), mass, model.createTypedLiteral("1000 g", CDTUCUM.theTypeURI));
        model.add(model.createResource(EX + "s3"), mass, model.createTypedLiteral("500 g", CDTUCUM.theTypeURI));

        Query query = QueryFactory.create(
            "PREFIX ex: <" + EX + "> "
          + "SELECT ?s WHERE { ?s ex:mass ?m . "
          + "FILTER(?m = \"1 kg\"^^<https://w3id.org/cdt/ucum>) }");

        Set<String> matched = new HashSet<>();
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet rs = qexec.execSelect();
            while (rs.hasNext()) {
                QuerySolution sol = rs.next();
                matched.add(sol.getResource("s").getURI());
            }
        }

        assertTrue(matched.contains(EX + "s1"));
        assertFalse(matched.contains(EX + "s2"));
        assertFalse(matched.contains(EX + "s3"));
    }
}