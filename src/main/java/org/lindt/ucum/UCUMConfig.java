package org.lindt.ucum;

import org.apache.jena.datatypes.TypeMapper;
import org.lindt.ucum.datatype.CDTDatatype;
import org.lindt.ucum.sparql.UCUMFunctions;


public class UCUMConfig {

    private static boolean initialized = false;

    /**
     * Call this once to register all UCUM datatypes and SPARQL functions with Jena.
     * Safe to call multiple times — only runs once.
     *
     * After calling this, SPARQL queries can use:
     *   PREFIX cdt: <https://w3id.org/cdt/>
     *   FILTER(cdt:greaterThan(?h, "100 m"^^cdt:length))
     *   BIND(cdt:add(?h1, ?h2) AS ?total)
     *   ORDER BY cdt:toSI(?weight)
     */
    public static synchronized void init() {
        if (initialized) return;

        System.out.println("[jena-ucum] Registering UCUM datatypes...");

        // Phase 1: Register all 33+ CDT datatypes with TypeMapper
        CDTDatatype.loadCDTTypes(TypeMapper.getInstance());

        // Phase 2: Register all SPARQL filter functions with FunctionRegistry
        UCUMFunctions.loadAll();

        initialized = true;
        System.out.println("[jena-ucum] Initialization complete - datatypes + SPARQL functions registered.");
    }
}
