package org.lindt.ucum;

import java.math.BigDecimal;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnconvertibleException;

import org.apache.jena.datatypes.DatatypeFormatException;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ResourceFactory;
import org.lindt.ucum.datatype.quantity.CDTUCUM;
import org.lindt.ucum.datatype.quantity.QuantityDatatype;

public final class UCUMOperations {

    private UCUMOperations() {}

    private static Quantity<?> parse(Literal lit) {
        return CDTUCUM.theType.parse(lit.getLexicalForm());
    }

    private static Literal toLiteral(Quantity<?> q) {
        return ResourceFactory.createTypedLiteral(CDTUCUM.theType.unparse(q), CDTUCUM.theType);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Literal add(Literal a, Literal b) {
        Quantity<?> q1 = parse(a);
        Quantity<?> q2 = parse(b);
        return toLiteral(((Quantity) q1).add(((Quantity) q2).to(q1.getUnit())));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Literal subtract(Literal a, Literal b) {
        Quantity<?> q1 = parse(a);
        Quantity<?> q2 = parse(b);
        return toLiteral(((Quantity) q1).subtract(((Quantity) q2).to(q1.getUnit())));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Literal multiply(Literal a, Literal b) {
        Quantity<?> q1 = parse(a);
        Quantity<?> q2 = parse(b);
        return toLiteral(((Quantity) q1).multiply((Quantity) q2));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Literal divide(Literal a, Literal b) {
        Quantity<?> q1 = parse(a);
        Quantity<?> q2 = parse(b);
        return toLiteral(((Quantity) q1).divide((Quantity) q2));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Literal multiply(Literal a, Number scalar) {
        Quantity<?> q = parse(a);
        return toLiteral(((Quantity) q).multiply(scalar));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Literal divide(Literal a, Number scalar) {
        Quantity<?> q = parse(a);
        return toLiteral(((Quantity) q).divide(scalar));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Literal convert(Literal a, String targetUnit) {
        Quantity<?> q = parse(a);
        Unit<?> unit = QuantityDatatype.unitFormat.parse(targetUnit);
        return toLiteral(((Quantity) q).to((Unit) unit));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static BigDecimal getValue(Literal a, String targetUnit) {
        Quantity<?> q = parse(a);
        Unit<?> unit = QuantityDatatype.unitFormat.parse(targetUnit);
        Quantity<?> converted = ((Quantity) q).to((Unit) unit);
        return new BigDecimal(converted.getValue().toString());
    }

    public static String getUnit(Literal a) {
        Quantity<?> q = parse(a);
        return QuantityDatatype.unitFormat.format(q.getUnit());
    }

    public static boolean equals(Literal a, Literal b) {
        return CDTUCUM.theType.isEqual(a.asNode().getLiteral(), b.asNode().getLiteral());
    }

    public static int compare(Literal a, Literal b) {
        return CDTUCUM.theType.compare(a.asNode().getLiteral(), b.asNode().getLiteral());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean sameDimension(Literal a, Literal b) {
        try {
            Quantity<?> q1 = parse(a);
            Quantity<?> q2 = parse(b);
            ((Quantity) q2).to((Unit) q1.getUnit());
            return true;
        } catch (UnconvertibleException e) {
            return false;
        } catch (DatatypeFormatException e) {
            return false;
        }
    }
}
