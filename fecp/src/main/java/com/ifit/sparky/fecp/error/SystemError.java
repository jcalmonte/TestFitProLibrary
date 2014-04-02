/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 4/2/2014
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.error;

public class SystemError {

    private ErrorCode mErrCode;
    private int mLineNumber;
    private String mFileName;
    private String mFunctionName;


    public SystemError()
    {
        this.mErrCode = ErrorCode.NONE;
        this.mFileName = "";
        this.mFunctionName = "";
        this.mLineNumber = 0;
    }


}
