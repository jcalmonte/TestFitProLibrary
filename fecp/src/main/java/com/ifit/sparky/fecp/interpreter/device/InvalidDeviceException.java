/**
 * Invalid Device Exception is to allow for better resources for what is wrong.
 * @author Levi.Balling
 * @date 12/10/13
 * @version 1
 * This will provide what is wrong, and allow for the user to quickly evaluate the issue at hand.
 */
package com.ifit.sparky.fecp.interpreter.device;

public class InvalidDeviceException extends Exception {

    /**
     * Handles an exception if there is a bad int used.
     * @param badId bad int used
     */
    public InvalidDeviceException(int badId)
    {
        super("Invalid Device id ("+badId+").");
    }
}
