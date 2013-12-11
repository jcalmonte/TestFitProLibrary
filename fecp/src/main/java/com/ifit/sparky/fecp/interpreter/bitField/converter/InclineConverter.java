/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 12/11/13
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;

import java.util.ArrayList;

public class InclineConverter extends BitfieldDataConverter {

    private double mIncline;

    public InclineConverter()
    {
        super();
        this.mIncline = 0;
    }

    @Override
    public BitfieldDataConverter getData() throws Exception
    {
        this.mIncline = this.getRawToInt();
   return this;
    }

    @Override
    public ArrayList<Byte> convertData(Object obj) throws InvalidBitFieldException {
        return null;
    }

    /**
     * Gets the Incline based off of the MaxIncline and the MinIncline
     * @param MaxIncline the Max Incline for the current System
     * @param MinIncline the Min Incline for the current System
     * @return the incline as a double
     */
    public double getIncline(int MaxIncline, int MinIncline)
    {
        //TODO convert based on max and Min
        return this.mIncline;
    }

}