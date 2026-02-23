package org.lindt.ucum.sparql;

import org.apache.jena.sparql.function.FunctionRegistry;
import org.lindt.ucum.sparql.functions.FN_Add;
import org.lindt.ucum.sparql.functions.FN_Convert;
import org.lindt.ucum.sparql.functions.FN_Divide;
import org.lindt.ucum.sparql.functions.FN_Equals;
import org.lindt.ucum.sparql.functions.FN_GetUnit;
import org.lindt.ucum.sparql.functions.FN_GetValue;
import org.lindt.ucum.sparql.functions.FN_GreaterThan;
import org.lindt.ucum.sparql.functions.FN_LessThan;
import org.lindt.ucum.sparql.functions.FN_Multiply;
import org.lindt.ucum.sparql.functions.FN_SameDimension;
import org.lindt.ucum.sparql.functions.FN_Subtract;
import org.lindt.ucum.sparql.functions.FN_ToSI;



/**
 * Registers all UCUM SPARQL functions.
 * 
 * @author irfan
 */
public class UCUMFunctions {

    /**
     * Register all UCUM filter functions with the given FunctionRegistry.
     *
     * @param registry the FunctionRegistry (typically FunctionRegistry.get())
     */
    public static void loadFilterFunctions(FunctionRegistry registry) {

        // Comparison functions 
        registry.put(FN_SameDimension.IRI, FN_SameDimension.class);
        registry.put(FN_Equals.IRI,        FN_Equals.class);
        registry.put(FN_GreaterThan.IRI,   FN_GreaterThan.class);
        registry.put(FN_LessThan.IRI,      FN_LessThan.class);

        //  Arithmetic functions 
        registry.put(FN_Add.IRI,           FN_Add.class);
        registry.put(FN_Subtract.IRI,      FN_Subtract.class);
        registry.put(FN_Multiply.IRI,      FN_Multiply.class);
        registry.put(FN_Divide.IRI,        FN_Divide.class);

        //   Conversion functions                 
        registry.put(FN_Convert.IRI,       FN_Convert.class);
        registry.put(FN_ToSI.IRI,          FN_ToSI.class);

        //   Accessor functions               
        registry.put(FN_GetValue.IRI,      FN_GetValue.class);
        registry.put(FN_GetUnit.IRI,       FN_GetUnit.class);
    }

    /**
     * Register all functions with the global FunctionRegistry.
     * Convenience method for simple setup.
     */
    public static void loadAll() {
        loadFilterFunctions(FunctionRegistry.get());
    }
}