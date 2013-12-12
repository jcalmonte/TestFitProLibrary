/**
 * Tests the BitFieldId enum and its methods.
 * @author Levi.Balling
 * @date 12/11/13
 * @version 1
 * Tests the enum and all of the different values dealing with the DataConverters also.
 * This is a Brute Force test, and should only be ran over night and before release.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.bitField;

import android.widget.TableRow;

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
     * Test all the different enums to make sure they work with there values
     * @throws Exception
     */
    public void testEachEnumValues_BitFieldId() throws Exception
    {
        BitFieldId bit;
        ByteBuffer buff1;
        ByteBuffer buff2;
        ByteBuffer buff4;
        ByteBuffer resultBuff1;
        ByteBuffer resultBuff2;
        ByteBuffer resultBuff4;
        int intValue = 5;
        double doubleValue = 5.0;
        buff1 = ByteBuffer.allocate(1);
        buff1.order(ByteOrder.LITTLE_ENDIAN);
        buff2 = ByteBuffer.allocate(2);
        buff2.order(ByteOrder.LITTLE_ENDIAN);
        buff4 = ByteBuffer.allocate(4);
        buff4.order(ByteOrder.LITTLE_ENDIAN);
        resultBuff1 = ByteBuffer.allocate(1);
        resultBuff1.order(ByteOrder.LITTLE_ENDIAN);
        resultBuff2 = ByteBuffer.allocate(2);
        resultBuff2.order(ByteOrder.LITTLE_ENDIAN);
        resultBuff4 = ByteBuffer.allocate(4);
        resultBuff4.order(ByteOrder.LITTLE_ENDIAN);
        buff1.put((byte)0x05);
        buff2.put((byte)0x05);
        buff4.put((byte)0x05);
        resultBuff1.put((byte)intValue);
        resultBuff2.putShort((short)intValue);
        resultBuff4.putInt(intValue);


        //test target MPH
        bit = BitFieldId.TARGET_MPH;
        assertEquals(BitFieldId.TARGET_MPH, bit);
        assertEquals(0, bit.getVal());
        assertEquals(0, bit.getSection());
        assertEquals(0, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(0.5, ((SpeedConverter)bit.getData(buff2)).getSpeed());
        resultBuff2.clear();
        resultBuff2.putShort((short)50);
        assertEquals(resultBuff2, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test

        // test Current MPH
        bit = BitFieldId.CURRENT_MPH;
        assertEquals(BitFieldId.CURRENT_MPH, bit);
        assertEquals(1, bit.getVal());
        assertEquals(0, bit.getSection());
        assertEquals(1, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(0.5, ((SpeedConverter)bit.getData(buff2)).getSpeed());
        resultBuff2.clear();
        resultBuff2.putShort((short)50);
        assertEquals(resultBuff2, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test

        //test target Incline
        bit = BitFieldId.TARGET_INCLINE;
        assertEquals(BitFieldId.TARGET_INCLINE, bit);
        assertEquals(2, bit.getVal());
        assertEquals(0, bit.getSection());
        assertEquals(2, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(0.05, ((InclineConverter)bit.getData(buff2)).getIncline());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test

        //test Current Incline
        bit = BitFieldId.CURRENT_INCLINE;
        assertEquals(BitFieldId.CURRENT_INCLINE, bit);
        assertEquals(3, bit.getVal());
        assertEquals(0, bit.getSection());
        assertEquals(3, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(0.05, ((InclineConverter)bit.getData(buff2)).getIncline());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test

        //test target Volume
        bit = BitFieldId.TARGET_VOLUME;
        assertEquals(BitFieldId.TARGET_VOLUME, bit);
        assertEquals(4, bit.getVal());
        assertEquals(0, bit.getSection());
        assertEquals(4, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Current Volume
        bit = BitFieldId.CURRENT_VOLUME;
        assertEquals(BitFieldId.CURRENT_VOLUME, bit);
        assertEquals(5, bit.getVal());
        assertEquals(0, bit.getSection());
        assertEquals(5, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Target Fan Speed
        bit = BitFieldId.TARGET_FAN_SPEED;
        assertEquals(BitFieldId.TARGET_FAN_SPEED, bit);
        assertEquals(6, bit.getVal());
        assertEquals(0, bit.getSection());
        assertEquals(6, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Current Fan Speed
        bit = BitFieldId.CURRENT_FAN_SPEED;
        assertEquals(BitFieldId.CURRENT_FAN_SPEED, bit);
        assertEquals(7, bit.getVal());
        assertEquals(0, bit.getSection());
        assertEquals(7, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Target Resistance
        bit = BitFieldId.TARGET_RESISTANCE;
        assertEquals(BitFieldId.TARGET_RESISTANCE, bit);
        assertEquals(8, bit.getVal());
        assertEquals(1, bit.getSection());
        assertEquals(0, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(0.05, ((ResistanceConverter)bit.getData(buff2)).getResistance());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test

        //test Current Resistance
        bit = BitFieldId.CURRENT_RESISTANCE;
        assertEquals(BitFieldId.CURRENT_RESISTANCE, bit);
        assertEquals(9, bit.getVal());
        assertEquals(1, bit.getSection());
        assertEquals(1, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(0.05, ((ResistanceConverter)bit.getData(buff2)).getResistance());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test

        //test Target Watts
        bit = BitFieldId.TARGET_WATTS;
        assertEquals(BitFieldId.TARGET_WATTS, bit);
        assertEquals(10, bit.getVal());
        assertEquals(1, bit.getSection());
        assertEquals(2, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(5, ((ShortConverter)bit.getData(buff2)).getValue());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(500.123));//double test
        assertEquals(resultBuff2, bit.getRawFromData(500));//int test

        //test Current Watts
        bit = BitFieldId.CURRENT_WATTS;
        assertEquals(BitFieldId.CURRENT_WATTS, bit);
        assertEquals(11, bit.getVal());
        assertEquals(1, bit.getSection());
        assertEquals(3, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ShortConverter)bit.getData(buff2)).getValue());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(500.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(500));//int test

        //test Target Torque
        bit = BitFieldId.TARGET_TORQUE;
        assertEquals(BitFieldId.TARGET_TORQUE, bit);
        assertEquals(12, bit.getVal());
        assertEquals(1, bit.getSection());
        assertEquals(4, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(5, ((ShortConverter)bit.getData(buff2)).getValue());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(500.123));//double test
        assertEquals(resultBuff2, bit.getRawFromData(500));//int test

        //test Current Torque
        bit = BitFieldId.CURRENT_TORQUE;
        assertEquals(BitFieldId.CURRENT_TORQUE, bit);
        assertEquals(13, bit.getVal());
        assertEquals(1, bit.getSection());
        assertEquals(5, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ShortConverter)bit.getData(buff2)).getValue());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(500.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(500));//int test

        //test Current Keycode
        bit = BitFieldId.CURRENT_KEYCODE;
        assertEquals(BitFieldId.CURRENT_KEYCODE, bit);
        assertEquals(14, bit.getVal());
        assertEquals(1, bit.getSection());
        assertEquals(6, bit.getBit());
        assertEquals(4, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ShortConverter)bit.getData(buff2)).getValue());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(500.123));//double test
        assertEquals(resultBuff2, bit.getRawFromData(500));//int test

        //test Current Torque
        bit = BitFieldId.CURRENT_TORQUE;
        assertEquals(BitFieldId.CURRENT_TORQUE, bit);
        assertEquals(13, bit.getVal());
        assertEquals(1, bit.getSection());
        assertEquals(5, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ShortConverter)bit.getData(buff2)).getValue());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(500.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(500));//int test

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