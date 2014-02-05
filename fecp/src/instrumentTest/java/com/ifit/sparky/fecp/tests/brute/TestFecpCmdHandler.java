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
import com.ifit.sparky.fecp.FecpController;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.CommType;

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
    public void testGetSystemDevice() throws Exception{
        //setup fecp controller

        FecpController controller;
        Context c;
        TempFecpCallbacker callback;
        SystemDevice sysDev;
        c = getTestContext();
        callback = new TempFecpCallbacker();
        controller = new FecpController(c, null, CommType.USB_COMMUNICATION, callback);
//        sysDev = controller.initializeConnection(CmdHandlerType.CMD_TYPE_PRIORITY);//currently doesn't matter

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
