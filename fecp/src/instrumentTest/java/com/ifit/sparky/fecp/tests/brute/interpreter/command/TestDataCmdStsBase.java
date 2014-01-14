/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 12/16/13
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.command;

import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.BitfieldDataConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ByteConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.InclineConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.SpeedConverter;
import com.ifit.sparky.fecp.interpreter.command.DataBaseCmd;

import junit.framework.TestCase;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;

public class TestDataCmdStsBase extends TestCase {

    /**
     * Setups the TestRunner for Status.
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception{
        super.setUp();
    }

    /**
     * Closes the TestRunner for Status.
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /** Tests the Constructors.
     *
     * @throws Exception
     */
    public void testDataCmdSts_Constructor() throws Exception{

        DataBaseCmd dataBase;


        dataBase = new DataBaseCmd();
        //default constructor
        assertEquals(0, dataBase.getNumOfDataBytes());
        assertEquals(0, dataBase.getMsgDataBytesCount());

    }

    /** Tests the add bitfield and data.
     *
     * @throws Exception
     */
    public void testDataCmdSts_addBitField() throws Exception{

        DataBaseCmd dataBase;

        dataBase = new DataBaseCmd();
        //default constructor
        assertEquals(0, dataBase.getNumOfDataBytes());
        assertEquals(0, dataBase.getMsgDataBytesCount());

        dataBase.addBitfieldData(BitFieldId.TARGET_MPH, 10.5);

        assertEquals(1, dataBase.getNumOfDataBytes());
        assertEquals(2, dataBase.getMsgDataBytesCount());

        //add another byte to the first section
        dataBase.addBitfieldData(BitFieldId.TARGET_INCLINE, 10.5);

        assertEquals(1, dataBase.getNumOfDataBytes());
        assertEquals(4, dataBase.getMsgDataBytesCount());

        //add another byte to the 2nd section.
        dataBase.addBitfieldData(BitFieldId.TARGET_RESISTANCE, 50.00);//%50.00

        assertEquals(2, dataBase.getNumOfDataBytes());
        assertEquals(6, dataBase.getMsgDataBytesCount());
    }

    /** Tests the removing a bitfield and data.
     *
     * @throws Exception
     */
    public void testDataCmdSts_removeBitField() throws Exception{

        DataBaseCmd dataBase;

        dataBase = new DataBaseCmd();
        //default constructor
        assertEquals(0, dataBase.getNumOfDataBytes());
        assertEquals(0, dataBase.getMsgDataBytesCount());

        //try removing an item when there isn't one there.
        //should do nothing
        dataBase.removeBitfieldData(BitFieldId.TARGET_MPH);

        assertEquals(0, dataBase.getNumOfDataBytes());
        assertEquals(0, dataBase.getMsgDataBytesCount());

        //add one and remove it.
        dataBase.addBitfieldData(BitFieldId.TARGET_MPH, 10.5);

        assertEquals(1, dataBase.getNumOfDataBytes());
        assertEquals(2, dataBase.getMsgDataBytesCount());

        //try removing an item when there is one there.
        dataBase.removeBitfieldData(BitFieldId.TARGET_MPH);

        assertEquals(0, dataBase.getNumOfDataBytes());
        assertEquals(0, dataBase.getMsgDataBytesCount());

        //add 2
        dataBase.addBitfieldData(BitFieldId.TARGET_MPH, 10.5);
        dataBase.addBitfieldData(BitFieldId.TARGET_INCLINE, 10.5);

        assertEquals(1, dataBase.getNumOfDataBytes());
        assertEquals(4, dataBase.getMsgDataBytesCount());

        //remove 1 and make sure it is still there
        dataBase.removeBitfieldData(BitFieldId.TARGET_INCLINE);

        assertEquals(1, dataBase.getNumOfDataBytes());
        assertEquals(2, dataBase.getMsgDataBytesCount());

        //add 1 in a different section
        dataBase.addBitfieldData(BitFieldId.TARGET_RESISTANCE, 50.00);//%50.00

        assertEquals(2, dataBase.getNumOfDataBytes());
        assertEquals(4, dataBase.getMsgDataBytesCount());

        //remove the 1st section item
        dataBase.removeBitfieldData(BitFieldId.TARGET_MPH);

        assertEquals(2, dataBase.getNumOfDataBytes());// Caught Bug in code
        assertEquals(2, dataBase.getMsgDataBytesCount());

        //try adding one that is already there
        dataBase.addBitfieldData(BitFieldId.TARGET_RESISTANCE, 50.00);//%50.00

        assertEquals(2, dataBase.getNumOfDataBytes());
        assertEquals(2, dataBase.getMsgDataBytesCount());

    }

    /** Tests getting the message data formatting.
     *
     * @throws Exception
     */
    public void testDataCmdSts_getMsgDataHeader() throws Exception{

        DataBaseCmd dataBase;
        ByteBuffer buffer;

        dataBase = new DataBaseCmd();

        //get the message header for an empty list
        buffer = dataBase.getMsgDataHeader();
        buffer.position(0);
        assertEquals(1, buffer.capacity());
        assertEquals(0, buffer.get());

        dataBase.addBitfieldData(BitFieldId.TARGET_MPH, 10.5);

        buffer = dataBase.getMsgDataHeader();
        buffer.position(0);
        assertEquals(2, buffer.capacity());
        assertEquals(1, buffer.get());//number of sections
        assertEquals(1, buffer.get());//targetMPH bit Caught Bug in Code

        //add another in same section
        dataBase.addBitfieldData(BitFieldId.TARGET_INCLINE, 10.5);

        buffer = dataBase.getMsgDataHeader();
        buffer.position(0);
        assertEquals(2, buffer.capacity());
        assertEquals(1, buffer.get());//number of sections
        assertEquals(5, buffer.get());

        //add another in different section
        dataBase.addBitfieldData(BitFieldId.TARGET_RESISTANCE, 50.00);

        buffer = dataBase.getMsgDataHeader();
        buffer.position(0);
        assertEquals(3, buffer.capacity());
        assertEquals(2, buffer.get());//number of sections
        assertEquals(5, buffer.get());
        assertEquals(1, buffer.get());

        //skip a section and add it
        //add another in different section
        dataBase.addBitfieldData(BitFieldId.CURRENT_PULSE, 10);

        buffer = dataBase.getMsgDataHeader();
        buffer.position(0);
        assertEquals(5, buffer.capacity());
        assertEquals(4, buffer.get());//number of sections
        assertEquals(5, buffer.get());
        assertEquals(1, buffer.get());
        assertEquals(0, buffer.get());
        assertEquals((1<<5), buffer.get());//test bit 5 should be set
    }

    /** Tests getting the all the data, section bytes to the end of the last data object.
     *
     * @throws Exception
     */
    public void testDataCmdSts_getWriteDataMsg() throws Exception{

        DataBaseCmd dataBase;
        ByteBuffer buffer;

        dataBase = new DataBaseCmd();

        //get the message header for an empty list
        buffer = dataBase.getWriteMsgData();
        buffer.position(0);
        assertEquals(1, buffer.capacity());
        assertEquals(0, buffer.get());

        dataBase.addBitfieldData(BitFieldId.TARGET_MPH, 10.5);

        buffer = dataBase.getWriteMsgData();
        buffer.position(0);
        assertEquals(4, buffer.capacity());
        assertEquals(1, buffer.get());//number of sections
        assertEquals(1, buffer.get());
        assertEquals(105, buffer.getShort());//Caught bug

        //add another in same section
        dataBase.addBitfieldData(BitFieldId.TARGET_INCLINE, 10.50);//%10.50 percent incline

        buffer = dataBase.getWriteMsgData();
        buffer.position(0);
        assertEquals(6, buffer.capacity());
        assertEquals(1, buffer.get());//number of sections
        assertEquals(5, buffer.get());
        assertEquals(105, buffer.getShort());//target speed
        assertEquals(1050, buffer.getShort());//target incline Caught Bug

        //add another in different section
        dataBase.addBitfieldData(BitFieldId.TARGET_RESISTANCE, 50.00);

        buffer = dataBase.getWriteMsgData();
        buffer.position(0);
        assertEquals(9, buffer.capacity());
        assertEquals(2, buffer.get());//number of sections
        assertEquals(5, buffer.get());//section 0
        assertEquals(1, buffer.get());//section 1
        assertEquals(105, buffer.getShort());//target speed
        assertEquals(1050, buffer.getShort());//target incline
        assertEquals(5000, buffer.getShort());//target Resistance

        //skip a section and add it
        //add another in different section
        dataBase.addBitfieldData(BitFieldId.CURRENT_PULSE, 123);

        buffer = dataBase.getWriteMsgData();
        buffer.position(0);
        assertEquals(12, buffer.capacity());
        assertEquals(4, buffer.get());//number of sections
        assertEquals(5, buffer.get());//section 0
        assertEquals(1, buffer.get());//section 1
        assertEquals(0, buffer.get());//section 2
        assertEquals((1<<5), buffer.get());//section 3
        assertEquals(105, buffer.getShort());//target speed
        assertEquals(1050, buffer.getShort());//target incline
        assertEquals(5000, buffer.getShort());//target Resistance
        assertEquals(123, buffer.get());//Current Pulse
    }

    /** Tests handling the data in the buffer.
     * buffer has to be in the correct position.
     *
     * @throws Exception
     */
    public void testDataCmdSts_handleReadData() throws Exception{
        DataBaseCmd dataBase;
        ByteBuffer buffer;
        Map<BitFieldId, BitfieldDataConverter> map;

        dataBase = new DataBaseCmd();

        //test empty byte buffer
        buffer = ByteBuffer.allocate(1);//tes
        buffer.position(0);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0);
        buffer.position(0);
        dataBase.handleReadData(buffer);
        assertEquals(0, buffer.position());//position shouldn't change

        //Test Speed value
        buffer = ByteBuffer.allocate(2);//tes
        buffer.position(0);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort((short) 100);//ten mph
        buffer.position(0);

        dataBase.addBitfieldData(BitFieldId.TARGET_MPH, 0);
        map = dataBase.handleReadData(buffer);

        assertEquals(10.0, ((SpeedConverter)map.get(BitFieldId.TARGET_MPH)).getSpeed());

        // Test the Speed and the Incline
        buffer = ByteBuffer.allocate(4);
        buffer.position(0);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort((short) 100);//ten mph
        buffer.putShort((short) 1234);//incline
        buffer.position(0);

        dataBase.addBitfieldData(BitFieldId.TARGET_INCLINE, 0);
        map = dataBase.handleReadData(buffer);

        assertEquals(10.0, ((SpeedConverter)map.get(BitFieldId.TARGET_MPH)).getSpeed());
        assertEquals(12.34, ((InclineConverter)map.get(BitFieldId.TARGET_INCLINE)).getIncline());

        // Test skipping a section, and the order of the items
        buffer = ByteBuffer.allocate(3);
        buffer.position(0);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort((short) 1234);//incline
        buffer.put((byte) 231);//Pulse
        buffer.position(0);

        dataBase.removeBitfieldData(BitFieldId.TARGET_MPH);
        dataBase.addBitfieldData(BitFieldId.CURRENT_PULSE, 0);
        map = dataBase.handleReadData(buffer);

        assertEquals(12.34, ((InclineConverter)map.get(BitFieldId.TARGET_INCLINE)).getIncline());
        assertEquals(231, ((ByteConverter)map.get(BitFieldId.CURRENT_PULSE)).getValue());

        //re add the speed, and check order
        // Test skipping a section, and the order of the items
        buffer = ByteBuffer.allocate(5);
        buffer.position(0);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort((short) 100);//ten mph
        buffer.putShort((short) 1234);//incline
        buffer.put((byte) 231);//Pulse
        buffer.position(0);

        dataBase.addBitfieldData(BitFieldId.TARGET_MPH, 0);
        map = dataBase.handleReadData(buffer);

        assertEquals(10.0, ((SpeedConverter)map.get(BitFieldId.TARGET_MPH)).getSpeed());
        assertEquals(12.34, ((InclineConverter)map.get(BitFieldId.TARGET_INCLINE)).getIncline());
        assertEquals(231, ((ByteConverter)map.get(BitFieldId.CURRENT_PULSE)).getValue());

    }
}
