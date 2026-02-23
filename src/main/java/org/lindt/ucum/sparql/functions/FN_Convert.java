/*
 * SPARQL function: cdt:convert(?q, ?targetUnit) → quantity
 *
 * Converts a quantity to a different unit of the same dimension.
 * The target unit can be a cdt:ucumunit literal or a plain string.
 * Example: cdt:convert("5 km"^^cdt:length, "m") → "5000.0 m"^^cdt:length
 *
 * Usage:
 *   BIND(cdt:convert(?height, "ft") AS ?heightInFeet)
 */
package org.lindt.ucum.sparql.functions;

import javax.measure.Quantity;
import javax.measure.Unit;

import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.lindt.ucum.sparql.QuantityWrapper;

public class FN_Convert extends FunctionBase2 {

    public static final String IRI = "https://w3id.org/cdt/convert";

    @Override
    public NodeValue exec(NodeValue nv1, NodeValue nv2) {
        try {
            Quantity<?> q = QuantityWrapper.extractQuantity(nv1);
            Unit<?> targetUnit = QuantityWrapper.extractUnit(nv2);

            @SuppressWarnings("unchecked")
            Quantity<?> converted = ((Quantity) q).to(targetUnit);
            return QuantityWrapper.makeNodeValue(converted);

        } catch (ExprEvalException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ExprEvalException("cdt:convert — " + ex.getMessage());
        }
    }
}
