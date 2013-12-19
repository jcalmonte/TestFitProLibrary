/**
 * Temp Testing callback object.
 * @author Levi.Balling
 * @date 12/19/13
 * @version 1
 * This callback was made to validate the callback for the Command Callback.
 */
package com.ifit.sparky.fecp.tests.brute;

import com.ifit.sparky.fecp.CommandCallback;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;

public class TempFecpCallbacker implements CommandCallback {

    private boolean itWorks;
    private CommandId id;//temp id to make sure the command was sent

    /**
     * simple constructor for the callback.
     */
    public TempFecpCallbacker()
    {
        this.itWorks = false;
    }

    /**
     * Handles the reply from the device
     *
     * @param cmd the command that was sent.
     */
    @Override
    public void msgHandler(Command cmd)
    {
        this.itWorks = (cmd.getCmdId() == this.id);
    }

    /**
     * sets the commandId to validate it is correct.
     * @param id The commandId to check
     */
    public void setCmdId(CommandId id)
    {
        this.id = id;
    }

    /**
     * gets the Test status, to make sure it passed
     * @return the boolean status
     */
    public boolean getWorksStatus()
    {
        return this.itWorks;
    }
}
