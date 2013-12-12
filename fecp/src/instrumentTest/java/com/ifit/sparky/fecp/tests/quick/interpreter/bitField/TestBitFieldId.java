/**
 * Tests the BitFieldId enum and its methods.
 * @author Levi.Balling
 * @date 12/11/13
 * @version 1
 * Tests the enum and all of the different values dealing with the DataConverters also.
 * This is a Brute Force test, and should only be ran over night and before release.
 */
package com.ifit.sparky.fecp.tests.quick.interpreter.bitField;

import com.ifit.sparky.fecp.interpreter.bitField.*;
import com.ifit.sparky.fecp.interpreter.bitField.converter.*;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
        ByteBuffer rawData = ByteBuffer.allocate(2);
        rawData.order(ByteOrder.LITTLE_ENDIAN);
        Byte b1;
        Byte b2;

        idOne = BitFieldId.TARGET_MPH;
        idTwo = BitFieldId.TARGET_INCLINE;
        b1 = 0x0B;
        b2 = 0x01;
        rawData.put(b1);
        rawData.put(b2);

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
        assertEquals(2, idTwo.getSize());
        assertEquals(false, idTwo.getReadOnly());

        //test changes in the converter behind the seens to make sure it still matches
        converterOne =idOne.getData(rawData);

        assertEquals(26.7, ((SpeedConverter)converterOne).getSpeed());
        assertEquals(BitFieldId.TARGET_MPH, idOne);

    }

    /**
     * Test the get data aspect of the Enum
     * @throws Exception
     */
    public void testConverterGetData_BitfieldId() throws Exception
    {

        BitFieldId idOne;
        BitfieldDataConverter converter;
        ByteBuffer buff = ByteBuffer.allocate(2);
        buff.order(ByteOrder.LITTLE_ENDIAN);

        //test Speed Converter
        idOne = BitFieldId.TARGET_MPH;

        // Test all unsigned short values
        for(int i = 0; i < 65536; i++)
        {
            double expectResult;
            buff.clear();
            buff.putShort((short)i);
            expectResult = (i + 0.0) / 10;
            converter = idOne.getData(buff);
            assertEquals(SpeedConverter.class, converter.getClass());//should be the same class
            assertEquals(expectResult, ((SpeedConverter)converter).getSpeed());
        }

        //test Incline Converter
        idOne = BitFieldId.TARGET_INCLINE;

        // Test all unsigned short values
        for(int i = Short.MIN_VALUE; i < Short.MAX_VALUE; i++)
        {
            double expectResult;
            buff.clear();
            buff.putShort((short) i);
            expectResult = (i + 0.0) / 100;
            converter = idOne.getData(buff);
            assertEquals(InclineConverter.class, converter.getClass());//should be the same class
            assertEquals(expectResult, ((InclineConverter)converter).getIncline());
        }

        //test Byte Converter with int inputs
        idOne = BitFieldId.TARGET_FAN_SPEED;
        buff = ByteBuffer.allocate(1);

        buff.order(ByteOrder.LITTLE_ENDIAN);

        // Test all unsigned short values
        for(int i = Byte.MIN_VALUE; i < Byte.MAX_VALUE; i++)
        {
            buff.clear();
            buff.put((byte) i);
            converter = idOne.getData(buff);
            assertEquals(ByteConverter.class, converter.getClass());//should be the same class
            assertEquals((int)((byte)i & 0xFF), ((ByteConverter)converter).getValue());
        }

        //test Byte Converter with int inputs we want this to be unsigned
        idOne = BitFieldId.TARGET_WATTS;
        buff = ByteBuffer.allocate(2);
        buff.order(ByteOrder.LITTLE_ENDIAN);

        // Test all unsigned short values
        for(int i = Short.MIN_VALUE; i < Short.MAX_VALUE; i++)
        {
            buff.clear();
            buff.putShort((short) i);
            converter = idOne.getData(buff);
            assertEquals(ShortConverter.class, converter.getClass());//should be the same class
            assertEquals((int)((short)i & 0xFFFF), ((ShortConverter)converter).getValue());
        }

        //test Byte Converter with int inputs
        idOne = BitFieldId.CURRENT_KEYCODE;
        buff = ByteBuffer.allocate(4);
        buff.order(ByteOrder.LITTLE_ENDIAN);

//        // Test all unsigned short values
//        for(long i = Integer.MIN_VALUE; i < Integer.MAX_VALUE; i++)
//        {
//            buff.clear();
//            buff.putInt((int)i);
//            converter = idOne.getData(buff);
//            assertEquals(LongConverter.class, converter.getClass());//should be the same class
//            assertEquals((int)((int)i & 0xFFFFFFFF), ((LongConverter)converter).getValue());
//        }

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