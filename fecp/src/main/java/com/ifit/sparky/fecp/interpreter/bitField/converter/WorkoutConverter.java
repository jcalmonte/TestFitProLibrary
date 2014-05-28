/**
 * The Workout selected for the user
 * @author Levi.Balling
 * @date 5/5/14
 * @version 1
 * The default workout is always Manual. and the user can select it to allow options from ifit.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class WorkoutConverter extends BitfieldDataConverter implements Serializable {
    private WorkoutId mWorkout;

    /**
     * Initializes the Workout converter
     */
    public WorkoutConverter()
    {
        super();
        this.mWorkout = WorkoutId.MANUAL;
        this.mDataSize = 1;
        this.mRawData = ByteBuffer.allocate(this.mDataSize);
    }

    @Override
    public BitfieldDataConverter getData() throws Exception {
        int temp = (int)this.getRawToInt();
        this.mWorkout = WorkoutId.values()[temp];
        return this;
    }

    @Override
    public ByteBuffer convertData(Object obj) throws InvalidBitFieldException {

        //object needs to be a double
        if(obj.getClass() == WorkoutId.class)
        {
            this.mWorkout = (WorkoutId)obj;
        }
        else if(obj.getClass() == Integer.class)
        {
            this.mWorkout = WorkoutId.values()[(Integer)obj];
        }
        else if(obj.getClass() == Double.class)
        {
            Double temp = (Double)obj;
            this.mWorkout = WorkoutId.values()[temp.intValue()];
        }
        else
        {
            throw new InvalidBitFieldException( WorkoutId.class, obj );
        }

        return this.getRawFromData(this.mWorkout.getValue());
    }

    @Override
    public void writeObject(ObjectOutputStream stream) throws IOException {

        stream.writeObject(this.mWorkout);
    }

    @Override
    public void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {

        this.mWorkout = (WorkoutId)stream.readObject();
    }

    /**
     * gets the data as an int regardless of size
     * @return the data as an int
     */
    public WorkoutId getWorkout()
    {
        return this.mWorkout;
    }
}
