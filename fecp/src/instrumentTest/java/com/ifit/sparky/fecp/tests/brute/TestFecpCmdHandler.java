/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 2/5/14
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.tests.brute;

import android.content.Context;
import android.test.ActivityTestCase;
import android.test.ServiceTestCase;

import com.ifit.sparky.fecp.CmdHandlerType;
import com.ifit.sparky.fecp.FecpCmdHandler;
import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.FecpController;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.CommType;
import com.ifit.sparky.fecp.interpreter.command.InfoCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import junit.framework.TestCase;

import java.lang.reflect.Method;

public class TestFecpCmdHandler extends ActivityTestCase {

    /**
     * Setups the TestRunner for Command.
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Closes the TestRunner for Command.
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Tests the Constructor.
     * @throws Exception
     */
    public void testFecpCmdHandler_Constructor() throws Exception{
        TestToolDumyCom comTool = new TestToolDumyCom();

        FecpCmdHandler cmdHandler  = new FecpCmdHandler(comTool);

        assertEquals(cmdHandler.getCommController(), comTool);
    }

    /**
     * Tests the Adding a Fecp Command to the Fecp Command Handler.
     * @throws Exception
     */
    public void testFecpCmdHandler_AddFecpCmd() throws Exception{
        TestToolDumyCom comTool = new TestToolDumyCom();
        FecpCommand cmd;//command to validate sent

        FecpCmdHandler cmdHandler  = new FecpCmdHandler(comTool);

        cmd = new FecpCommand(new InfoCmd(DeviceId.TREADMILL));
        assertEquals(0, cmd.getCmdSentCounter());
        assertEquals(0, cmd.getCmdReceivedCounter());
        cmdHandler.addFecpCommand(cmd);
        assertEquals(0, cmd.getCmdSentCounter());
        assertEquals(0, cmd.getCmdReceivedCounter());
        Thread.sleep(100);//need to wait for the command to finish
        assertEquals(1, cmd.getCmdSentCounter());

        cmdHandler.addFecpCommand(cmd);
        assertEquals(1, cmd.getCmdSentCounter());
        Thread.sleep(100);//need to wait for the command to finish
        assertEquals(2, cmd.getCmdSentCounter());
    }
    /**
     * @return The {@link android.content.Context} of the test project.
     */
    private Context getTestContext()
    {
        try
        {
            Method getTestContext = ServiceTestCase.class.getMethod("getTestContext");
            return (Context) getTestContext.invoke(this);
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
            return null;
        }
    }
}
