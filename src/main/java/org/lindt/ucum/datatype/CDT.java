/*
 * Adapted from the original jena-ucum implementation.
 * Moved from org.apache.jena.datatypes.cdt to separate library.
 */
package org.lindt.ucum.datatype;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.lindt.ucum.datatype.quantity.*;

public class CDT {
    /**
     * The namespace of the vocabulary as a string
     */
    public static final String NS = CDTDatatype.CDT;

    /**
     * The RDF-friendly version of the CDT namespace
     */
    public static String getURI() { return NS; }

    /** Resource URI for cdt:ucumunit */
    public static Resource ucumunit;

    /** Resource URI for cdt:ucum */
    public static Resource ucum;

    /** Resource URI for cdt:acceleration */
    public static Resource acceleration;

    /** Resource URI for cdt:amountOfSubstance */
    public static Resource amountOfSubstance;

    /** Resource URI for cdt:angle */
    public static Resource angle;

    /** Resource URI for cdt:area */
    public static Resource area;

    /** Resource URI for cdt:catalyticActivity */
    public static Resource catalyticActivity;

    /** Resource URI for cdt:dimensionless */
    public static Resource dimensionless;

    /** Resource URI for cdt:electricCapacitance */
    public static Resource electricCapacitance;

    /** Resource URI for cdt:electricCharge */
    public static Resource electricCharge;

    /** Resource URI for cdt:electricConductance */
    public static Resource electricConductance;

    /** Resource URI for cdt:electricCurrent */
    public static Resource electricCurrent;

    /** Resource URI for cdt:electricInductance */
    public static Resource electricInductance;

    /** Resource URI for cdt:electricPotential */
    public static Resource electricPotential;

    /** Resource URI for cdt:electricResistance */
    public static Resource electricResistance;

    /** Resource URI for cdt:energy */
    public static Resource energy;

    /** Resource URI for cdt:force */
    public static Resource force;

    /** Resource URI for cdt:frequency */
    public static Resource frequency;

    /** Resource URI for cdt:illuminance */
    public static Resource illuminance;

    /** Resource URI for cdt:length */
    public static Resource length;

    /** Resource URI for cdt:luminousFlux */
    public static Resource luminousFlux;

    /** Resource URI for cdt:luminousIntensity */
    public static Resource luminousIntensity;

    /** Resource URI for cdt:magneticFlux */
    public static Resource magneticFlux;

    /** Resource URI for cdt:magneticFluxDensity */
    public static Resource magneticFluxDensity;

    /** Resource URI for cdt:mass */
    public static Resource mass;

    /** Resource URI for cdt:power */
    public static Resource power;

    /** Resource URI for cdt:pressure */
    public static Resource pressure;

    /** Resource URI for cdt:radiationDoseAbsorbed */
    public static Resource radiationDoseAbsorbed;

    /** Resource URI for cdt:radiationDoseEffective */
    public static Resource radiationDoseEffective;

    /** Resource URI for cdt:radioactivity */
    public static Resource radioactivity;

    /** Resource URI for cdt:solidAngle */
    public static Resource solidAngle;

    /** Resource URI for cdt:speed */
    public static Resource speed;

    /** Resource URI for cdt:temperature */
    public static Resource temperature;

    /** Resource URI for cdt:time */
    public static Resource time;

    /** Resource URI for cdt:volume */
    public static Resource volume;

    // Initializer
    static {
        ucumunit = ResourceFactory.createResource(CDTUCUMUnit.theTypeURI);
        ucum = ResourceFactory.createResource(CDTUCUM.theTypeURI);
        acceleration = ResourceFactory.createResource(CDTAcceleration.theTypeURI);
        amountOfSubstance = ResourceFactory.createResource(CDTAmountOfSubstance.theTypeURI);
        angle = ResourceFactory.createResource(CDTAngle.theTypeURI);
        area = ResourceFactory.createResource(CDTArea.theTypeURI);
        catalyticActivity = ResourceFactory.createResource(CDTCatalyticActivity.theTypeURI);
        dimensionless = ResourceFactory.createResource(CDTDimensionless.theTypeURI);
        electricCapacitance = ResourceFactory.createResource(CDTElectricCapacitance.theTypeURI);
        electricCharge = ResourceFactory.createResource(CDTElectricCharge.theTypeURI);
        electricConductance = ResourceFactory.createResource(CDTElectricConductance.theTypeURI);
        electricCurrent = ResourceFactory.createResource(CDTElectricCurrent.theTypeURI);
        electricInductance = ResourceFactory.createResource(CDTElectricInductance.theTypeURI);
        electricPotential = ResourceFactory.createResource(CDTElectricPotential.theTypeURI);
        electricResistance = ResourceFactory.createResource(CDTElectricResistance.theTypeURI);
        energy = ResourceFactory.createResource(CDTEnergy.theTypeURI);
        force = ResourceFactory.createResource(CDTForce.theTypeURI);
        frequency = ResourceFactory.createResource(CDTFrequency.theTypeURI);
        illuminance = ResourceFactory.createResource(CDTIlluminance.theTypeURI);
        length = ResourceFactory.createResource(CDTLength.theTypeURI);
        luminousFlux = ResourceFactory.createResource(CDTLuminousFlux.theTypeURI);
        luminousIntensity = ResourceFactory.createResource(CDTLuminousIntensity.theTypeURI);
        magneticFlux = ResourceFactory.createResource(CDTMagneticFlux.theTypeURI);
        magneticFluxDensity = ResourceFactory.createResource(CDTMagneticFluxDensity.theTypeURI);
        mass = ResourceFactory.createResource(CDTMass.theTypeURI);
        power = ResourceFactory.createResource(CDTPower.theTypeURI);
        pressure = ResourceFactory.createResource(CDTPressure.theTypeURI);
        radiationDoseAbsorbed = ResourceFactory.createResource(CDTRadiationDoseAbsorbed.theTypeURI);
        radiationDoseEffective = ResourceFactory.createResource(CDTRadiationDoseEffective.theTypeURI);
        radioactivity = ResourceFactory.createResource(CDTRadioactivity.theTypeURI);
        solidAngle = ResourceFactory.createResource(CDTSolidAngle.theTypeURI);
        speed = ResourceFactory.createResource(CDTSpeed.theTypeURI);
        temperature = ResourceFactory.createResource(CDTTemperature.theTypeURI);
        time = ResourceFactory.createResource(CDTTime.theTypeURI);
        volume = ResourceFactory.createResource(CDTVolume.theTypeURI);
    }
}
