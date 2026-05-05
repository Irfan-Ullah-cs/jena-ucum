package org.lindt.ucum.datatype.quantity;

import javax.measure.Quantity;

/**
 *
 * @author maxime.lefrancois
 */
public class CDTUCUM extends QuantityDatatype {

    public static final String theTypeURI = CDT + "ucum";
    public static final CDTUCUM theType = new CDTUCUM();

    /**
     * private constructor - single global instance
     */
    private CDTUCUM() {
        super(theTypeURI, Quantity.class);
    }

}
