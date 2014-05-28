/**
 * Converts the data into its values.
 * @author Levi.Balling
 * @date 12/12/13
 * @version 1
 * Converts the value into its exact value.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class LongConverter extends BitfieldDataConverter implements Serializable {

    private int mData;
    public LongConverter()
    {
        super();
        this.mData = 0;
        this.mDataSize = 4;
        this.mRawData = ByteBuffer.allocate(this.mDataSize);
    }

    @Override
    public BitfieldDataConverter getData() throws Exception {
        this.mData = (int)this.getRawToInt();
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
        return this.getRawFromData((int)temp);
    }


    @Override
    public void writeObject(ObjectOutputStream stream) throws IOException {

        stream.writeInt(this.mData);
    }

    @Override
    public void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {

        this.mData = stream.readInt();
    }

    /**
     * gets the data as an int regardless of size
     * @return the data as an int
     */
    public int getValue()
    {
        return this.mData;
    }
}