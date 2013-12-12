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
        //Todo all of the following
        //convert the first 4 bytes into the rawKeycode value

        //convert the Next 2 bytes into the CookedKeycode

        //convert the Next 2 bytes into the Time it was pressed in seconds

        //converts the Next 2 Bytes into how long it was held


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