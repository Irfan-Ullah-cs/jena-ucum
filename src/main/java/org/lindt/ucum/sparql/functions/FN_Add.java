/*
 * SPARQL function: cdt:add(?q1, ?q2) → quantity
 *
 * Adds two quantities with automatic unit conversion.
 * Result is expressed in the unit of the first argument.
 * Example: cdt:add("5 km"^^cdt:length, "3000 m"^^cdt:length) → "8.0 km"^^cdt:length
 *
 * Usage:
 *   BIND(cdt:add(?height1, ?height2) AS ?total)
 */

package org.lindt.ucum.sparql.functions;

import javax.measure.Quantity;

import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.lindt.ucum.sparql.QuantityWrapper;

public class FN_Add extends FunctionBase2 {

    public static final String IRI = "https://w3id.org/cdt/add";

    @Override
    public NodeValue exec(NodeValue nv1, NodeValue nv2) {
        try {
            Quantity<?> q1 = QuantityWrapper.extractQuantity(nv1);
            Quantity<?> q2 = QuantityWrapper.extractQuantity(nv2);
            @SuppressWarnings("unchecked")
            Quantity<?> result = ((Quantity) q1).add(((Quantity) q2).to(q1.getUnit()));
            return QuantityWrapper.makeNodeValue(result);
        } catch (ExprEvalException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ExprEvalException("cdt:add — " + ex.getMessage());
        }
    }
}
