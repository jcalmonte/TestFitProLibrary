/**
 * Tests the BitFieldId enum and its methods.
 * @author Levi.Balling
 * @date 12/11/13
 * @version 1
 * Tests the enum and all of the different values dealing with the DataConverters also.
 */
package com.ifit.sparky.fecp.tests.interpreter.bitField;

import com.ifit.sparky.fecp.interpreter.bitField.*;
import com.ifit.sparky.fecp.interpreter.bitField.converter.BitfieldDataConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.SpeedConverter;

import junit.framework.TestCase;

import java.util.ArrayList;

public class TestBitFieldId extends TestCase {

    /**
     * Setups the TestRunner for CommandId
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Closes the TestRunner for CommandId
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Runs through the Tests for the Enum values
     * @throws Exception
     */
    public void testEnum() throws Exception{

        BitFieldId idOne;
        BitFieldId idTwo;
        BitfieldDataConverter converterOne;
        ArrayList<Byte> rawData;
        Byte b1;
        Byte b2;

        idOne = BitFieldId.TARGET_MPH;
        idTwo = BitFieldId.TARGET_INCLINE;
        rawData = new ArrayList<Byte>();
        b1 = 0x0B;
        b2 = 0x01;
        rawData.add(b1);
        rawData.add(b2);

        assertEquals(BitFieldId.TARGET_MPH, idOne);
        assertEquals(0, idOne.getVal());
        assertEquals(0, idOne.getSection());
        assertEquals(0, idOne.getBit());
        assertEquals(2, idOne.getSize());
        assertEquals(false, idOne.getReadOnly());

        assertEquals(BitFieldId.TARGET_INCLINE, idTwo);
        assertEquals(2, idTwo.getVal());
        assertEquals(0, idTwo.getSection());
        assertEquals(2, idTwo.getBit());
        assertEquals(1, idTwo.getSize());
        assertEquals(false, idTwo.getReadOnly());

        //test changes in the converter behind the seens to make sure it still matches
        converterOne =idOne.getData(rawData);

        assertEquals(26.7, ((SpeedConverter)converterOne).getSpeed());
        assertEquals(BitFieldId.TARGET_MPH, idOne);

    }

    /**
     * Runs through the Tests for the static Enum function calls
     * @throws Exception
     */
    public void testGetStatic_BitFieldId() throws Exception{
        //Test the static Get BitfieldId from ID
        try
        {
            BitFieldId idOne = BitFieldId.getBitFieldId(0);
            assertEquals(BitFieldId.TARGET_MPH, idOne);
        }
        catch (Exception ex)
        {
            fail();//shouldn't throw an exception
        }

        try
        {
            BitFieldId.getBitFieldId(67000);
            fail();//should throw an exception before here
        }
        catch (InvalidBitFieldException ex)
        {
            assertTrue(true);//this should throw an exception
        }

        //Test the static Get BitfieldId from bit and section
        try
        {
            BitFieldId idOne = BitFieldId.getBitFieldId(0,2);
            assertEquals(BitFieldId.TARGET_INCLINE, idOne);
        }
        catch (Exception ex)
        {
            fail();//shouldn't throw an exception
        }

        try
        {
            BitFieldId.getBitFieldId(50, 3);
            fail();//should throw an exception before here
        }
        catch (InvalidBitFieldException ex)
        {
            assertTrue(true);//this should throw an exception
        }
    }

}