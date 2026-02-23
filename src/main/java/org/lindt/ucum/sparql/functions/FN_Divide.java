/*
 * SPARQL function: cdt:divide(?q, ?divisor) → quantity
 *
 * Divides a quantity by a numeric scalar.
 * Example: cdt:divide("10 km"^^cdt:length, 2) → "5.0 km"^^cdt:length
 *
 * If both arguments are quantities, performs dimensional division:
 * Example: cdt:divide("100 m"^^cdt:length, "10 s"^^cdt:time) → "10.0 m/s"^^cdt:ucum
 *
 * Usage:
 *   BIND(cdt:divide(?distance, ?time) AS ?speed)
 */
package org.lindt.ucum.sparql.functions;

import javax.measure.Quantity;

import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.lindt.ucum.sparql.QuantityWrapper;

public class FN_Divide extends FunctionBase2 {

    public static final String IRI = "https://w3id.org/cdt/divide";

    @Override
    public NodeValue exec(NodeValue nv1, NodeValue nv2) {
        try {
            Quantity<?> q1 = QuantityWrapper.extractQuantity(nv1);

            // Case 1: Second argument is a numeric scalar
            if (nv2.isNumber()) {
                double divisor = nv2.getDouble();
                if (divisor == 0.0) {
                    throw new ExprEvalException("cdt:divide — division by zero");
                }
                @SuppressWarnings("unchecked")
                Quantity<?> result = ((Quantity) q1).divide(divisor);
                return QuantityWrapper.makeNodeValue(result);
            }

            // Case 2: Second argument is also a quantity → dimensional divide
            if (QuantityWrapper.isQuantity(nv2)) {
                Quantity<?> q2 = QuantityWrapper.extractQuantity(nv2);
                @SuppressWarnings("unchecked")
                Quantity<?> result = ((Quantity) q1).divide(q2);
                return QuantityWrapper.makeNodeValue(result);
            }

            throw new ExprEvalException("cdt:divide — second argument must be numeric or a quantity");

        } catch (ExprEvalException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ExprEvalException("cdt:divide — " + ex.getMessage());
        }
    }
}
