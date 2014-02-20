/**
 * This is all the main Data Bit values.
 * @author Levi.Balling
 * @date 12/11/13
 * @version 1
 * This will hold all the major values for controlling the Data Bit.
 */
package com.ifit.sparky.fecp.interpreter.bitField;

import com.ifit.sparky.fecp.interpreter.bitField.converter.*;

import java.nio.ByteBuffer;

public enum BitFieldId {
    KPH(0, 2, false, new SpeedConverter(), "KPH"),
    INCLINE(1, 2, false, new InclineConverter(), "Incline"),
    RESISTANCE(2, 2, false, new ResistanceConverter(), "Resistance"),
    WATTS(3, 2, true, new ShortConverter(), "Watts"),
    TORQUE(4, 2, true, new ShortConverter(), "Torque"),
    RPM(5, 1, true, new ByteConverter(), "RPM"),
    DISTANCE(6, 4, true, new LongConverter(), "Distance"),
    KEY_OBJECT(7, 14, true, new KeyObjectConverter(), "Key Object"),
    FAN_SPEED(8, 1, false, new ByteConverter(), "Fan Speed"),
    VOLUME(9, 1, false, new ByteConverter(), "Volume"),
    PULSE(10, 1, true, new ByteConverter(), "Pulse"),
    RUNNING_TIME(11, 4, true, new LongConverter(), "Running Time"),
    WORKOUT_MODE(12, 1, false, new ModeConverter(), "Workout Mode"),
    CALORIES(13, 2, true, new ShortConverter(), "Calories"),
    AUDIO_SOURCE(14, 1, false, new ByteConverter(), "Audio Source"),
    ANDROID_KEYS(15, 1, true, new ByteConverter(), "Android Keys"),
    ACTUAL_KPH(16, 2, true, new SpeedConverter(), "Actual KPH"),
    ACTUAL_INCLINE(17, 2, true, new InclineConverter(), "Actual Incline"),
    ACTUAL_RESISTANCE(18, 2, true, new ResistanceConverter(), "Actual Resistance"),
    ACTUAL_DISTANCE(19, 4, true, new LongConverter(), "Actual Distance"),

    AGE(24, 1, false, new ByteConverter(), "Age"),
    WEIGHT(25, 2, false, new ShortConverter(), "Weight"),
    GEARS(26, 1, false, new ByteConverter(), "Gears"),
    MAX_INCLINE(27, 2, true, new InclineConverter(), "Max Incline"),
    MIN_INCLINE(28, 2, true, new InclineConverter(), "Min Incline"),
    TRANS_MAX(29, 2, false, new ShortConverter(), "Trans Max"),
    MAX_KPH(30, 2, true, new SpeedConverter(), "Max KPH"),
    MIN_KPH(31, 2, true, new SpeedConverter(), "Min KPH"),
    BV_VOLUME(32, 1, false, new ByteConverter(), "Broadcast Vision Volume"),
    BV_FREQUENCY(33, 2, false, new ShortConverter(), "Broadcast Vision Frequency"),
    IDLE_TIMEOUT(34, 2, false, new ShortConverter(), "Idle Timeout"),
    PAUSE_TIMEOUT(35, 2, false, new ShortConverter(), "Pause Timeout");

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

    /**
     * Gets a Bitfield data Converter for converting the data correctly
     * @param rawData the raw buffer data at the correct location in the buffer
     * @return the data as an Bitfield data converter
     * @throws Exception
     */
    public BitfieldDataConverter getData(ByteBuffer rawData) throws Exception
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
    public ByteBuffer getRawFromData(int data)throws InvalidBitFieldException
    {
        return this.mConverter.convertData(data);
    }

    /**
     * Converts the data into a formatted array of bytes.
     * @param data data that is to be formatted.
     * @return Array of bytes that are ready to be sent
     * @throws InvalidBitFieldException if the types don't match up
     */
    public ByteBuffer getRawFromData(double data)throws InvalidBitFieldException
    {
        return this.mConverter.convertData(data);
    }

    /**
     * Converts the data into a formatted array of bytes.
     * @param data data that is to be formatted.
     * @return Array of bytes that are ready to be sent
     * @throws InvalidBitFieldException if the types don't match up
     */
    public ByteBuffer getRawFromData(Object data)throws InvalidBitFieldException
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
