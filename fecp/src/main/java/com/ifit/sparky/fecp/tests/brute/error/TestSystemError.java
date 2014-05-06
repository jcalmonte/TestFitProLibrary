/**
 * Tests all the items in the Error Code enum
 * @author Levi.Balling
 * @date 12/9/2013
 * @version 1
 * Release Date
 * @date 12/10/13
 * This will valitade the the getting a value from the ErrorCode Static function
 */
package com.ifit.sparky.fecp.tests.brute.error;

import com.ifit.sparky.fecp.error.ErrorCode;
import com.ifit.sparky.fecp.error.ErrorMsgType;
import com.ifit.sparky.fecp.error.SystemError;

import junit.framework.TestCase;

public class TestSystemError extends TestCase{

    /** Tests the Constructors, sets, and Gets
     *
     * @throws Exception
     */
    public void testSystemError_Constructor() throws Exception{

        SystemError error = new SystemError();

        //default constructor
        assertEquals(error.getErrCode(), ErrorCode.NONE);
        assertEquals(error.getErrorNumber(), 0);
        assertEquals(error.getErrorTime().getTimeInMillis(), 0);
        assertEquals(error.getErrorType(), ErrorMsgType.DEFAULT);
        assertEquals(error.getFileName(), "");
        assertEquals(error.getFunctionName(), "");
        assertEquals(error.getLineNumber(), 0);


        //skipped Msg type since we only have one
        error = new SystemError(ErrorMsgType.DEFAULT);

        //validate Msg
        assertEquals(error.getErrCode(), ErrorCode.NONE);
        assertEquals(error.getErrorNumber(), 0);
        assertEquals(error.getErrorTime().getTimeInMillis(), 0);
        assertEquals(error.getErrorType(), ErrorMsgType.DEFAULT);
        assertEquals(error.getFileName(), "");
        assertEquals(error.getFunctionName(), "");
        assertEquals(error.getLineNumber(), 0);

        //skipped Msg type since we only have one
        error = new SystemError(ErrorMsgType.DEFAULT, ErrorCode.BAD_CHECKSUM_ERROR);

        //validate ErrorCode
        assertEquals(error.getErrCode(), ErrorCode.BAD_CHECKSUM_ERROR);
        assertEquals(error.getErrorNumber(), 0);
        assertEquals(error.getErrorTime().getTimeInMillis(), 0);
        assertEquals(error.getErrorType(), ErrorMsgType.DEFAULT);
        assertEquals(error.getFileName(), "");
        assertEquals(error.getFunctionName(), "");
        assertEquals(error.getLineNumber(), 0);


        error = new SystemError(ErrorMsgType.DEFAULT, ErrorCode.NONE, 123);
        //validate Line Number
        assertEquals(error.getErrCode(), ErrorCode.NONE);
        assertEquals(error.getErrorNumber(), 0);
        assertEquals(error.getErrorTime().getTimeInMillis(), 0);
        assertEquals(error.getErrorType(), ErrorMsgType.DEFAULT);
        assertEquals(error.getFileName(), "");
        assertEquals(error.getFunctionName(), "");
        assertEquals(error.getLineNumber(), 123);

        error = new SystemError(ErrorMsgType.DEFAULT, ErrorCode.NONE, 123, "HELLO");
        //validate File Name
        assertEquals(error.getErrCode(), ErrorCode.NONE);
        assertEquals(error.getErrorNumber(), 0);
        assertEquals(error.getErrorTime().getTimeInMillis(), 0);
        assertEquals(error.getErrorType(), ErrorMsgType.DEFAULT);
        assertEquals(error.getFileName(), "HELLO");
        assertEquals(error.getFunctionName(), "");
        assertEquals(error.getLineNumber(), 123);

        error = new SystemError(ErrorMsgType.DEFAULT, ErrorCode.NONE, 123, "HELLO", "WORLD");
        //validate Function
        assertEquals(error.getErrCode(), ErrorCode.NONE);
        assertEquals(error.getErrorNumber(), 0);
        assertEquals(error.getErrorTime().getTimeInMillis(), 0);
        assertEquals(error.getErrorType(), ErrorMsgType.DEFAULT);
        assertEquals(error.getFileName(), "HELLO");
        assertEquals(error.getFunctionName(), "WORLD");
        assertEquals(error.getLineNumber(), 123);

    }

}
