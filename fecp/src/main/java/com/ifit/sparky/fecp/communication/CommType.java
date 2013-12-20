/**
 * Determines the communication medium.
 * @author Levi.Balling
 * @date 12/19/13
 * @version 1
 * enum representing what communication is to be used.
 */
package com.ifit.sparky.fecp.communication;

public enum CommType {
    USB_COMMUNICATION(),
    UART_COMMUNICATION(),
    BT_COMMUNICATION(),
    NFC_COMMUNICATION();

    /**
     * Default CommType enum
     */
    CommType()
    {

    }
}
