/**
 * Converts Incline values into double values with 0.01% precision.
 * @author Levi.Balling
 * @date 12/11/13
 * @version 1
 * Converts the 2 byte value from a buffer into a incline value from -100.00% to 100.00%.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;

import java.nio.ByteBuffer;

public class InclineConverter extends BitfieldDataConverter {

    private double mIncline;

    public InclineConverter()
    {
        super();
        this.mDataSize = 2;
        this.mIncline = 0;
    }

    @Override
    public BitfieldDataConverter getData() throws Exception
    {
        //need to cast as a signed value of a short.
        short tempShort = (short)this.getRawToInt();
        this.mIncline = tempShort;
        this.mIncline /= 100.0;
        return this;
    }

    @Override
    public ByteBuffer convertData(Object obj) throws InvalidBitFieldException {

        double temp;
        //data coming in as a double
        if(obj.getClass() == Double.class)
        {
            temp = (Double)obj;
        }
        else if(obj.getClass() == Integer.class)
        {
            temp = (Integer)obj + 0.0;
        }
        else
        {
            throw new InvalidBitFieldException( double.class, obj );
        }

        temp *= 100;
        return this.getRawFromData((int)temp);
    }

    /**
     * Gets the incline, doesn't require min or max values
     * @return the incline as a double
     */
    public double getIncline()
    {
        return this.mIncline;
    }
}