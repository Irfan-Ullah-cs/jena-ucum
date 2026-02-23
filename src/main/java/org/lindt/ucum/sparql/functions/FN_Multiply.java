/*
 * SPARQL function: cdt:multiply(?q, ?factor) → quantity
 *
 * Multiplies a quantity by a numeric scalar.
 * Example: cdt:multiply("5 km"^^cdt:length, 3) → "15.0 km"^^cdt:length
 *
 * If both arguments are quantities, performs dimensional multiplication:
 * Example: cdt:multiply("5 m"^^cdt:length, "3 m"^^cdt:length) → "15.0 m2"^^cdt:ucum
 *
 * Usage:
 *   BIND(cdt:multiply(?speed, ?time) AS ?distance)
 */
package org.lindt.ucum.sparql.functions;

import javax.measure.Quantity;

import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.lindt.ucum.sparql.QuantityWrapper;

public class FN_Multiply extends FunctionBase2 {

    public static final String IRI = "https://w3id.org/cdt/multiply";

    @Override
    public NodeValue exec(NodeValue nv1, NodeValue nv2) {
        try {
            Quantity<?> q1 = QuantityWrapper.extractQuantity(nv1);

            // Case 1: Second argument is a numeric scalar
            if (nv2.isNumber()) {
                double factor = nv2.getDouble();
                @SuppressWarnings("unchecked")
                Quantity<?> result = ((Quantity) q1).multiply(factor);
                return QuantityWrapper.makeNodeValue(result);
            }

            // Case 2: Second argument is also a quantity → dimensional multiply
            if (QuantityWrapper.isQuantity(nv2)) {
                Quantity<?> q2 = QuantityWrapper.extractQuantity(nv2);
                @SuppressWarnings("unchecked")
                Quantity<?> result = ((Quantity) q1).multiply(q2);
                return QuantityWrapper.makeNodeValue(result);
            }

            throw new ExprEvalException("cdt:multiply — second argument must be numeric or a quantity");

        } catch (ExprEvalException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ExprEvalException("cdt:multiply — " + ex.getMessage());
        }
    }
}
