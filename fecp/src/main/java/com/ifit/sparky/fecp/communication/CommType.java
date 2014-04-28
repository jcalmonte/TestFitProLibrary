/**
 * Determines the communication medium.
 * @author Levi.Balling
 * @date 12/19/13
 * @version 1
 * enum representing what communication is to be used. Will add as supported
 */
package com.ifit.sparky.fecp.communication;

public enum CommType {
    USB_COMMUNICATION(),
    TESTING_COMM();

    /**
     * Default CommType enum
     */
    CommType()
    {

    }
}
