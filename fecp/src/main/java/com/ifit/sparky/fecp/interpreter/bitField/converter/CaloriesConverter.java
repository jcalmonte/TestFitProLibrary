/**
 * Converts the Raw Calories data into double.
 * @author Levi.Balling
 * @date 5/6/14
 * @version 1
 * the units of calories are 100,000 equals 10.0000 calories.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;

import java.nio.ByteBuffer;

public class CaloriesConverter extends BitfieldDataConverter {

    private double mCalories;

    public CaloriesConverter()
    {
        super();
        this.mDataSize = 4;
        this.mCalories = 0;
    }

    @Override
    public BitfieldDataConverter getData() throws Exception
    {
        this.mCalories = (double)this.getRawToInt();
        this.mCalories /= 10000;
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
        else if(obj.getClass() == Integer.class)
        {
            temp = (Integer)obj + 0.0;
        }
        else
        {
            throw new InvalidBitFieldException( double.class, obj );
        }
        temp *= 10000;//convert to int
        return this.getRawFromData((int)temp);
    }

    public double getCalories()
    {
        return this.mCalories;
    }

}
