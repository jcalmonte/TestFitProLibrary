/**
 * Handles the status for the WriteReadData command.
 * @author Levi.Balling
 * @date 1/14/14
 * @version 1
 * this will handle all the information about the read data, and the reply.
 */
package com.ifit.sparky.fecp.interpreter.status;

import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.BitfieldDataConverter;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.DataBaseCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.TreeMap;

public class WriteReadDataSts extends Status implements StatusInterface  {


    private static final int MIN_STS_LENGTH = 5;
    private DataBaseCmd mData;
    private TreeMap<BitFieldId, BitfieldDataConverter> mResultData;

    /**
     * Default constructor for handling the reply and generating the cmd data.
     * @param devId the device id
     * @throws Exception
     */
    public WriteReadDataSts(DeviceId devId) throws Exception
    {
        //Min length is 5 bytes
        super(StatusId.DEV_NOT_SUPPORTED, MIN_STS_LENGTH, CommandId.WRITE_READ_DATA, devId);
        this.mData = new DataBaseCmd();
        this.mResultData = new TreeMap<BitFieldId, BitfieldDataConverter>(new Comparator<BitFieldId>() {
            @Override
            public int compare(BitFieldId bitFieldId, BitFieldId bitFieldId2) {
                return bitFieldId.compareTo(bitFieldId2);
            }
        });
    }

    /**
     * Gets the controller for handling bitfields for sending and receiving
     * @return the DataBaseCmd. ifit shouldn't need to touch this.
     */
    public DataBaseCmd getBitFieldReadData() {
        return mData;
    }

    /**
     * Gets the data from the message received
     * @return a Treemap of all the data base on BitfieldIds(mph,incline,etc..)
     */
    public TreeMap<BitFieldId, BitfieldDataConverter> getResultData() {
        return mResultData;
    }

    /**
     * Handles the message that is coming across the usb. It handles raw data, and it
     * must be handled by the correct status.
     *
     * @param buff the msg that came from the usb. only str
     */
    @Override
    public void handleStsMsg(ByteBuffer buff) throws Exception {
        super.handleStsMsg(buff);
        //handle the data now
        this.mResultData = (TreeMap<BitFieldId, BitfieldDataConverter>)this.mData.handleReadData(buff);
    }
}
