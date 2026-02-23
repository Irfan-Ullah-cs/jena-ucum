package org.lindt.ucum.sparql.functions;

import javax.measure.Quantity;
import javax.measure.UnconvertibleException;

import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.lindt.ucum.sparql.QuantityWrapper;

/*
 * SPARQL function: cdt:sameDimension(?q1, ?q2) → boolean
 *
 * Returns true if two quantities have the same physical dimension
 * (e.g., km and miles are both length), false otherwise.
 *
 * Usage:
 *   FILTER(cdt:sameDimension(?height, ?width))
 */

public class FN_SameDimension extends FunctionBase2 {

    public static final String IRI = "https://w3id.org/cdt/sameDimension";

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public NodeValue exec(NodeValue nv1, NodeValue nv2) {
        try {
            Quantity<?> q1 = QuantityWrapper.extractQuantity(nv1);
            Quantity<?> q2 = QuantityWrapper.extractQuantity(nv2);
            // If conversion succeeds, they share the same dimension
            ((Quantity) q2).to((javax.measure.Unit) q1.getUnit());
            return NodeValue.makeBoolean(true);
        } catch (UnconvertibleException ex) {
            return NodeValue.makeBoolean(false);
        } catch (ExprEvalException ex) {
            throw ex;
        } catch (Exception ex) {
            return NodeValue.makeBoolean(false);
        }
    }
}