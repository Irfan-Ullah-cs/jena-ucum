/*
 * Utility class for extracting javax.measure.Quantity objects from SPARQL NodeValues.
 *
 * This is the UCUM equivalent of GeoSPARQL's GeometryWrapper.extract().
 * It allows SPARQL functions to work with UCUM quantity literals WITHOUT
 * modifying Jena's NodeValue class (unlike Maxime's fork which added
 * getQuantity()/getUnit() directly to NodeValue).
 *
 * Pattern:
 *   Maxime's fork:   nv.getQuantity()              — requires Jena core modification
 *   Our approach:    QuantityWrapper.extract(nv)    — external library, no core changes
 *   GeoSPARQL:       GeometryWrapper.extract(nv)    — same pattern we follow
 */
package org.lindt.ucum.sparql;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.format.UnitFormat;
import javax.measure.spi.ServiceProvider;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.NodeValue;
import org.lindt.ucum.datatype.CDTDatatype;
import org.lindt.ucum.datatype.quantity.QuantityDatatype;

/**
 * Extracts javax.measure objects from SPARQL NodeValues.
 * 
 * Usage in SPARQL functions:
 * <pre>
 *   Quantity<?> q = QuantityWrapper.extractQuantity(nodeValue);
 *   Unit<?> u = QuantityWrapper.extractUnit(nodeValue);
 * </pre>
 * 
 * @author irfan
 */
public class QuantityWrapper {

    private static final UnitFormat unitFormat;

    static {
        UnitFormat fmt;
        try {
            fmt = ServiceProvider.current().getFormatService().getUnitFormat("CS");
        } catch (Exception e) {
            try {
                fmt = ServiceProvider.current().getFormatService().getUnitFormat("UCUM");
            } catch (Exception e2) {
                fmt = ServiceProvider.current()
                        .getFormatService()
                        .getAvailableFormatNames(javax.measure.spi.FormatService.FormatType.UNIT_FORMAT)
                        .stream()
                        .findFirst()
                        .map(name -> ServiceProvider.current().getFormatService().getUnitFormat(name))
                        .orElseThrow(() -> new RuntimeException("No UCUM UnitFormat available"));
            }
        }
        unitFormat = fmt;
    }

    /**
     * Extract a Quantity from a NodeValue.
     * 
     * Works with any cdt:* quantity literal:
     *   "5 km"^^cdt:length  →  Quantity{5, km}
     *   "1.5 V"^^cdt:ucum   →  Quantity{1.5, V}
     *
     * @param nv the NodeValue from SPARQL evaluation
     * @return the parsed Quantity
     * @throws ExprEvalException if the NodeValue is not a valid quantity literal
     */
    public static Quantity<?> extractQuantity(NodeValue nv) {
        try {
            Node node = nv.asNode();

            if (!node.isLiteral()) {
                throw new ExprEvalException("Not a literal: " + node);
            }

            String datatypeURI = node.getLiteralDatatypeURI();
            if (datatypeURI == null || !datatypeURI.startsWith(CDTDatatype.CDT)) {
                throw new ExprEvalException("Not a CDT quantity literal: " + node);
            }

            RDFDatatype dtype = TypeMapper.getInstance().getTypeByName(datatypeURI);
            if (dtype == null) {
                throw new ExprEvalException("Unknown CDT datatype: " + datatypeURI);
            }

            if (!(dtype instanceof QuantityDatatype)) {
                throw new ExprEvalException("Not a quantity datatype: " + datatypeURI);
            }

            QuantityDatatype<?> qdt = (QuantityDatatype<?>) dtype;
            String lexicalForm = node.getLiteralLexicalForm();
            return qdt.parse(lexicalForm);

        } catch (ExprEvalException e) {
            throw e;
        } catch (Exception e) {
            throw new ExprEvalException("Failed to extract quantity from: " + nv + " — " + e.getMessage());
        }
    }

    /**
     * Extract a Unit from a NodeValue.
     * 
     * Accepts either a unit literal ("km"^^cdt:ucumunit) or extracts
     * the unit from a quantity literal ("5 km"^^cdt:length → km).
     * Also accepts a plain string literal ("km" → km).
     *
     * @param nv the NodeValue
     * @return the parsed Unit
     * @throws ExprEvalException if the unit cannot be extracted
     */
    public static Unit<?> extractUnit(NodeValue nv) {
        try {
            Node node = nv.asNode();

            if (!node.isLiteral()) {
                throw new ExprEvalException("Not a literal: " + node);
            }

            String datatypeURI = node.getLiteralDatatypeURI();
            String lexicalForm = node.getLiteralLexicalForm();

            // Case 1: It's a cdt:ucumunit literal → parse directly as unit
            if (datatypeURI != null && datatypeURI.equals(CDTDatatype.CDT + "ucumunit")) {
                return unitFormat.parse(lexicalForm);
            }

            // Case 2: It's a quantity literal → extract unit from it
            if (datatypeURI != null && datatypeURI.startsWith(CDTDatatype.CDT)) {
                Quantity<?> q = extractQuantity(nv);
                return q.getUnit();
            }

            // Case 3: Plain string → try parsing as unit
            return unitFormat.parse(lexicalForm);

        } catch (ExprEvalException e) {
            throw e;
        } catch (Exception e) {
            throw new ExprEvalException("Failed to extract unit from: " + nv + " — " + e.getMessage());
        }
    }

    /**
     * Create a NodeValue from a Quantity result.
     * 
     * Finds the most suitable CDT datatype and creates a typed literal.
     * Example: Quantity{8, m} → "8.0 m"^^cdt:length
     *
     * @param quantity the result quantity
     * @return a NodeValue wrapping the typed literal
     */
    public static NodeValue makeNodeValue(Quantity<?> quantity) {
        QuantityDatatype<?> dtype = QuantityDatatype.getMostSuitableQuantityDatatype(quantity);
        String lexical = quantity.getValue() + " " + unitFormat.format(quantity.getUnit());
        return NodeValue.makeNode(lexical, dtype);
    }

    /**
     * Check if a NodeValue contains a CDT quantity literal.
     */
    public static boolean isQuantity(NodeValue nv) {
        try {
            Node node = nv.asNode();
            if (!node.isLiteral()) return false;
            String uri = node.getLiteralDatatypeURI();
            if (uri == null || !uri.startsWith(CDTDatatype.CDT)) return false;
            RDFDatatype dtype = TypeMapper.getInstance().getTypeByName(uri);
            return dtype instanceof QuantityDatatype;
        } catch (Exception e) {
            return false;
        }
    }
}
