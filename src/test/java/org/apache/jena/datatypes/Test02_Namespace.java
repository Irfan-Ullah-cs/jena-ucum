package org.apache.jena.datatypes;

import static org.junit.Assert.*;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lindt.ucum.UCUMConfig;
import org.lindt.ucum.datatype.CDT;
import org.lindt.ucum.datatype.CDTDatatype;
import org.lindt.ucum.datatype.quantity.CDTUCUM;
import org.lindt.ucum.datatype.quantity.CDTUCUMUnit;

public class Test02_Namespace {

    @BeforeClass
    public static void setup() {
        UCUMConfig.init();
    }

    // -- Base URI ---

    @Test
    public void base_uri() {
        assertEquals("https://w3id.org/cdt/", CDTDatatype.CDT);
    }

    @Test
    public void ucum_uri() {
        assertEquals("https://w3id.org/cdt/ucum", CDTUCUM.theTypeURI);
    }

    @Test
    public void ucumunit_uri() {
        assertEquals("https://w3id.org/cdt/ucumunit", CDTUCUMUnit.theTypeURI);
    }

    // -- Type count -

    @Test
    public void exactly_two_types_registered() {
        RDFDatatype ucum     = TypeMapper.getInstance().getTypeByName(CDTDatatype.CDT + "ucum");
        RDFDatatype ucumunit = TypeMapper.getInstance().getTypeByName(CDTDatatype.CDT + "ucumunit");
        assertNotNull(ucum);
        assertNotNull(ucumunit);

        RDFDatatype other = TypeMapper.getInstance().getTypeByName(CDTDatatype.CDT + "length");
        assertNull(other);
    }

    @Test
    public void ucum_in_type_registry() {
        assertNotNull(TypeMapper.getInstance().getTypeByName(CDTDatatype.CDT + "ucum"));
    }

    @Test
    public void ucumunit_in_type_registry() {
        assertNotNull(TypeMapper.getInstance().getTypeByName(CDTDatatype.CDT + "ucumunit"));
    }

    // -- CDT membership ---------

    @Test
    public void ucum_is_cdt_datatype() {
        assertTrue(isCdtDatatype(CDTDatatype.CDT + "ucum"));
    }

    @Test
    public void ucumunit_is_cdt_datatype() {
        assertTrue(isCdtDatatype(CDTDatatype.CDT + "ucumunit"));
    }

    @Test
    public void xsd_string_is_not_cdt() {
        assertFalse(isCdtDatatype(XSDDatatype.XSDstring.getURI()));
    }

    @Test
    public void xsd_integer_is_not_cdt() {
        assertFalse(isCdtDatatype(XSDDatatype.XSDinteger.getURI()));
    }

    @Test
    public void xsd_double_is_not_cdt() {
        assertFalse(isCdtDatatype(XSDDatatype.XSDdouble.getURI()));
    }

    @Test
    public void null_is_not_cdt() {
        assertFalse(isCdtDatatype(null));
    }

    @Test
    public void arbitrary_uri_is_not_cdt() {
        assertFalse(isCdtDatatype("https://example.org/mytype"));
    }

    // -- CDT resource references ------------

    @Test
    public void cdt_ucum_resource_uri() {
        assertEquals(CDTDatatype.CDT + "ucum", CDT.ucum.getURI());
    }

    @Test
    public void cdt_ucumunit_resource_uri() {
        assertEquals(CDTDatatype.CDT + "ucumunit", CDT.ucumunit.getURI());
    }

    // -- Helper -----

    private boolean isCdtDatatype(String uri) {
        if (uri == null) return false;
        RDFDatatype dt = TypeMapper.getInstance().getTypeByName(uri);
        return dt instanceof CDTDatatype;
    }
}
