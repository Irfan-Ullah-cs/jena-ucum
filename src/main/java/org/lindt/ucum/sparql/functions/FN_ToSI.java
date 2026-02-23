/*
 * SPARQL function: cdt:toSI(?q) → xsd:double
 *
 * Converts a quantity to its SI base unit and returns the numeric value.
 * Useful for ORDER BY since SPARQL can sort doubles natively.
 * Example: cdt:toSI("5 km"^^cdt:length) → 5000.0
 *          cdt:toSI("1 h"^^cdt:time) → 3600.0
 *
 * Usage:
 *   ORDER BY cdt:toSI(?height)
 */
package org.lindt.ucum.sparql.functions;

import javax.measure.Quantity;

import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;
import org.lindt.ucum.sparql.QuantityWrapper;

public class FN_ToSI extends FunctionBase1 {

    public static final String IRI = "https://w3id.org/cdt/toSI";

    @Override
    public NodeValue exec(NodeValue nv) {
        try {
            Quantity<?> q = QuantityWrapper.extractQuantity(nv);
            // toSystemUnit() converts to the SI base unit for this dimension
            @SuppressWarnings("unchecked")
            Quantity<?> siQuantity = ((Quantity) q).to(q.getUnit().getSystemUnit());
            return NodeValue.makeDouble(siQuantity.getValue().doubleValue());
        } catch (ExprEvalException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ExprEvalException("cdt:toSI — " + ex.getMessage());
        }
    }
}
