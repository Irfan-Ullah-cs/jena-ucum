/*
 * Adapted from the original jena-ucum implementation by Maxime Lefrançois.
 * Moved from org.apache.jena.datatypes.cdt to separate library.
 */
package org.lindt.ucum.datatype;

import org.apache.jena.datatypes.BaseDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.lindt.ucum.datatype.quantity.QuantityDatatype;

public abstract class CDTDatatype extends BaseDatatype {

    /**
     * The cdt namespace
     */
    public static final String CDT = "https://w3id.org/cdt/";

    public static void loadCDTTypes(TypeMapper tm) {
        QuantityDatatype.loadCDTTypes(tm);
    }

    public CDTDatatype(String uri) {
        super(uri);
    }

}
