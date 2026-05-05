package org.lindt.ucum.datatype.quantity;

import org.apache.jena.datatypes.TypeMapper;
import java.math.BigDecimal;
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

public abstract class QuantityDatatype<Q extends Quantity<Q>> extends CDTDatatype {

    protected static final UnitFormat unitFormat;

    static {
        UnitFormat fmt;
        try {
            fmt = ServiceProvider.current().getFormatService().getUnitFormat("CS");
        } catch (Exception e) {
            try {
                fmt = ServiceProvider.current().getFormatService().getUnitFormat("UCUM");
            } catch (Exception e2) {
                fmt = ServiceProvider.current()
                        .getFormatService()
                        .getAvailableFormatNames(javax.measure.spi.FormatService.FormatType.UNIT_FORMAT)
                        .stream()
                        .findFirst()
                        .map(name -> ServiceProvider.current().getFormatService().getUnitFormat(name))
                        .orElseThrow(() -> new RuntimeException("No UCUM UnitFormat available"));
            }
        }
        unitFormat = fmt;
    }

    private final Class<Q> clazz;

    public QuantityDatatype(String uri, Class<Q> clazz) {
        super(uri);
        this.clazz = clazz;
    }

    @Override
    public Class<?> getJavaClass() {
        return clazz;
    }

    @Override
    public String unparse(Object value) {
        try {
            @SuppressWarnings("unchecked")
            final Quantity<Q> q = (Quantity<Q>) value;
            return q.getValue() + " " + unitFormat.format(q.getUnit());
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException("value must be an instance of Quantity of the specified dimension");
        }
    }

    @Override
    public Quantity<Q> parse(String lexicalForm) throws DatatypeFormatException {
        if (!lexicalForm.equals(lexicalForm.trim())) {
            throw new DatatypeFormatException(lexicalForm, this, "Lexical form must not have leading or trailing spaces");
        }
        int index = lexicalForm.indexOf(" ");
        if (index == -1) {
            try {
                final BigDecimal value = new BigDecimal(lexicalForm);
                @SuppressWarnings("unchecked")
                final Unit<Q> dimensionless = (Unit<Q>) unitFormat.parse("1");
                return Quantities.getQuantity(value, dimensionless).asType(clazz);
            } catch (NumberFormatException e) {
                throw new DatatypeFormatException(lexicalForm, this, "Not a valid number or quantity");
            } catch (Exception e) {
                throw new DatatypeFormatException(lexicalForm, this, "Not a valid quantity: " + e.getMessage());
            }
        }
        try {
            @SuppressWarnings("unchecked")
            final Unit<Q> unit = (Unit<Q>) unitFormat.parse(lexicalForm.substring(index + 1));
            final BigDecimal value = new BigDecimal(lexicalForm.substring(0, index));
            return Quantities.getQuantity(value, unit).asType(clazz);
        } catch (MeasurementParseException e) {
            throw new DatatypeFormatException(lexicalForm, this, "Not a valid unit: " + e.getMessage());
        } catch (NumberFormatException e) {
            throw new DatatypeFormatException(lexicalForm, this, "Not a valid number: " + e.getMessage());
        } catch (ClassCastException e) {
            throw new DatatypeFormatException(lexicalForm, this, "Not a valid " + clazz.getSimpleName() + " unit: " + e.getMessage());
        } catch (Exception e) {
            throw new DatatypeFormatException(lexicalForm, this, "Not a valid unit: " + e.getMessage());
        }
    }

    @Override
    public boolean isEqual(LiteralLabel value1, LiteralLabel value2) {
        try {
            final Quantity<Q> q1 = parse(value1.getLexicalForm());
            final Quantity<Q> q2 = parse(value2.getLexicalForm());
            final Quantity<Q> q3 = q2.to(q1.getUnit());
            return Objects.equals(q1.getUnit(), q3.getUnit())
                    && new BigDecimal(q1.getValue().toString())
                            .compareTo(new BigDecimal(q3.getValue().toString())) == 0;
        } catch (Exception e) {
            return false;
        }
    }

    public int compare(LiteralLabel value1, LiteralLabel value2) {
        try {
            final Quantity<Q> q1 = parse(value1.getLexicalForm());
            final Quantity<Q> q2 = parse(value2.getLexicalForm());
            final Quantity<Q> q3 = q2.to(q1.getUnit());
            return new BigDecimal(q1.getValue().toString())
                    .compareTo(new BigDecimal(q3.getValue().toString()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception while comparing quantity literals "
                    + value1.getLexicalForm() + " and " + value2.getLexicalForm());
        }
    }

    public static void loadCDTTypes(TypeMapper tm) {
        tm.registerDatatype(CDTUCUM.theType);
        tm.registerDatatype(CDTUCUMUnit.theType);
    }
}