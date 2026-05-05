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
 * Follows the same pattern as GeoSPARQL's GeometryWrapper.extract().
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
            return qdt.parse(node.getLiteralLexicalForm());

        } catch (ExprEvalException e) {
            throw e;
        } catch (Exception e) {
            throw new ExprEvalException("Failed to extract quantity from: " + nv + " — " + e.getMessage());
        }
    }

    public static Unit<?> extractUnit(NodeValue nv) {
        try {
            Node node = nv.asNode();

            if (!node.isLiteral()) {
                throw new ExprEvalException("Not a literal: " + node);
            }

            String datatypeURI = node.getLiteralDatatypeURI();
            String lexicalForm = node.getLiteralLexicalForm();

            if (datatypeURI != null && datatypeURI.equals(CDTDatatype.CDT + "ucumunit")) {
                return unitFormat.parse(lexicalForm);
            }

            if (datatypeURI != null && datatypeURI.startsWith(CDTDatatype.CDT)) {
                return extractQuantity(nv).getUnit();
            }

            return unitFormat.parse(lexicalForm);

        } catch (ExprEvalException e) {
            throw e;
        } catch (Exception e) {
            throw new ExprEvalException("Failed to extract unit from: " + nv + " — " + e.getMessage());
        }
    }

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