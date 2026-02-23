/*
 * SPARQL function: cdt:getValue(?q) → xsd:double
 *
 * Extracts the numeric value from a quantity literal (without unit conversion).
 * Example: cdt:getValue("5.5 km"^^cdt:length) → 5.5
 *
 * Usage:
 *   FILTER(cdt:getValue(?mass) > 10)
 */
package org.lindt.ucum.sparql.functions;

import javax.measure.Quantity;

import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;
import org.lindt.ucum.sparql.QuantityWrapper;

public class FN_GetValue extends FunctionBase1 {

    public static final String IRI = "https://w3id.org/cdt/getValue";

    @Override
    public NodeValue exec(NodeValue nv) {
        try {
            Quantity<?> q = QuantityWrapper.extractQuantity(nv);
            return NodeValue.makeDouble(q.getValue().doubleValue());
        } catch (ExprEvalException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ExprEvalException("cdt:getValue — " + ex.getMessage());
        }
    }
}
