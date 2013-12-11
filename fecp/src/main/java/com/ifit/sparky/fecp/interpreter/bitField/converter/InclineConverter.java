/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 12/11/13
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

public class InclineConverter extends BitfieldDataConverter {

    private double mIncline;

    public InclineConverter()
    {
        super();
        this.mIncline = 0;
    }

    @Override
    public BitfieldDataConverter getData()
    {
        int rawIncline;

        rawIncline= this.mRawData.get(0);
        return this;
    }

    public double getSpeed(int MaxIncline, int MinIncline)
    {
        //TODO based on the max and min calculate the incline
        return this.mIncline;
    }

}