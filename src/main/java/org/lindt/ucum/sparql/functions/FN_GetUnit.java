/*
 * SPARQL function: cdt:getUnit(?q) → xsd:string
 *
 * Extracts the unit string from a quantity literal.
 * Example: cdt:getUnit("5.5 km"^^cdt:length) → "km"
 *
 * Usage:
 *   BIND(cdt:getUnit(?measurement) AS ?unit)
 */
package org.lindt.ucum.sparql.functions;

import javax.measure.Quantity;
import javax.measure.format.UnitFormat;
import javax.measure.spi.ServiceProvider;

import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;
import org.lindt.ucum.sparql.QuantityWrapper;

public class FN_GetUnit extends FunctionBase1 {

    public static final String IRI = "https://w3id.org/cdt/getUnit";

    private static final UnitFormat unitFormat;

    static {
        UnitFormat fmt;
        try {
            fmt = ServiceProvider.current().getFormatService().getUnitFormat("CS");
        } catch (Exception e) {
            fmt = ServiceProvider.current().getFormatService().getUnitFormat("UCUM");
        }
        unitFormat = fmt;
    }

    @Override
    public NodeValue exec(NodeValue nv) {
        try {
            Quantity<?> q = QuantityWrapper.extractQuantity(nv);
            String unitStr = unitFormat.format(q.getUnit());
            return NodeValue.makeString(unitStr);
        } catch (ExprEvalException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ExprEvalException("cdt:getUnit — " + ex.getMessage());
        }
    }
}
