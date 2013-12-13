/**
 * Tests the BitFieldId enum and its methods.
 * @author Levi.Balling
 * @date 12/11/13
 * @version 1
 * Tests the enum and all of the different values dealing with the DataConverters also.
 * This is a Brute Force test, and should only be ran over night and before release.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.bitField;

import com.ifit.sparky.fecp.interpreter.bitField.*;
import com.ifit.sparky.fecp.interpreter.bitField.converter.*;
import com.ifit.sparky.fecp.interpreter.key.KeyCodes;
import com.ifit.sparky.fecp.interpreter.key.KeyObject;

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
        assertNotNull(idOne.getDescription());

        assertEquals(BitFieldId.TARGET_INCLINE, idTwo);
        assertEquals(2, idTwo.getVal());
        assertEquals(0, idTwo.getSection());
        assertEquals(2, idTwo.getBit());
        assertEquals(2, idTwo.getSize());
        assertEquals(false, idTwo.getReadOnly());
        assertNotNull(idTwo.getDescription());

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
        ByteBuffer buff;

        //test Byte Converter with int inputs
        idOne = BitFieldId.TARGET_FAN_SPEED;
        buff = ByteBuffer.allocate(1);
        buff.order(ByteOrder.LITTLE_ENDIAN);

        // Test all unsigned Byte values
        for(int i = Byte.MIN_VALUE; i < Byte.MAX_VALUE; i++)
        {
            buff.clear();
            buff.put((byte) i);
            converter = idOne.getData(buff);
            assertEquals(ByteConverter.class, converter.getClass());//should be the same class
            assertEquals(((byte)i & 0xFF), ((ByteConverter)converter).getValue());
        }

        //test Incline Converter
        idOne = BitFieldId.TARGET_INCLINE;
        buff = ByteBuffer.allocate(2);
        buff.order(ByteOrder.LITTLE_ENDIAN);

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

        //test Key object converter
        idOne = BitFieldId.KEY_OBJECT;
        buff = ByteBuffer.allocate(10);
        buff.order(ByteOrder.LITTLE_ENDIAN);

        //Test all the keycodes
        for(KeyCodes code : KeyCodes.values())
        {
            buff.clear();
            buff.putShort((short) code.getVal());
            buff.putInt(0xFFFFFFFC);
            buff.putShort((short) 1234);
            buff.putShort((short) 4321);
            converter = idOne.getData(buff);
            assertEquals(KeyObjectConverter.class, converter.getClass());//should be the same class
            assertEquals(code, ((KeyObjectConverter)converter).getKeyObject().getCookedKeyCode());
            assertEquals(0xFFFFFFFC, ((KeyObjectConverter)converter).getKeyObject().getRawKeyCode());
            assertEquals(1234, ((KeyObjectConverter)converter).getKeyObject().getTimePressed());
            assertEquals(4321, ((KeyObjectConverter)converter).getKeyObject().getTimeHeld());
        }
        //test Long Converter
        idOne = BitFieldId.LED_MASK;
        buff = ByteBuffer.allocate(4);
        buff.order(ByteOrder.LITTLE_ENDIAN);

        //test limits only, anything above short is to big.
        //min
        buff.clear();
        buff.putInt(Integer.MIN_VALUE);
        converter = idOne.getData(buff);
        assertEquals(LongConverter.class, converter.getClass());//should be the same class
        assertEquals(Integer.MIN_VALUE , ((LongConverter)converter).getValue());

        //0
        buff.clear();
        buff.putInt(0);
        converter = idOne.getData(buff);
        assertEquals(LongConverter.class, converter.getClass());//should be the same class
        assertEquals(0, ((LongConverter)converter).getValue());

        //max
        buff.clear();
        buff.putInt(Integer.MAX_VALUE);
        converter = idOne.getData(buff);
        assertEquals(LongConverter.class, converter.getClass());//should be the same class
        assertEquals(Integer.MAX_VALUE, ((LongConverter)converter).getValue());

        //test Resistance Converter
        idOne = BitFieldId.TARGET_RESISTANCE;
        buff = ByteBuffer.allocate(2);
        buff.order(ByteOrder.LITTLE_ENDIAN);

        // Test all unsigned short values
        for(int i = Short.MIN_VALUE; i < Short.MAX_VALUE; i++)
        {
            double expectResult;
            buff.clear();
            buff.putShort((short) i);
            expectResult = (((short)i & 0xFFFF) + 0.0) / 100;
            converter = idOne.getData(buff);
            assertEquals(ResistanceConverter.class, converter.getClass());//should be the same class
            assertEquals(expectResult, ((ResistanceConverter)converter).getResistance());
        }

        //test Short Converter with int inputs we want this to be unsigned
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
            assertEquals(((short)i & 0xFFFF), ((ShortConverter)converter).getValue());
        }

        //test Speed Converter
        buff = ByteBuffer.allocate(2);
        buff.order(ByteOrder.LITTLE_ENDIAN);
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
        ByteBuffer keyBuff;
        ByteBuffer resultBuff1;
        ByteBuffer resultBuff2;
        ByteBuffer resultBuff4;
        KeyObject key;
        int intValue = 5;
        buff1 = ByteBuffer.allocate(1);
        buff1.order(ByteOrder.LITTLE_ENDIAN);
        buff2 = ByteBuffer.allocate(2);
        buff2.order(ByteOrder.LITTLE_ENDIAN);
        buff4 = ByteBuffer.allocate(4);
        buff4.order(ByteOrder.LITTLE_ENDIAN);
        keyBuff = ByteBuffer.allocate(10);
        keyBuff.order(ByteOrder.LITTLE_ENDIAN);
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
        bit = BitFieldId.KEY_OBJECT;
        keyBuff.putShort((short)KeyCodes.SPEED_UP.getVal());//stopKey
        keyBuff.putInt(0xFFFFCFFF);
        keyBuff.putShort((short) 123);
        keyBuff.putShort((short)321);

        assertEquals(BitFieldId.KEY_OBJECT, bit);
        assertEquals(14, bit.getVal());
        assertEquals(1, bit.getSection());
        assertEquals(6, bit.getBit());
        assertEquals(10, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        key = ((KeyObjectConverter) bit.getData(keyBuff)).getKeyObject();
        assertEquals(KeyCodes.SPEED_UP, key.getCookedKeyCode());
        assertEquals(0xFFFFCFFF, key.getRawKeyCode());
        assertEquals(123, key.getTimePressed());
        assertEquals(321, key.getTimeHeld());

        //test Key Beep
        bit = BitFieldId.KEY_BEEP;
        assertEquals(BitFieldId.KEY_BEEP, bit);
        assertEquals(16, bit.getVal());
        assertEquals(2, bit.getSection());
        assertEquals(0, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test AndroidKeys
        bit = BitFieldId.ANDROID_KEYS;
        assertEquals(BitFieldId.ANDROID_KEYS, bit);
        assertEquals(17, bit.getVal());
        assertEquals(2, bit.getSection());
        assertEquals(1, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Mode flags
        bit = BitFieldId.MODE_FLAGS;
        assertEquals(BitFieldId.MODE_FLAGS, bit);
        assertEquals(18, bit.getVal());
        assertEquals(2, bit.getSection());
        assertEquals(2, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Status
        bit = BitFieldId.STATUS;
        assertEquals(BitFieldId.STATUS, bit);
        assertEquals(19, bit.getVal());
        assertEquals(2, bit.getSection());
        assertEquals(3, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Current BroadCast frequency
        bit = BitFieldId.CURRENT_BV_FREQUENCY;
        assertEquals(BitFieldId.CURRENT_BV_FREQUENCY, bit);
        assertEquals(20, bit.getVal());
        assertEquals(2, bit.getSection());
        assertEquals(4, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ShortConverter)bit.getData(buff2)).getValue());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(500.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(500));//int test

        //test Current Broadcast Volume
        bit = BitFieldId.CURRENT_BV_VOLUME;
        assertEquals(BitFieldId.CURRENT_BV_VOLUME, bit);
        assertEquals(21, bit.getVal());
        assertEquals(2, bit.getSection());
        assertEquals(5, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Current Audio Source
        bit = BitFieldId.CURRENT_AUDIO_SOURCE;
        assertEquals(BitFieldId.CURRENT_AUDIO_SOURCE, bit);
        assertEquals(22, bit.getVal());
        assertEquals(2, bit.getSection());
        assertEquals(6, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Target Gears
        bit = BitFieldId.TARGET_GEARS;
        assertEquals(BitFieldId.TARGET_GEARS, bit);
        assertEquals(23, bit.getVal());
        assertEquals(2, bit.getSection());
        assertEquals(7, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Safety Fence
        bit = BitFieldId.SAFETY_FENCE;
        assertEquals(BitFieldId.SAFETY_FENCE, bit);
        assertEquals(24, bit.getVal());
        assertEquals(3, bit.getSection());
        assertEquals(0, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ShortConverter)bit.getData(buff2)).getValue());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(500.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(500));//int test

        //test UpRights
        bit = BitFieldId.UP_RIGHTS;
        assertEquals(BitFieldId.UP_RIGHTS, bit);
        assertEquals(25, bit.getVal());
        assertEquals(3, bit.getSection());
        assertEquals(1, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ShortConverter)bit.getData(buff2)).getValue());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(500.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(500));//int test

        //test Tilt
        bit = BitFieldId.TILT;
        assertEquals(BitFieldId.TILT, bit);
        assertEquals(26, bit.getVal());
        assertEquals(3, bit.getSection());
        assertEquals(2, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ShortConverter)bit.getData(buff2)).getValue());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(500.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(500));//int test

        //test MaxResistance
        bit = BitFieldId.MAX_RESISTANCE;
        assertEquals(BitFieldId.MAX_RESISTANCE, bit);
        assertEquals(27, bit.getVal());
        assertEquals(3, bit.getSection());
        assertEquals(3, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(0.05, ((ResistanceConverter)bit.getData(buff2)).getResistance());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test

        //test Current RPM
        bit = BitFieldId.CURRENT_RPM;
        assertEquals(BitFieldId.CURRENT_RPM, bit);
        assertEquals(28, bit.getVal());
        assertEquals(3, bit.getSection());
        assertEquals(4, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Current Pulse
        bit = BitFieldId.CURRENT_PULSE;
        assertEquals(BitFieldId.CURRENT_PULSE, bit);
        assertEquals(29, bit.getVal());
        assertEquals(3, bit.getSection());
        assertEquals(5, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Incline Transmax
        bit = BitFieldId.INCLINE_TRANSMAX;
        assertEquals(BitFieldId.INCLINE_TRANSMAX, bit);
        assertEquals(30, bit.getVal());
        assertEquals(3, bit.getSection());
        assertEquals(6, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(5, ((ShortConverter)bit.getData(buff2)).getValue());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(500.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(500));//int test

        //test MaxResistance
        bit = BitFieldId.MIN_RESISTANCE;
        assertEquals(BitFieldId.MIN_RESISTANCE, bit);
        assertEquals(31, bit.getVal());
        assertEquals(3, bit.getSection());
        assertEquals(7, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(0.05, ((ResistanceConverter)bit.getData(buff2)).getResistance());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test

        //test Incline Tach
        bit = BitFieldId.INCLINE_TACH;
        assertEquals(BitFieldId.INCLINE_TACH, bit);
        assertEquals(32, bit.getVal());
        assertEquals(4, bit.getSection());
        assertEquals(0, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Target Stride Length
        bit = BitFieldId.TARGET_STRIDE_LENGTH;
        assertEquals(BitFieldId.TARGET_STRIDE_LENGTH, bit);
        assertEquals(33, bit.getVal());
        assertEquals(4, bit.getSection());
        assertEquals(1, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Current Stride Position
        bit = BitFieldId.CURRENT_STRIDE_POSITION;
        assertEquals(BitFieldId.CURRENT_STRIDE_POSITION, bit);
        assertEquals(34, bit.getVal());
        assertEquals(4, bit.getSection());
        assertEquals(2, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Current Stride Direction
        bit = BitFieldId.CURRENT_STRIDE_DIRECTION;
        assertEquals(BitFieldId.CURRENT_STRIDE_DIRECTION, bit);
        assertEquals(35, bit.getVal());
        assertEquals(4, bit.getSection());
        assertEquals(3, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Current Stride Length
        bit = BitFieldId.CURRENT_STRIDE_LENGTH;
        assertEquals(BitFieldId.CURRENT_STRIDE_LENGTH, bit);
        assertEquals(36, bit.getVal());
        assertEquals(4, bit.getSection());
        assertEquals(4, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Current Stride Speed
        bit = BitFieldId.CURRENT_STRIDE_SPEED;
        assertEquals(BitFieldId.CURRENT_STRIDE_SPEED, bit);
        assertEquals(37, bit.getVal());
        assertEquals(4, bit.getSection());
        assertEquals(5, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Led Bank
        bit = BitFieldId.LED_BANK;
        assertEquals(BitFieldId.LED_BANK, bit);
        assertEquals(38, bit.getVal());
        assertEquals(4, bit.getSection());
        assertEquals(6, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test LED Mask
        bit = BitFieldId.LED_MASK;
        assertEquals(BitFieldId.LED_MASK, bit);
        assertEquals(39, bit.getVal());
        assertEquals(4, bit.getSection());
        assertEquals(7, bit.getBit());
        assertEquals(4, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((LongConverter)bit.getData(buff4)).getValue());
        resultBuff4.clear();
        resultBuff4.putInt(5);
        assertEquals(resultBuff4, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff4, bit.getRawFromData(5));//int test

        //test Current Raw Pulse
        bit = BitFieldId.CURRENT_RAW_PULSE;
        assertEquals(BitFieldId.CURRENT_RAW_PULSE, bit);
        assertEquals(40, bit.getVal());
        assertEquals(5, bit.getSection());
        assertEquals(0, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Current Voltage Sense
        bit = BitFieldId.CURRENT_VSENSE;
        assertEquals(BitFieldId.CURRENT_VSENSE, bit);
        assertEquals(41, bit.getVal());
        assertEquals(5, bit.getSection());
        assertEquals(1, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test WorkoutState
        bit = BitFieldId.WORKOUT_STATE;
        assertEquals(BitFieldId.WORKOUT_STATE, bit);
        assertEquals(42, bit.getVal());
        assertEquals(5, bit.getSection());
        assertEquals(2, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Min Incline
        bit = BitFieldId.MIN_INCLINE;
        assertEquals(BitFieldId.MIN_INCLINE, bit);
        assertEquals(43, bit.getVal());
        assertEquals(5, bit.getSection());
        assertEquals(3, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(0.05, ((InclineConverter)bit.getData(buff2)).getIncline());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test

        //test Max Incline
        bit = BitFieldId.MAX_INCLINE;
        assertEquals(BitFieldId.MAX_INCLINE, bit);
        assertEquals(44, bit.getVal());
        assertEquals(5, bit.getSection());
        assertEquals(4, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(0.05, ((InclineConverter)bit.getData(buff2)).getIncline());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test

        //test Stop Incline
        bit = BitFieldId.STOP_INCLINE;
        assertEquals(BitFieldId.STOP_INCLINE, bit);
        assertEquals(45, bit.getVal());
        assertEquals(5, bit.getSection());
        assertEquals(5, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(0.05, ((InclineConverter)bit.getData(buff2)).getIncline());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test

        //test Actual Max Incline
        bit = BitFieldId.ACTUAL_MAX_INCLINE;
        assertEquals(BitFieldId.ACTUAL_MAX_INCLINE, bit);
        assertEquals(46, bit.getVal());
        assertEquals(5, bit.getSection());
        assertEquals(6, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(0.05, ((InclineConverter)bit.getData(buff2)).getIncline());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test

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