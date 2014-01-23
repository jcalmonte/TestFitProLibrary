/**
 * Converts the raw data into a KeyObject.
 * @author Levi.Balling
 * @date 12/12/13
 * @version 1
 * Converts the raw data into a KeyObject, that holds the Cooked keycode, the rawKeyCode data,
 * the time it was pressed in respects to the beginning of the workout, and how long the button was
 * held for in milliseconds.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;
import com.ifit.sparky.fecp.interpreter.key.KeyObject;

import java.nio.ByteBuffer;

public class KeyObjectConverter extends BitfieldDataConverter {

    private KeyObject mKey;

    /**
     * the default constructor
     */
    public KeyObjectConverter()
    {
        super();
        this.mDataSize = 10;
        this.mKey = new KeyObject();
    }

    @Override
    public BitfieldDataConverter getData() throws Exception
    {
        this.mRawData.position(0);

        //convert the Next 2 bytes into the CookedKeycode
        this.mKey.setCode(this.mRawData.getShort());

        //convert the first 4 bytes into the rawKeycode value
        this.mKey.setRawKeyCode(this.mRawData.getLong());

        //convert the Next 2 bytes into the Time it was pressed in seconds
        this.mKey.setTimePressed(this.mRawData.getShort());

        //converts the Next 2 Bytes into how long it was held
        this.mKey.setTimeHeld(this.mRawData.getShort());

        return this;
    }

    @Override
    public ByteBuffer convertData(Object obj) throws InvalidBitFieldException {

        throw new InvalidBitFieldException("KeyCodes bitfield doesn't support converting " +
                "into Raw Data");
    }

    /**
     * gets the KeyObject
     * @return the KeyObject
     */
    public KeyObject getKeyObject()
    {
        return this.mKey;
    }

}