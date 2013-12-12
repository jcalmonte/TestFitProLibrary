/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 12/11/13
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class SpeedConverter extends BitfieldDataConverter {

    private double mSpeed;

    public SpeedConverter()
    {
        super();
        this.mDataSize = 2;
        this.mSpeed = 0;
    }

    @Override
    public BitfieldDataConverter getData() throws Exception
    {
        this.mSpeed = (double)this.getRawToInt();
        this.mSpeed /= 10;
        return this;
    }

    @Override
    public ByteBuffer convertData(Object obj) throws InvalidBitFieldException {
        double temp;
        //object needs to be a double
        if(obj.getClass() == Double.class)
        {
            temp = (Double)obj;
        }
        if(obj.getClass() == Integer.class)
        {
            temp = (Integer)obj + 0.0;
        }
        else
        {
            throw new InvalidBitFieldException( double.class, obj );
        }
        temp *= 10;//convert to int
        return this.getRawFromData((int)temp);
    }

    public double getSpeed()
    {
        return this.mSpeed;
    }

}
