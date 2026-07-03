package org.apache.jena.datatypes;

import static org.junit.Assert.*;

import org.apache.jena.graph.impl.LiteralLabel;
import org.apache.jena.graph.impl.LiteralLabelFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lindt.ucum.UCUMConfig;
import org.lindt.ucum.datatype.quantity.CDTUCUM;

public class Test03_Equality {

    @BeforeClass
    public static void setup() {
        UCUMConfig.init();
    }

    // -- Same unit --

    @Test
    public void same_unit_same_value() {
        LiteralLabel a = LiteralLabelFactory.create("1000 m", CDTUCUM.theType);
        LiteralLabel b = LiteralLabelFactory.create("1000 m", CDTUCUM.theType);
        assertTrue(CDTUCUM.theType.isEqual(a, b));
    }

    @Test
    public void same_unit_different_value() {
        LiteralLabel a = LiteralLabelFactory.create("1 km", CDTUCUM.theType);
        LiteralLabel b = LiteralLabelFactory.create("500 m", CDTUCUM.theType);
        assertFalse(CDTUCUM.theType.isEqual(a, b));
    }

    // -- Cross-unit -

    @Test
    public void cross_unit_length() {
        LiteralLabel a = LiteralLabelFactory.create("1 km", CDTUCUM.theType);
        LiteralLabel b = LiteralLabelFactory.create("1000 m", CDTUCUM.theType);
        assertTrue(CDTUCUM.theType.isEqual(a, b));
    }

    @Test
    public void cross_unit_time() {
        LiteralLabel a = LiteralLabelFactory.create("1 h", CDTUCUM.theType);
        LiteralLabel b = LiteralLabelFactory.create("3600 s", CDTUCUM.theType);
        assertTrue(CDTUCUM.theType.isEqual(a, b));
    }

    @Test
    public void cross_unit_mass() {
        LiteralLabel a = LiteralLabelFactory.create("70 kg", CDTUCUM.theType);
        LiteralLabel b = LiteralLabelFactory.create("70000 g", CDTUCUM.theType);
        assertTrue(CDTUCUM.theType.isEqual(a, b));
    }

    @Test
    public void cross_unit_pressure() {
        LiteralLabel a = LiteralLabelFactory.create("1 kPa", CDTUCUM.theType);
        LiteralLabel b = LiteralLabelFactory.create("1000 Pa", CDTUCUM.theType);
        assertTrue(CDTUCUM.theType.isEqual(a, b));
    }

    @Test
    public void cross_unit_speed() {
        LiteralLabel a = LiteralLabelFactory.create("3.6 km/h", CDTUCUM.theType);
        LiteralLabel b = LiteralLabelFactory.create("1 m/s", CDTUCUM.theType);
        assertTrue(CDTUCUM.theType.isEqual(a, b));
    }

    @Test
    public void cross_unit_temperature_celsius_kelvin() {
        LiteralLabel a = LiteralLabelFactory.create("0 Cel", CDTUCUM.theType);
        LiteralLabel b = LiteralLabelFactory.create("273.15 K", CDTUCUM.theType);
        assertTrue(CDTUCUM.theType.isEqual(a, b));
    }

    // -- Incompatible dimensions ------------

    @Test
    public void incompatible_dimensions_length_vs_mass() {
        LiteralLabel a = LiteralLabelFactory.create("1 m", CDTUCUM.theType);
        LiteralLabel b = LiteralLabelFactory.create("1 kg", CDTUCUM.theType);
        assertFalse(CDTUCUM.theType.isEqual(a, b));
    }

    @Test
    public void incompatible_dimensions_time_vs_length() {
        LiteralLabel a = LiteralLabelFactory.create("1 s", CDTUCUM.theType);
        LiteralLabel b = LiteralLabelFactory.create("1 m", CDTUCUM.theType);
        assertFalse(CDTUCUM.theType.isEqual(a, b));
    }

    // -- Derived units ----------

    @Test
    public void newton_equals_kg_m_per_s2() {
        LiteralLabel a = LiteralLabelFactory.create("1 N", CDTUCUM.theType);
        LiteralLabel b = LiteralLabelFactory.create("1 kg.m/s2", CDTUCUM.theType);
        assertTrue(CDTUCUM.theType.isEqual(a, b));
    }

    @Test
    public void joule_equals_newton_meter() {
        LiteralLabel a = LiteralLabelFactory.create("1 J", CDTUCUM.theType);
        LiteralLabel b = LiteralLabelFactory.create("1 N.m", CDTUCUM.theType);
        assertTrue(CDTUCUM.theType.isEqual(a, b));
    }

    @Test
    public void watt_equals_joule_per_second() {
        LiteralLabel a = LiteralLabelFactory.create("1 W", CDTUCUM.theType);
        LiteralLabel b = LiteralLabelFactory.create("1 J/s", CDTUCUM.theType);
        assertTrue(CDTUCUM.theType.isEqual(a, b));
    }

    @Test
    public void pascal_equals_kg_per_m_s2() {
        LiteralLabel a = LiteralLabelFactory.create("1 Pa", CDTUCUM.theType);
        LiteralLabel b = LiteralLabelFactory.create("1 kg/(m.s2)", CDTUCUM.theType);
        assertTrue(CDTUCUM.theType.isEqual(a, b));
    }

    @Test
    public void hertz_equals_inverse_second() {
        LiteralLabel a = LiteralLabelFactory.create("1 Hz", CDTUCUM.theType);
        LiteralLabel b = LiteralLabelFactory.create("1 s-1", CDTUCUM.theType);
        assertTrue(CDTUCUM.theType.isEqual(a, b));
    }

    // -- Special cases ----------

    @Test
    public void speed_of_light() {
        LiteralLabel a = LiteralLabelFactory.create("299792458 m/s", CDTUCUM.theType);
        LiteralLabel b = LiteralLabelFactory.create("1 [c]", CDTUCUM.theType);
        assertTrue(CDTUCUM.theType.isEqual(a, b));
    }

    @Test
    public void unit_order_irrelevant() {
        LiteralLabel a = LiteralLabelFactory.create("1 kg.m/s2", CDTUCUM.theType);
        LiteralLabel b = LiteralLabelFactory.create("1 s-2.m.kg", CDTUCUM.theType);
        assertTrue(CDTUCUM.theType.isEqual(a, b));
    }

    @Test
    public void dimensionless_ratio() {
        LiteralLabel a = LiteralLabelFactory.create("50 %", CDTUCUM.theType);
        LiteralLabel b = LiteralLabelFactory.create("0.5 1", CDTUCUM.theType);
        assertTrue(CDTUCUM.theType.isEqual(a, b));
    }
}