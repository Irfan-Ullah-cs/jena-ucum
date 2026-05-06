package org.apache.jena.datatypes;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lindt.ucum.UCUMConfig;
import org.lindt.ucum.UCUMOperations;
import org.lindt.ucum.datatype.quantity.CDTUCUM;

public class Test05_UCUMOperations {

    @BeforeClass
    public static void setup() {
        UCUMConfig.init();
    }

    private static Literal lit(String lexical) {
        return ResourceFactory.createTypedLiteral(lexical, CDTUCUM.theType);
    }

    private static BigDecimal val(Literal result, String unit) {
        return UCUMOperations.getValue(result, unit);
    }

    // -- add ----------------------------------------------------------------

    @Test
    public void add_same_unit() {
        Literal result = UCUMOperations.add(lit("5 km"), lit("3 km"));
        assertEquals(CDTUCUM.theTypeURI, result.getDatatypeURI());
        assertEquals(0, val(result, "km").compareTo(new BigDecimal("8")));
        assertEquals("km", UCUMOperations.getUnit(result));
    }

    @Test
    public void add_cross_unit_result_in_left_unit() {
        Literal result = UCUMOperations.add(lit("5 km"), lit("200 m"));
        assertEquals(0, val(result, "km").compareTo(new BigDecimal("5.2")));
        assertEquals("km", UCUMOperations.getUnit(result));
    }

    @Test
    public void add_mass_cross_unit() {
        Literal result = UCUMOperations.add(lit("1 kg"), lit("500 g"));
        assertEquals(0, val(result, "kg").compareTo(new BigDecimal("1.5")));
        assertEquals("kg", UCUMOperations.getUnit(result));
    }

    @Test
    public void add_time_cross_unit() {
        Literal result = UCUMOperations.add(lit("1 h"), lit("30 min"));
        assertEquals(0, val(result, "h").compareTo(new BigDecimal("1.5")));
        assertEquals("h", UCUMOperations.getUnit(result));
    }

    @Test
    public void add_energy_cross_unit() {
        Literal result = UCUMOperations.add(lit("1 kJ"), lit("500 J"));
        assertEquals(0, val(result, "kJ").compareTo(new BigDecimal("1.5")));
        assertEquals("kJ", UCUMOperations.getUnit(result));
    }

    @Test
    public void add_complex_compound_force() {
        Literal result = UCUMOperations.add(lit("10 N"), lit("5 kg.m/s2"));
        assertEquals(0, val(result, "N").compareTo(new BigDecimal("15")));
    }

    @Test(expected = Exception.class)
    public void add_incompatible_throws() {
        UCUMOperations.add(lit("1 m"), lit("1 kg"));
    }

    // -- subtract -----------------------------------------------------------

    @Test
    public void subtract_same_unit() {
        Literal result = UCUMOperations.subtract(lit("5 km"), lit("3 km"));
        assertEquals(0, val(result, "km").compareTo(new BigDecimal("2")));
        assertEquals("km", UCUMOperations.getUnit(result));
    }

    @Test
    public void subtract_cross_unit() {
        Literal result = UCUMOperations.subtract(lit("5 km"), lit("200 m"));
        assertEquals(0, val(result, "km").compareTo(new BigDecimal("4.8")));
    }

    @Test
    public void subtract_zero_result() {
        Literal result = UCUMOperations.subtract(lit("1 km"), lit("1000 m"));
        assertEquals(0, val(result, "m").compareTo(BigDecimal.ZERO));
    }

    @Test
    public void subtract_negative_result() {
        Literal result = UCUMOperations.subtract(lit("200 m"), lit("1 km"));
        assertEquals(0, val(result, "m").compareTo(new BigDecimal("-800")));
        assertEquals("m", UCUMOperations.getUnit(result));
    }

    @Test(expected = Exception.class)
    public void subtract_incompatible_throws() {
        UCUMOperations.subtract(lit("1 m"), lit("1 s"));
    }

    // -- multiply / divide Literal × scalar ---------------------------------

    @Test
    public void multiply_by_int() {
        Literal result = UCUMOperations.multiply(lit("5 km"), 3);
        assertEquals(0, val(result, "km").compareTo(new BigDecimal("15")));
        assertEquals("km", UCUMOperations.getUnit(result));
    }

    @Test
    public void multiply_by_float() {
        Literal result = UCUMOperations.multiply(lit("2 km"), 0.5);
        assertEquals(0, val(result, "km").compareTo(new BigDecimal("1")));
    }

    @Test
    public void divide_by_int() {
        Literal result = UCUMOperations.divide(lit("10 km"), 2);
        assertEquals(0, val(result, "km").compareTo(new BigDecimal("5")));
        assertEquals("km", UCUMOperations.getUnit(result));
    }

    @Test
    public void divide_by_float() {
        Literal result = UCUMOperations.divide(lit("1 km"), 0.5);
        assertEquals(0, val(result, "km").compareTo(new BigDecimal("2")));
    }

    // -- multiply / divide Literal × Literal (dimension-changing) ----------

    @Test
    public void multiply_area_from_length_times_length() {
        Literal result = UCUMOperations.multiply(lit("3 m"), lit("4 m"));
        assertEquals(CDTUCUM.theTypeURI, result.getDatatypeURI());
        assertEquals(0, val(result, "m2").compareTo(new BigDecimal("12")));
    }

    @Test
    public void divide_velocity_from_length_div_time() {
        Literal result = UCUMOperations.divide(lit("100 m"), lit("10 s"));
        assertEquals(0, val(result, "m/s").compareTo(new BigDecimal("10")));
        assertEquals("m/s", UCUMOperations.getUnit(result));
    }

    @Test
    public void multiply_force_equals_newton() {
        Literal result = UCUMOperations.multiply(lit("2 kg"), lit("3 m/s2"));
        assertEquals(0, val(result, "kg.m/s2").compareTo(new BigDecimal("6")));
        assertTrue(UCUMOperations.equals(result, lit("6 N")));
    }

    @Test
    public void multiply_energy_equals_joule() {
        Literal result = UCUMOperations.multiply(lit("10 N"), lit("5 m"));
        assertEquals(0, val(result, "N.m").compareTo(new BigDecimal("50")));
        assertTrue(UCUMOperations.equals(result, lit("50 J")));
    }

    @Test
    public void divide_power_equals_watt() {
        Literal result = UCUMOperations.divide(lit("100 J"), lit("10 s"));
        assertEquals(0, val(result, "J/s").compareTo(new BigDecimal("10")));
        assertTrue(UCUMOperations.equals(result, lit("10 W")));
    }

    @Test
    public void divide_pressure_equals_pascal() {
        Literal result = UCUMOperations.divide(lit("10 N"), lit("2 m2"));
        assertEquals(0, val(result, "N/m2").compareTo(new BigDecimal("5")));
        assertTrue(UCUMOperations.equals(result, lit("5 Pa")));
    }

    @Test
    public void divide_frequency_from_reciprocal_time() {
        Literal result = UCUMOperations.divide(lit("1 1"), lit("0.01 s"));
        assertEquals(0, val(result, "s-1").compareTo(new BigDecimal("100")));
        assertTrue(UCUMOperations.equals(result, lit("100 Hz")));
    }

    @Test
    public void divide_dimensionless_from_same_unit() {
        Literal result = UCUMOperations.divide(lit("5 m"), lit("5 m"));
        assertEquals(0, val(result, "1").compareTo(new BigDecimal("1")));
    }

    @Test
    public void divide_dimensionless_from_mass() {
        Literal result = UCUMOperations.divide(lit("2 kg"), lit("1 kg"));
        assertEquals(0, val(result, "1").compareTo(new BigDecimal("2")));
    }

    @Test
    public void multiply_electric_power() {
        Literal result = UCUMOperations.multiply(lit("10 V"), lit("2 A"));
        assertEquals(0, val(result, "V.A").compareTo(new BigDecimal("20")));
        assertTrue(UCUMOperations.equals(result, lit("20 W")));
    }

    @Test
    public void divide_ohms_law() {
        Literal result = UCUMOperations.divide(lit("10 V"), lit("2 A"));
        assertEquals(0, val(result, "V/A").compareTo(new BigDecimal("5")));
        assertTrue(UCUMOperations.equals(result, lit("5 Ohm")));
    }

    // -- convert ------------------------------------------------------------

    @Test
    public void convert_km_to_m() {
        Literal result = UCUMOperations.convert(lit("1 km"), "m");
        assertEquals(0, val(result, "m").compareTo(new BigDecimal("1000")));
        assertEquals("m", UCUMOperations.getUnit(result));
    }

    @Test
    public void convert_m_to_km() {
        Literal result = UCUMOperations.convert(lit("1000 m"), "km");
        assertEquals(0, val(result, "km").compareTo(new BigDecimal("1")));
    }

    @Test
    public void convert_g_to_kg() {
        Literal result = UCUMOperations.convert(lit("500 g"), "kg");
        assertEquals(0, val(result, "kg").compareTo(new BigDecimal("0.5")));
    }

    @Test
    public void convert_h_to_s() {
        Literal result = UCUMOperations.convert(lit("1 h"), "s");
        assertEquals(0, val(result, "s").compareTo(new BigDecimal("3600")));
    }

    @Test
    public void convert_min_to_s() {
        Literal result = UCUMOperations.convert(lit("1 min"), "s");
        assertEquals(0, val(result, "s").compareTo(new BigDecimal("60")));
    }

    @Test
    public void convert_MHz_to_Hz() {
        Literal result = UCUMOperations.convert(lit("1 MHz"), "Hz");
        assertEquals(0, val(result, "Hz").compareTo(new BigDecimal("1000000")));
    }

    @Test
    public void convert_mV_to_V() {
        Literal result = UCUMOperations.convert(lit("1000 mV"), "V");
        assertEquals(0, val(result, "V").compareTo(new BigDecimal("1")));
    }

    @Test
    public void convert_N_to_kg_m_s2() {
        Literal result = UCUMOperations.convert(lit("1 N"), "kg.m/s2");
        assertEquals(0, val(result, "kg.m/s2").compareTo(new BigDecimal("1")));
    }

    @Test(expected = Exception.class)
    public void convert_incompatible_length_to_mass_throws() {
        UCUMOperations.convert(lit("1 m"), "kg");
    }

    @Test(expected = Exception.class)
    public void convert_incompatible_time_to_length_throws() {
        UCUMOperations.convert(lit("1 s"), "m");
    }

    // -- equals -------------------------------------------------------------

    @Test
    public void equals_same_unit_same_value() {
        assertTrue(UCUMOperations.equals(lit("1000 m"), lit("1 km")));
    }

    @Test
    public void equals_cross_unit_time() {
        assertTrue(UCUMOperations.equals(lit("1 h"), lit("3600 s")));
    }

    @Test
    public void equals_cross_unit_pressure() {
        assertTrue(UCUMOperations.equals(lit("1 kPa"), lit("1000 Pa")));
    }

    @Test
    public void equals_cross_unit_speed() {
        assertTrue(UCUMOperations.equals(lit("3.6 km/h"), lit("1 m/s")));
    }

    @Test
    public void equals_different_values_false() {
        assertFalse(UCUMOperations.equals(lit("1 km"), lit("500 m")));
    }

    @Test
    public void equals_incompatible_dimensions_false() {
        assertFalse(UCUMOperations.equals(lit("1 m"), lit("1 kg")));
    }

    @Test
    public void equals_derived_newton() {
        assertTrue(UCUMOperations.equals(lit("1 N"), lit("1 kg.m/s2")));
    }

    @Test
    public void equals_joule_equals_newton_meter() {
        assertTrue(UCUMOperations.equals(lit("1 J"), lit("1 N.m")));
    }

    @Test
    public void equals_watt_equals_joule_per_second() {
        assertTrue(UCUMOperations.equals(lit("1 W"), lit("1 J/s")));
    }

    @Test
    public void equals_hertz_equals_inverse_second() {
        assertTrue(UCUMOperations.equals(lit("1 Hz"), lit("1 s-1")));
    }

    @Test
    public void equals_speed_of_light() {
        assertTrue(UCUMOperations.equals(lit("299792458 m/s"), lit("1 [c]")));
    }

    @Test
    public void equals_unit_order_irrelevant() {
        assertTrue(UCUMOperations.equals(lit("1 kg.m/s2"), lit("1 s-2.m.kg")));
    }

    // -- compare ------------------------------------------------------------

    @Test
    public void compare_lt_same_unit() {
        assertTrue(UCUMOperations.compare(lit("500 m"), lit("1000 m")) < 0);
    }

    @Test
    public void compare_lt_mass_cross_unit() {
        assertTrue(UCUMOperations.compare(lit("500 g"), lit("1 kg")) < 0);
    }

    @Test
    public void compare_lt_time_cross_unit() {
        assertTrue(UCUMOperations.compare(lit("59 min"), lit("1 h")) < 0);
    }

    @Test
    public void compare_lt_energy() {
        assertTrue(UCUMOperations.compare(lit("1 eV"), lit("1 J")) < 0);
    }

    @Test
    public void compare_false_when_greater() {
        assertFalse(UCUMOperations.compare(lit("1 km"), lit("500 m")) < 0);
    }

    @Test
    public void compare_equal_cross_unit() {
        assertEquals(0, UCUMOperations.compare(lit("1 km"), lit("1000 m")));
    }

    @Test
    public void compare_gt_mass() {
        assertTrue(UCUMOperations.compare(lit("1 kg"), lit("999 g")) > 0);
    }

    @Test
    public void compare_gt_speed() {
        assertTrue(UCUMOperations.compare(lit("10 m/s"), lit("30 km/h")) > 0);
    }

    @Test
    public void compare_gt_acceleration() {
        assertTrue(UCUMOperations.compare(lit("9.81 m/s2"), lit("9.0 m/s2")) > 0);
    }

    @Test
    public void compare_gt_derived_newton() {
        assertTrue(UCUMOperations.compare(lit("2 N"), lit("1 kg.m/s2")) > 0);
    }

    @Test
    public void compare_le_equal_cross_unit() {
        assertTrue(UCUMOperations.compare(lit("1000 m"), lit("1 km")) <= 0);
    }

    @Test
    public void compare_ge_equal_cross_unit() {
        assertTrue(UCUMOperations.compare(lit("1 km"), lit("1000 m")) >= 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void compare_incompatible_throws() {
        UCUMOperations.compare(lit("1 m"), lit("1 kg"));
    }

    // -- sameDimension ------------------------------------------------------

    @Test
    public void sameDimension_true_length() {
        assertTrue(UCUMOperations.sameDimension(lit("1 km"), lit("500 m")));
    }

    @Test
    public void sameDimension_true_derived() {
        assertTrue(UCUMOperations.sameDimension(lit("1 N"), lit("1 kg.m/s2")));
    }

    @Test
    public void sameDimension_false_length_vs_mass() {
        assertFalse(UCUMOperations.sameDimension(lit("1 km"), lit("1 kg")));
    }

    @Test
    public void sameDimension_false_length_vs_time() {
        assertFalse(UCUMOperations.sameDimension(lit("1 m"), lit("1 s")));
    }

    @Test
    public void sameDimension_true_but_not_equal() {
        assertTrue(UCUMOperations.sameDimension(lit("1 km"), lit("500 m")));
        assertFalse(UCUMOperations.equals(lit("1 km"), lit("500 m")));
    }
}