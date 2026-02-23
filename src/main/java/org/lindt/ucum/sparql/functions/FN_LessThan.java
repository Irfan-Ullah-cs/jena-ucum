/*
 * SPARQL function: cdt:lessThan(?q1, ?q2) → boolean
 *
 * Compares two quantities with automatic unit conversion.
 * Example: cdt:lessThan("500 m"^^cdt:length, "2 km"^^cdt:length) → true
 *
 * Usage:
 *   FILTER(cdt:lessThan(?temp, "100 Cel"^^cdt:temperature))
 */
package org.lindt.ucum.sparql.functions;

import javax.measure.Quantity;

import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.lindt.ucum.sparql.QuantityWrapper;

public class FN_LessThan extends FunctionBase2 {

    public static final String IRI = "https://w3id.org/cdt/lessThan";

    @Override
    public NodeValue exec(NodeValue nv1, NodeValue nv2) {
        try {
            Quantity<?> q1 = QuantityWrapper.extractQuantity(nv1);
            Quantity<?> q2 = QuantityWrapper.extractQuantity(nv2);
            @SuppressWarnings("unchecked")
            Quantity<?> q3 = ((Quantity) q2).to(q1.getUnit());
            boolean result = q1.getValue().doubleValue() < q3.getValue().doubleValue();
            return NodeValue.makeBoolean(result);
        } catch (ExprEvalException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ExprEvalException("cdt:lessThan — " + ex.getMessage());
        }
    }
}
