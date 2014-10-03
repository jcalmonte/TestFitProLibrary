package com.ifit.sfit.sparky.helperclasses;

import android.app.Activity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.ScrollView;

import com.ifit.sfit.sparky.testsdrivers.BaseTest;
import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.FecpController;


/**
 * Created by jc.almonte on 7/30/14.
 * Common features shared among all test classes
 */
public abstract class CommonFeatures extends Activity {

    protected FecpController mFecpController;
    protected BaseTest mAct;
    protected HandleCmd hCmd;
    protected SFitSysCntrl mSFitSysCntrl;
    protected SystemDevice MainDevice;

    protected FecpCommand wrCmd;
    protected FecpCommand rdCmd;
    /**
     * Takes care of displaying on screen the test results sent through "msg" param
     * @param msg
     */

    public void appendMessage(final String msg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
              BaseTest.testingView.append(Html.fromHtml(msg));
              BaseTest.testingView.setMovementMethod(LinkMovementMethod.getInstance());
              BaseTest.scrollview.post(new Runnable() {
                   @Override
                   public void run() {
                       BaseTest.scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                   }
               });
            }
        });
    }

    /**
     * Creates a text template for the test's Results Summary
     * @param testValidation the validation result of the test
     * @param currentVersion current software version(s)
     * @param gitHubWikiName string to complete Github wiki link with current test info
     * @param failsCount rate of fail tests (failtests out of Total tests)
     * @param issuesList string containing list of issues found on test
     * @return the completed template
     */
    public String resultsSummaryTemplate(String testValidation, String currentVersion, String gitHubWikiName, int failsCount, String issuesList,String issuesListHtml, int totalTestsCount)
    {
        String results="";
        appendMessage("<br><br><b>RESULTS SUMMARY</b><br><br>");
        appendMessage("<b>Test Wiki:</b> <a href=\"https://github.com/jcalmonte/TestFitProLibrary/wiki/"+gitHubWikiName+"\">"+BaseTest.filename.replace(".txt"," ")+"</a><br>");
        appendMessage("<b>Current Version: </b>"+currentVersion+"&nbsp;&nbsp;&nbsp;<b>Test Validation:</b> "+testValidation+"<br>");
        appendMessage("<b>Fail rate:</b> "+failsCount+"/"+totalTestsCount+"<br>");
        appendMessage("<b>List of Found Issues:</b>"+issuesListHtml+"<br><br>");

        results+="\n\nRESULTS SUMMARY\n\n";
        results+="Test Wiki: https://github.com/jcalmonte/TestFitProLibrary/wiki/"+gitHubWikiName+"\n";
        results+="Current Version: "+currentVersion+"\tTest Validation: "+testValidation+"\n";
        results+="Fail rate: "+failsCount+"/"+totalTestsCount+"\n";
        results+="List of Found Issues: "+issuesList+"\n";

        return results;
    }

    /**
     * Used to run all tests for each class.
     * Declared abstract since every TestClass runs a different set of tests
     * @return text log of test results
     * @throws Exception
     */
   public abstract String runAll() throws Exception;
}
