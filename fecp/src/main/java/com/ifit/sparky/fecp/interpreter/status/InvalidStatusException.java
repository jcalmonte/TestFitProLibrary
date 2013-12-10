/**
 * Handles the Invalid Status Errors.
 * @author Levi.Balling
 * @date 12/10/13
 * @version 1
 * Handles the errors dealing with the Status.
 */
package com.ifit.sparky.fecp.interpreter.status;

public class InvalidStatusException extends Exception {

    /**
     * Invalid StatusId int was used.
     * @param badId bad int for the StatusId
     */
    public InvalidStatusException(int badId)
    {
        super("Invalid Status id ("+badId+").");
    }
}