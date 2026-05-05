
package org.lindt.ucum.datatype.quantity;

import javax.measure.Quantity;

/**
 *
 * @author maxime.lefrancois
 */
public class CDTUCUMUnit extends UnitDatatype {

    public static final String theTypeURI = CDT + "ucumunit";
    public static final CDTUCUMUnit theType = new CDTUCUMUnit();

    /**
     * private constructor - single global instance
     */
    private CDTUCUMUnit() {
        super(theTypeURI, Quantity.class);
    }

}
