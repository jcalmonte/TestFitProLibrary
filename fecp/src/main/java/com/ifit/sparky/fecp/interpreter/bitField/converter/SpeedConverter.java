/**
 * Converts the Speed data into double.
 * @author Levi.Balling
 * @date 12/11/13
 * @version 1
 * the units of Speed are 100 equals 10.0.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class SpeedConverter extends BitfieldDataConverter implements Serializable {

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
        this.mSpeed /= 100;
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
        temp *= 100;//convert to int
        return this.getRawFromData((int)temp);
    }

    @Override
    public void writeObject(ObjectOutputStream stream) throws IOException {

        stream.writeDouble(this.mSpeed);
    }

    @Override
    public void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {

        this.mSpeed = stream.readDouble();
    }

    public double getSpeed()
    {
        return this.mSpeed;
    }

}
