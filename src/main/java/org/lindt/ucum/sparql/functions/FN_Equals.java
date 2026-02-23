/*
 * SPARQL function: cdt:equals(?q1, ?q2) → boolean
 *
 * Returns true if two quantities represent the same measurement,
 * performing automatic unit conversion.
 * Example: cdt:equals("1 km"^^cdt:length, "1000 m"^^cdt:length) → true
 *
 * Usage:
 *   FILTER(cdt:equals(?sensor1, ?sensor2))
 */
package org.lindt.ucum.sparql.functions;
import javax.measure.Quantity;

import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.lindt.ucum.sparql.QuantityWrapper;

public class FN_Equals extends FunctionBase2 {

    public static final String IRI = "https://w3id.org/cdt/equals";

    @Override
    public NodeValue exec(NodeValue nv1, NodeValue nv2) {
        try {
            Quantity<?> q1 = QuantityWrapper.extractQuantity(nv1);
            Quantity<?> q2 = QuantityWrapper.extractQuantity(nv2);
            @SuppressWarnings("unchecked")
            Quantity<?> q3 = ((Quantity) q2).to(q1.getUnit());
            boolean equal = q1.getValue().doubleValue() == q3.getValue().doubleValue();
            return NodeValue.makeBoolean(equal);
        } catch (ExprEvalException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ExprEvalException("cdt:equals — " + ex.getMessage());
        }
    }
}
