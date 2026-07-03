package org.apache.jena.datatypes;

import static org.junit.Assert.*;

import org.apache.jena.graph.impl.LiteralLabel;
import org.apache.jena.graph.impl.LiteralLabelFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lindt.ucum.UCUMConfig;
import org.lindt.ucum.datatype.quantity.CDTUCUM;

public class Test04_Comparison {

    @BeforeClass
    public static void setup() {
        UCUMConfig.init();
    }

    // -- Less than --

    @Test
    public void lt_same_unit() {
        assertTrue(compare("500 m", "1000 m") < 0);
    }

    @Test
    public void lt_mass_cross_unit() {
        assertTrue(compare("500 g", "1 kg") < 0);
    }

    @Test
    public void lt_time_cross_unit() {
        assertTrue(compare("59 min", "1 h") < 0);
    }

    @Test
    public void lt_energy() {
        assertTrue(compare("1 eV", "1 J") < 0);
    }

    @Test
    public void lt_false_when_greater() {
        assertFalse(compare("1 km", "500 m") < 0);
    }

    @Test
    public void lt_false_when_equal() {
        assertFalse(compare("1 km", "1000 m") < 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void lt_incompatible_dimensions_throws() {
        compare("1 m", "1 kg");
    }

    @Test
    public void lt_large_value_300_digits() {
        String small = "1" + "0".repeat(600) + " m";
        String large = "1" + "0".repeat(600) + "1 m";
        assertTrue(compare(small, large) < 0);
    }

    @Test
    public void lt_compound_unit() {
        assertTrue(compare("500 g.m/s2", "1 kg.m/s2") < 0);
    }

    // -- Greater than -----------

    @Test
    public void gt_mass() {
        assertTrue(compare("1 kg", "999 g") > 0);
    }

    @Test
    public void gt_speed() {
        assertTrue(compare("10 m/s", "30 km/h") > 0);
    }

    @Test
    public void gt_acceleration() {
        assertTrue(compare("9.81 m/s2", "9.0 m/s2") > 0);
    }

    @Test
    public void gt_false_when_equal() {
        assertFalse(compare("1000 m", "1 km") > 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void gt_incompatible_dimensions_throws() {
        compare("1 m", "1 s");
    }

    @Test
    public void gt_derived_newton_vs_force() {
        assertTrue(compare("2 N", "1 kg.m/s2") > 0);
    }

    // -- Less than or equal -----

    @Test
    public void le_equal_cross_unit() {
        assertTrue(compare("1000 m", "1 km") <= 0);
    }

    @Test
    public void le_false_when_greater() {
        assertFalse(compare("2 km", "1000 m") <= 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void le_incompatible_dimensions_throws() {
        compare("1 m", "1 kg");
    }

    // -- Greater than or equal --

    @Test
    public void ge_equal_cross_unit() {
        assertTrue(compare("1 km", "1000 m") >= 0);
    }

    @Test
    public void ge_false_when_less() {
        assertFalse(compare("500 m", "1 km") >= 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ge_incompatible_dimensions_throws() {
        compare("1 m", "1 s");
    }

    // -- Compound and derived units ---------


    @Test
    public void compound_force() {
        assertTrue(compare("10 kg.m/s2", "5 N") > 0);
    }

    @Test
    public void compound_pressure() {
        assertTrue(compare("101325 Pa", "1 bar") > 0);
    }

    @Test
    public void inverse_unit_frequency() {
        assertTrue(compare("100 s-1", "10 Hz") > 0);
    }

    @Test
    public void energy_cross_type() {
        assertTrue(compare("1 kJ", "1 J") > 0);
    }

    // -- Large values (beyond double range) -----------

    @Test
    public void large_value_comparison() {
        assertTrue(compare("1E808 m", "1E307 m") > 0);
    }

    @Test
    public void large_value_cross_unit() {
        assertEquals(0, compare("1E700 km", "1E703 m"));
    }

    @Test
    public void sixteen_significant_digits_are_distinct() {
        assertFalse(CDTUCUM.theType.isEqual(
                LiteralLabelFactory.create("1.000000000000000003 m", CDTUCUM.theType),
                LiteralLabelFactory.create("1.000000000000000002 m", CDTUCUM.theType)));
    }

    @Test
    public void small_value_comparison() {
        assertTrue(compare("1e-20 m", "1e-19 m") < 0);
    }

    // -- Dimensionless ----------

    @Test
    public void dimensionless_comparison() {
        assertTrue(compare("0.5 1", "1 1") < 0);
    }

    // -- Temperature ------------

    @Test
    public void kelvin_comparison() {
        assertTrue(compare("300 K", "273.15 K") > 0);
    }

    @Test
    public void kelvin_vs_celsius_greater() {
        assertTrue(compare("300 K", "0 Cel") > 0);
    }

    @Test
    public void kelvin_vs_celsius_less() {
        assertTrue(compare("200 K", "0 Cel") < 0);
    }

    // -- Helper -----

    private int compare(String lex1, String lex2) {
        LiteralLabel a = LiteralLabelFactory.create(lex1, CDTUCUM.theType);
        LiteralLabel b = LiteralLabelFactory.create(lex2, CDTUCUM.theType);
        return CDTUCUM.theType.compare(a, b);
    }
}
