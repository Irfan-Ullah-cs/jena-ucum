/*
 * SPARQL function: cdt:subtract(?q1, ?q2) → quantity
 *
 * Subtracts two quantities with automatic unit conversion.
 * Result is expressed in the unit of the first argument.
 * Example: cdt:subtract("5 km"^^cdt:length, "3000 m"^^cdt:length) → "2.0 km"^^cdt:length
 *
 * Usage:
 *   BIND(cdt:subtract(?maxTemp, ?minTemp) AS ?range)
 */
package org.lindt.ucum.sparql.functions;

import javax.measure.Quantity;

import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.lindt.ucum.sparql.QuantityWrapper;

public class FN_Subtract extends FunctionBase2 {

    public static final String IRI = "https://w3id.org/cdt/subtract";

    @Override
    public NodeValue exec(NodeValue nv1, NodeValue nv2) {
        try {
            Quantity<?> q1 = QuantityWrapper.extractQuantity(nv1);
            Quantity<?> q2 = QuantityWrapper.extractQuantity(nv2);
            @SuppressWarnings("unchecked")
            Quantity<?> result = ((Quantity) q1).subtract(((Quantity) q2).to(q1.getUnit()));
            return QuantityWrapper.makeNodeValue(result);
        } catch (ExprEvalException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ExprEvalException("cdt:subtract — " + ex.getMessage());
        }
    }
}
