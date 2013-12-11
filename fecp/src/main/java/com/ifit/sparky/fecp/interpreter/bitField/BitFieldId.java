/**
 * This is all the main Data Bit values.
 * @author Levi.Balling
 * @date 12/11/13
 * @version 1
 * This will hold all the major values for controlling the Data Bit.
 */
package com.ifit.sparky.fecp.interpreter.bitField;

import com.ifit.sparky.fecp.interpreter.bitField.converter.*;
import com.ifit.sparky.fecp.interpreter.command.CommandId;

import java.util.ArrayList;

public enum BitFieldId {
    TARGET_MPH(0, 2, false, new SpeedConverter(), "Target Speed"),
    CURRENT_MPH(1, 2, true, new SpeedConverter(), "Current Speed"),
    TARGET_INCLINE(2, 1, false, new InclineConverter(), "Target Incline"),
    CURRENT_INCLINE(3, 1, true, new InclineConverter(), "Current Incline"),
    TARGET_VOLUME(4, 1, false, new InclineConverter(), "Target Volume"),
    CURRENT_VOLUME(5, 1, true, new InclineConverter(), "Current Volume"),
    TARGET_FAN_SPEED(6, 1, false, new InclineConverter(), "Target Fan Speed"),
    CURRENT_FAN_SPEED(7, 1, true, new InclineConverter(), "Current fan Speed");

    private int mValue; // indexed at 1-255
    private int mSection;
    private int mBit;
    private int mByteSize;//Number of bytes
    private boolean mReadOnly;//this dataType is a Read Only Type
    private BitfieldDataConverter mConverter;
    private String mDescription;

    /**
     * constructor for the CommandId enum.
     * based on the Id we get the section and bit
     * @param value value of the Command
     * @param size the number of bytes in the bitfield.
     * @param readOnly if the data is read only
     * @param converter the converter to convert the data correctly.
     * @param description of what the command is for
     */
    BitFieldId(int value, int size, boolean readOnly, BitfieldDataConverter converter, String description)
    {
        this.mValue = value;
        this.mSection = value/8;
        this.mBit = value%8;
        this.mByteSize = size;
        this.mReadOnly = readOnly;
        this.mConverter = converter;
        this.mDescription = description;
    }

    /**
     * gets the Section
     * @return gets the Section of the Bitfield
     */
    public int getSection()
    {
        return this.mSection;
    }

    /**
     * gets the Bit
     * @return gets the Bit of the Bitfield
     */
    public int getBit()
    {
        return this.mBit;
    }

    /**
     * gets the Data's size in bytes
     * @return gets the Byte size of the Bitfield
     */
    public int getSize()
    {
        return this.mByteSize;
    }

    /**
     * gets whether the data is read only
     * @return if it is read only returns True, else false
     */
    public boolean getReadOnly()
    {
        return this.mReadOnly;
    }

    /**
     * gets the id value
     * @return gets the Command Id Value
     */
    public int getVal()
    {
        return this.mValue;
    }

    /**
     * gets the description of the Command.
     * @return a description of the Command.
     */
    public String getDescription()
    {
        return  this.mDescription;
    }

    public BitfieldDataConverter getData(ArrayList<Byte> rawData) throws Exception
    {
        this.mConverter.setRawData(rawData, this.mByteSize);
        return this.mConverter.getData();
    }

    /**
     * Converts the data into a formatted array of bytes.
     * @param data data that is to be formatted.
     * @return Array of bytes that are ready to be sent
     * @throws InvalidBitFieldException if the types don't match up
     */
    public ArrayList<Byte> getRawFromData(int data)throws InvalidBitFieldException
    {
        return this.mConverter.convertData(data);
    }

    /**
     * Converts the data into a formatted array of bytes.
     * @param data data that is to be formatted.
     * @return Array of bytes that are ready to be sent
     * @throws InvalidBitFieldException if the types don't match up
     */
    public ArrayList<Byte> getRawFromData(double data)throws InvalidBitFieldException
    {
        return this.mConverter.convertData(data);
    }

    /**
     * Converts the data into a formatted array of bytes.
     * @param data data that is to be formatted.
     * @return Array of bytes that are ready to be sent
     * @throws InvalidBitFieldException if the types don't match up
     */
    public ArrayList<Byte> getRawFromData(String data)throws InvalidBitFieldException
    {
        return this.mConverter.convertData(data);
    }

    /**
     * Gets the CommandId based on the idNumber.
     * @param value The Bitfield Value
     * @return the Bitfield
     * @throws InvalidBitFieldException if it doesn't exist
     */
    public static BitFieldId getBitFieldId(int value) throws InvalidBitFieldException
    {
        //go through all command ids and if it equals then return it.
        for (BitFieldId bitId : BitFieldId.values())
        {
            if(value == bitId.getVal())
            {
                return bitId;
            }
        }

        //error throw exception
        throw new InvalidBitFieldException(value);
    }

    /**
     * Gets the CommandId based on the idNumber.
     * @param section The Bitfield Value
     * @param bit The Bitfield Value
     * @return the Bitfield
     * @throws InvalidBitFieldException if it doesn't exist
     */
    public static BitFieldId getBitFieldId(int section, int bit) throws InvalidBitFieldException
    {
        //go through all command ids and if it equals then return it.
        for (BitFieldId bitId : BitFieldId.values())
        {
            if(section == bitId.getSection() && bit == bitId.getBit())
            {
                return bitId;
            }
        }

        //error throw exception
        throw new InvalidBitFieldException(section, bit);
    }
}
