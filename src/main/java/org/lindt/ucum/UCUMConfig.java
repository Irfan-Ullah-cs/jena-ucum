package org.lindt.ucum;

import org.apache.jena.datatypes.TypeMapper;
import org.lindt.ucum.datatype.CDTDatatype;
import org.lindt.ucum.sparql.UCUMFunctions;

public class UCUMConfig {

    private static boolean initialized = false;

    public static synchronized void init() {
        if (initialized) return;

        CDTDatatype.loadCDTTypes(TypeMapper.getInstance());
        UCUMFunctions.loadAll();

        initialized = true;
    }
}