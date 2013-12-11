/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 12/11/13
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

import java.util.ArrayList;

public class SpeedConverter extends BitfieldDataConverter {

    private double mSpeed;

    public SpeedConverter()
    {
        super();
        this.mSpeed = 0;
    }

    @Override
    public BitfieldDataConverter getData()
    {
        int rawSpeed;
        int high =this.mRawData.get(1);//Little Endian
        int low  =this.mRawData.get(0);

        rawSpeed= ((high & 0xFF) << 8) | (low & 0xFF);
        //convert the 2 bytes into MPH
        this.mSpeed = (double)rawSpeed;
        this.mSpeed /= 10;
        return this;
    }

    public double getSpeed()
    {
        return this.mSpeed;
    }

}
