/*
 * Adapted from the original jena-ucum implementation by Maxime Lefrançois.
 * Moved from org.apache.jena.datatypes.cdt.quantity to separate library.
 *
 * CHANGES from original:
 *   - Package: org.apache.jena.datatypes.cdt.quantity → org.lindt.ucum.datatype
 *   - tec.uom.se.quantity.Quantities → tech.units.indriya.quantity.Quantities
 *   - systems.uom.ucum.internal.format.TokenException → removed
 *   - javax.measure.format.ParserException → javax.measure.format.MeasurementParseException
 */
package org.lindt.ucum.datatype.quantity; 


import java.util.Objects;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.format.MeasurementParseException;
import javax.measure.format.UnitFormat;
import javax.measure.spi.ServiceProvider;
import org.apache.jena.datatypes.DatatypeFormatException;
import org.apache.jena.graph.impl.LiteralLabel;
import org.lindt.ucum.datatype.CDTDatatype;

import tech.units.indriya.quantity.Quantities;

/**
 * @param <Q> the quantity kind for the datatype
 * @author maxime.lefrancois
 */
public abstract class UnitDatatype<Q extends Quantity<Q>> extends CDTDatatype {

    protected static final UnitFormat unitFormat;
    
    static {
        UnitFormat fmt;
        try {
            fmt = ServiceProvider.current().getFormatService().getUnitFormat("CS");
        } catch (Exception e) {
            try {
                fmt = ServiceProvider.current().getFormatService().getUnitFormat("UCUM");
            } catch (Exception e2) {
                fmt = ServiceProvider.current().getFormatService().getAvailableFormatNames(javax.measure.spi.FormatService.FormatType.UNIT_FORMAT).stream()
                        .findFirst()
                        .map(name -> ServiceProvider.current().getFormatService().getUnitFormat(name))
                        .orElseThrow(() -> new RuntimeException("No UCUM UnitFormat available"));
            }
        }
        unitFormat = fmt;
    }

    private final Class<Q> clazz;

    public UnitDatatype(String uri, Class<Q> clazz) {
        super(uri);
        this.clazz = clazz;
    }

    /**
     * Returns the java class which is used to represent value instances of this
     * datatype.
     */
    @Override
    public Class<?> getJavaClass() {
        return clazz;
    }

    /**
     * Convert a value of this datatype out to lexical form.
     * 
     * @throws IllegalArgumentException if the value is not an instance of Unit of the specified dimension
     */
    @Override
    public String unparse(Object value) {
        try {
            @SuppressWarnings("unchecked")
            final Unit<Q> q = (Unit<Q>) value;
            return unitFormat.format(q);
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException("value must be an instance of Unit of the specified dimension");
        }
    }

    /**
     * Parse a lexical form of this datatype to a value
     *
     * @throws DatatypeFormatException if the lexical form is not legal
     */
    @Override
    public Unit<Q> parse(String lexicalForm) throws DatatypeFormatException {
        try {
            @SuppressWarnings("unchecked")
            final Unit<Q> unit = (Unit<Q>) unitFormat.parse(lexicalForm);
            return unit.asType(clazz);
        } catch (MeasurementParseException e) {
            throw new DatatypeFormatException(lexicalForm, this, "Not a valid unit: " + e.getMessage());
        } catch (ClassCastException e) {
            throw new DatatypeFormatException(lexicalForm, this, "Not a valid " + clazz.getSimpleName() + " unit: " + e.getMessage());
        } catch (Exception e) {
            // Catches internal parser exceptions like systems.uom.ucum.internal.format.TokenException
            throw new DatatypeFormatException(lexicalForm, this, "Not a valid unit: " + e.getMessage());
        }
    }

    /**
     * Compares two instances of unit literals.
     */
    @Override
    public boolean isEqual(LiteralLabel value1, LiteralLabel value2) {
        try {
            final Unit<Q> u1 = parse(value1.getLexicalForm());
            final Quantity<Q> q1 = Quantities.getQuantity(1, u1);
            final Unit<Q> u2 = parse(value2.getLexicalForm());
            final Quantity<Q> q2 = Quantities.getQuantity(1, u2);
            final Quantity<Q> q3 = q2.to(q1.getUnit());
            return Objects.equals(q1.getUnit(), q3.getUnit())
                    && q1.getValue().doubleValue() == q3.getValue().doubleValue();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     */
    public int compare(LiteralLabel value1, LiteralLabel value2) {
        if (isEqual(value1, value2)) {
            return 0;
        }
        throw new IllegalArgumentException("Exception while comparing unit literals " + value1.getLexicalForm() + " and " + value2.getLexicalForm());
    }

}
