package org.apache.jena.datatypes;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import javax.measure.Quantity;
import javax.measure.format.UnitFormat;
import javax.measure.spi.ServiceProvider;

import org.apache.jena.graph.impl.LiteralLabel;
import org.apache.jena.graph.impl.LiteralLabelFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lindt.ucum.UCUMConfig;
import org.lindt.ucum.datatype.CDTDatatype;
import org.lindt.ucum.datatype.quantity.CDTUCUM;
import org.lindt.ucum.datatype.quantity.QuantityDatatype;
import org.lindt.ucum.datatype.quantity.UnitDatatype;

public class Test01_ParsingAndRegistration {

    private static UnitFormat unitFormat;

    @BeforeClass
    public static void setup() {
        UCUMConfig.init();
        unitFormat = ServiceProvider.current().getFormatService().getUnitFormat("CS");
    }

    // -- Valid lexical forms ----

    @Test
    public void integer_value() {
        Quantity<?> q = CDTUCUM.theType.parse("1 m");
        assertTrue(1.0 == q.getValue().doubleValue());
        assertEquals("m", unitFormat.format(q.getUnit()));
    }

    @Test
    public void decimal_value() {
        Quantity<?> q = CDTUCUM.theType.parse("1.5 km");
        assertTrue(1.5 == q.getValue().doubleValue());
        assertEquals("km", unitFormat.format(q.getUnit()));
    }

    @Test
    public void negative_value() {
        Quantity<?> q = CDTUCUM.theType.parse("-1.5 km");
        assertTrue(-1.5 == q.getValue().doubleValue());
        assertEquals("km", unitFormat.format(q.getUnit()));
    }

    @Test
    public void compound_unit_division() {
        Quantity<?> q = CDTUCUM.theType.parse("9.8 m/s2");
        assertTrue(9.8 == q.getValue().doubleValue());
    }

    @Test
    public void compound_unit_dot() {
        Quantity<?> q = CDTUCUM.theType.parse("1 kg.m/s2");
        assertTrue(1.0 == q.getValue().doubleValue());
    }

    @Test
    public void inverse_unit() {
        Quantity<?> q = CDTUCUM.theType.parse("60 s-1");
        assertTrue(60.0 == q.getValue().doubleValue());
    }

    @Test
    public void random_ucum_combination_1() {
        assertTrue(CDTUCUM.theType.isValid("1 s.cd/kg"));
    }

    @Test
    public void random_ucum_combination_2() {
        assertTrue(CDTUCUM.theType.isValid("1 [ly].s/[ft_i]"));
    }

    @Test
    public void random_ucum_combination_3() {
        assertTrue(CDTUCUM.theType.isValid("1 [dr_ap]/[min_us]2.[c]"));
    }

    @Test
    public void compound_constant_unit() {
        assertTrue(CDTUCUM.theType.isValid("1 [pi].[c]/[h]"));
    }

    @Test
    public void temperature_kelvin() {
        Quantity<?> q = CDTUCUM.theType.parse("273.15 K");
        assertTrue(273.15 == q.getValue().doubleValue());
        assertEquals("K", unitFormat.format(q.getUnit()));
    }

    @Test
    public void dimensionless() {
        assertTrue(CDTUCUM.theType.isValid("1 1"));
    }

    @Test
    public void dimensionless_ratio() {
        assertTrue(CDTUCUM.theType.isValid("1.2 m/m"));
    }

    @Test
    public void invalid_ucum_unit_NM() {
        assertFalse(CDTUCUM.theType.isValid("60 NM"));
    }

    @Test
    public void dimensionless_bare_integer() {
        assertTrue(CDTUCUM.theType.isValid("42"));
    }

    @Test
    public void dimensionless_bare_decimal() {
        assertTrue(CDTUCUM.theType.isValid("1.5"));
    }

    @Test
    public void dimensionless_bare_negative() {
        assertTrue(CDTUCUM.theType.isValid("-3.0"));
    }

    // -- Invalid lexical forms --

    @Test
    public void unit_only() {
        assertFalse(CDTUCUM.theType.isValid("km"));
    }

    @Test
    public void empty_string() {
        assertFalse(CDTUCUM.theType.isValid(""));
    }

    @Test
    public void no_space() {
        assertFalse(CDTUCUM.theType.isValid("1km"));
    }

    @Test
    public void whitespace_only() {
        assertFalse(CDTUCUM.theType.isValid("   "));
    }

    @Test
    public void leading_space() {
        assertFalse(CDTUCUM.theType.isValid("  1 m"));
    }

    @Test
    public void trailing_space() {
        assertFalse(CDTUCUM.theType.isValid("1 m  "));
    }

    @Test
    public void leading_and_trailing_space() {
        assertFalse(CDTUCUM.theType.isValid("  1 m  "));
    }

    // -- Registration -----------

    @Test
    public void cdt_ucum_registered() {
        RDFDatatype dt = TypeMapper.getInstance().getTypeByName(CDTDatatype.CDT + "ucum");
        assertNotNull(dt);
        assertTrue(dt instanceof QuantityDatatype);
    }

    @Test
    public void cdt_ucumunit_registered() {
        RDFDatatype dt = TypeMapper.getInstance().getTypeByName(CDTDatatype.CDT + "ucumunit");
        assertNotNull(dt);
        assertTrue(dt instanceof UnitDatatype);
    }

    @Test
    public void ill_typed_literal_invalid() {
        assertFalse(CDTUCUM.theType.isValid("not_a_quantity"));
    }

    @Test
    public void init_idempotent() {
        UCUMConfig.init();
        UCUMConfig.init();
        RDFDatatype dt = TypeMapper.getInstance().getTypeByName(CDTDatatype.CDT + "ucum");
        assertNotNull(dt);
        assertTrue(dt instanceof QuantityDatatype);
    }

    @Test
    public void permeability_of_vacuum() {
        Quantity<?> q = CDTUCUM.theType.parse("70 H/m");
        assertTrue(70.0 == q.getValue().doubleValue());
    }

    @Test
    public void twice_speed_of_light() {
        Quantity<?> q = CDTUCUM.theType.parse("2 [c]");
        assertTrue(2.0 == q.getValue().doubleValue());
    }

    @Test
    public void astronomical_unit() {
        Quantity<?> q = CDTUCUM.theType.parse("1 AU");
        assertTrue(1.0 == q.getValue().doubleValue());
    }

    // -- Scientific notation ----

    @Test
    public void scientific_notation_positive_exponent() {
        Quantity<?> q = CDTUCUM.theType.parse("1.5e3 kg");
        assertEquals(0, new BigDecimal("1500").compareTo(new BigDecimal(q.getValue().toString())));
        assertEquals("kg", unitFormat.format(q.getUnit()));
    }

    @Test
    public void scientific_notation_negative_exponent() {
        Quantity<?> q = CDTUCUM.theType.parse("1e-3 m");
        assertEquals(0, new BigDecimal("0.001").compareTo(new BigDecimal(q.getValue().toString())));
        assertEquals("m", unitFormat.format(q.getUnit()));
    }

    // -- Arbitrary precision -----

    @Test
    public void large_value_beyond_double_range_does_not_overflow() {
        Quantity<?> q = CDTUCUM.theType.parse("1e1000 m");
        assertEquals(0, new BigDecimal("1e1000").compareTo(new BigDecimal(q.getValue().toString())));
    }

    @Test
    public void small_value_beyond_double_range_does_not_underflow() {
        Quantity<?> q = CDTUCUM.theType.parse("5e-1000 m");
        assertEquals(0, new BigDecimal("5e-1000").compareTo(new BigDecimal(q.getValue().toString())));
    }

    @Test
    public void large_integer_329_digits_exact_value() {
        String digits = "12345678901234567890100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
        Quantity<?> q = CDTUCUM.theType.parse(digits + " m");
        assertEquals(0, new BigDecimal(digits).compareTo(new BigDecimal(q.getValue().toString())));
    }

    @Test
    public void kelvin_large_exponent_does_not_overflow() {
        Quantity<?> q = CDTUCUM.theType.parse("273.15e33 K");
        assertEquals(0, new BigDecimal("273.15e33").compareTo(new BigDecimal(q.getValue().toString())));
    }

    @Test
    public void compound_constant_unit_pi_c_h_computes_without_overflow() {
        Quantity<?> q = CDTUCUM.theType.parse("1 [pi].[c]/[h]");
        assertTrue(new BigDecimal(q.getValue().toString()).compareTo(BigDecimal.ZERO) > 0);
    }

    // -- Ill-typed literal -------

    @Test
    public void ill_typed_literal_creation_does_not_throw() {
        LiteralLabel ll = LiteralLabelFactory.create("not_a_quantity", CDTUCUM.theType);
        assertEquals("not_a_quantity", ll.getLexicalForm());
        assertFalse(CDTUCUM.theType.isValid(ll.getLexicalForm()));
    }

    // -- Lexical form preservation --

    @Test
    public void lexical_form_preserves_original_unit() {
        Quantity<?> q = CDTUCUM.theType.parse("1.5 km");
        assertEquals("1.5 km", CDTUCUM.theType.unparse(q));
    }
}