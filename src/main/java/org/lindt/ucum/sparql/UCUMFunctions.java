package org.lindt.ucum.sparql;

import org.apache.jena.sparql.function.FunctionRegistry;
import org.lindt.ucum.sparql.functions.FN_SameDimension;

public class UCUMFunctions {

    public static void loadFilterFunctions(FunctionRegistry registry) {
        registry.put(FN_SameDimension.IRI, FN_SameDimension.class);
    }

    public static void loadAll() {
        loadFilterFunctions(FunctionRegistry.get());
    }
}