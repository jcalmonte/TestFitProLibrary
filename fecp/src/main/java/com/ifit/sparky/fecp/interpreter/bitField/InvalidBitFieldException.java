/**
 * Invalid Bitfield Exception for errors dealing with the Bitfield.
 * @author Levi.Balling
 * @date 12/10/13
 * @version 1
 * If there is a Invalid Bitfield this is the exception to use for it.
 */
package com.ifit.sparky.fecp.interpreter.bitField;

public class InvalidBitFieldException extends Exception {

    /**
     * Handles an exception if there is a bad int used.
     * @param badId bad int used
     */
    public InvalidBitFieldException(int badId)
    {
        super("Invalid Bitfield id ("+badId+").");
    }
}
