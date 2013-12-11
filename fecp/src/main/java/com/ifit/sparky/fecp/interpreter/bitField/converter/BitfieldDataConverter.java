/**
 * The Data converter for data.
 * @author Levi.Balling
 * @date 12/11/13
 * @version 1
 * This will provide a set of static require functions to convert raw byte data into the correct
 * format and type.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

import java.util.ArrayList;

public abstract class BitfieldDataConverter {

    protected ArrayList<Byte> mRawData;

    public BitfieldDataConverter()
    {
    }

    public void setRawData(ArrayList<Byte> rawData)
    {
        this.mRawData = rawData;
    }

    public abstract BitfieldDataConverter getData();

}
