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

import java.nio.ByteBuffer;
import java.util.ArrayList;

public enum BitFieldId {
    TARGET_MPH(0, 2, false, new SpeedConverter(), "Target Speed"),
    CURRENT_MPH(1, 2, true, new SpeedConverter(), "Current Speed"),
    TARGET_INCLINE(2, 2, false, new InclineConverter(), "Target Incline"),
    CURRENT_INCLINE(3, 2, true, new InclineConverter(), "Current Incline"),
    TARGET_VOLUME(4, 1, false, new ByteConverter(), "Target Volume"),
    CURRENT_VOLUME(5, 1, true, new ByteConverter(), "Current Volume"),
    TARGET_FAN_SPEED(6, 1, false, new ByteConverter(), "Target Fan Speed"),
    CURRENT_FAN_SPEED(7, 1, true, new ByteConverter(), "Current fan Speed"),
    TARGET_RESISTANCE(8, 2, false, new ResistanceConverter(), "Target Resistance"),
    CURRENT_RESISTANCE(9, 2, true, new ResistanceConverter(), "Current Resistance"),
    TARGET_WATTS(10, 2, false, new ShortConverter(), "Target Watts"),
    CURRENT_WATTS(11, 2, true, new ShortConverter(), "Current Watts"),
    TARGET_TORQUE(12, 2, false, new ShortConverter(), "Target Torque"),
    CURRENT_TORQUE(13, 2, true, new ShortConverter(), "Current Torque"),
    CURRENT_KEYCODE(14, 4, true, new LongConverter(), "Current Keycode"),
    COOKED_KEYCODE(15, 2, true, new ShortConverter(), "Cooked Keycode"),
    KEY_BEEP(16, 1, true, new ByteConverter(), "Key Beep"),
    ANDROID_KEYS(17, 1, true, new ByteConverter(), "Android Keys"),
    MODE_FLAGS(18, 1, true, new ByteConverter(), "Mode Flags"),
    STATUS(19, 1, true, new ByteConverter(), "Status"),
    CURRENT_BV_FREQUENCY(20, 2, true, new ShortConverter(), "Current BroadCast Vision Frequency"),
    CURRENT_BV_VOLUME(21, 1, true, new ByteConverter(), "Current BroadCast Vision Volume"),
    CURRENT_AUDIO_SOURCE(22, 1, true, new ByteConverter(), "Current Audio Source"),//TODO create enum for this
    TARGET_GEARS(23, 1, false, new ByteConverter(), "Target Gears"),
    SAFETY_FENCE(24, 2, true, new ShortConverter(), "Safety Fence"),
    UP_RIGHTS(25, 2, true, new ShortConverter(), "Up Right Post Adjusters"),
    TILT(26, 2, true, new ShortConverter(), "Tilt"),
    MAX_RESISTANCE(27, 2, true, new ResistanceConverter(), "Max Resistance"),
    CURRENT_RPM(28, 1, true, new ByteConverter(), "Current RPM"),
    CURRENT_PULSE(29, 1, true, new ByteConverter(), "Current Pulse"),
    INCLINE_TRANSMAX(30, 2, false, new ShortConverter(), "Incline Transmax"),
    MIN_RESISTANCE(31, 2, true, new ResistanceConverter(), "Min Resistance"),
    INCLINE_TACH(32, 1, true, new ByteConverter(), "Incline Tach"),
    TARGET_STRIDE_LENGTH(33, 1, false, new ByteConverter(), "Target Stride Length"),
    CURRENT_STRIDE_POSITION(34, 1, true, new ByteConverter(), "Current Stride Position"),
    CURRENT_STRIDE_DIRECTION(35, 1, true, new ByteConverter(), "Current Stride Direction"),
    CURRENT_STRIDE_LENGTH(36, 1, true, new ByteConverter(), "Current Stride Length"),
    CURRENT_STRIDE_SPEED(37, 1, true, new ByteConverter(), "Current Stride Speed"),
    LED_BANK(38, 1, true, new ByteConverter(), "LED Bank"),
    LED_MASK(39, 4, true, new LongConverter(), "LED Mask"),
    CURRENT_RAW_PULSE(40, 1, true, new ByteConverter(), "Current Raw Pulse"),
    CURRENT_VSENSE(41, 1, true, new ByteConverter(), "Current Voltage Sense"),
    WORKOUT_STATE(42, 1, false, new ByteConverter(), "Workout State"),
    MIN_INCLINE(43, 2, true, new InclineConverter(), "Min Incline"),
    MAX_INCLINE(44, 2, true, new InclineConverter(), "Max Incline"),
    STOP_INCLINE(45, 2, true, new InclineConverter(), "Stop Incline"),
    ACTUAL_MAX_INCLINE(46, 2, true, new InclineConverter(), "Actual Max Incline");

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
    public ByteBuffer getRawFromData(String data)throws InvalidBitFieldException
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
