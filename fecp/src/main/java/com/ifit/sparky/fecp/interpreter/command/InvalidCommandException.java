/**
 * Invalid Command Exception is to allow for better resources for what is wrong.
 * @author Levi.Balling
 * @date 12/10/13
 * @version 1
 * This will provide what is wrong, and allow for the user to quickly evaluate the issue at hand.
 */
package com.ifit.sparky.fecp.interpreter.command;

public class InvalidCommandException extends Exception {

    /**
     * Exception if there is a bad Command id int
     * @param badId bad int used
     */
    public InvalidCommandException(int badId)
    {
        super("Invalid Command id ("+badId+").");
    }

    /**
     * Invalid Receive command Id for the send Command ID. Need to match up
     * @param sendId the command's commandId
     * @param receiveId the Status's CommandID
     */
    public InvalidCommandException(CommandId sendId, CommandId receiveId)
    {
        super("Invalid Status-CommandId("+receiveId.name()+
                "), doesn't match given CommandId("+sendId.name() +").");
    }

    /**
     * Duplicate CommandIds in a list
     * @param duplicate duplicate Command in the list.
     */
    public InvalidCommandException(Command duplicate)
    {
        super("Can't add command("+ duplicate.getCmdId().name()
                + ") It already is in the list.");
    }
}
