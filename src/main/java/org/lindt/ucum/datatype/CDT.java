package org.lindt.ucum.datatype;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.lindt.ucum.datatype.quantity.CDTUCUM;
import org.lindt.ucum.datatype.quantity.CDTUCUMUnit;

public class CDT {

    public static final String NS = CDTDatatype.CDT;

    public static String getURI() { return NS; }

    public static Resource ucum;
    public static Resource ucumunit;

    static {
        ucum    = ResourceFactory.createResource(CDTUCUM.theTypeURI);
        ucumunit = ResourceFactory.createResource(CDTUCUMUnit.theTypeURI);
    }
}