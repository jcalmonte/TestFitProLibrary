/**
 * The mode of the System.
 * @author Levi.Balling
 * @date 2/19/14
 * @version 1
 * Depending on the mode, you can set specific items.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;

import java.nio.ByteBuffer;

public class ModeConverter extends BitfieldDataConverter {
    private ModeId mMode;

    /**
     * Initializes the Mode converter
     */
    public ModeConverter()
    {
        super();
        this.mMode = ModeId.UNKNOWN;
        this.mDataSize = 1;
        this.mRawData = ByteBuffer.allocate(this.mDataSize);
    }

    @Override
    public BitfieldDataConverter getData() throws Exception {
        int temp = (int)this.getRawToInt();
        this.mMode = ModeId.values()[temp];
        return this;
    }

    @Override
    public ByteBuffer convertData(Object obj) throws InvalidBitFieldException {

        //object needs to be a double
        if(obj.getClass() == ModeId.class)
        {
            this.mMode = (ModeId)obj;
        }
        else if(obj.getClass() == Integer.class)
        {
            this.mMode = ModeId.values()[(Integer)obj];
        }
        else if(obj.getClass() == Double.class)
        {
            Double temp = (Double)obj;
            this.mMode = ModeId.values()[temp.intValue()];
        }
        else
        {
            throw new InvalidBitFieldException( ModeId.class, obj );
        }

        return this.getRawFromData(this.mMode.getValue());
    }

    /**
     * gets the data as an int regardless of size
     * @return the data as an int
     */
    public ModeId getMode()
    {
        return this.mMode;
    }
}
