/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 12/12/13
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.interpreter.key;

public class InvalidKeyCodeException extends Exception {

    /**
     * Handles an exception if there is a bad int used.
     * @param badValue bad int used
     */
    public InvalidKeyCodeException(int badValue)
    {
        super("Invalid KeyCode ("+badValue+").");
    }
}