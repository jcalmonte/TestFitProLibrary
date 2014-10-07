package com.ifit.sfit.sparky.tests;

import com.ifit.sfit.sparky.helperclasses.CommonFeatures;
import com.ifit.sfit.sparky.helperclasses.HandleCmd;
import com.ifit.sfit.sparky.helperclasses.SFitSysCntrl;
import com.ifit.sfit.sparky.testsdrivers.BaseTest;
import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeId;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.StatusId;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Calendar;

/**
 * Created by jc.almonte on 7/14/14.
 */
public class TestIncline extends CommonFeatures {

    private String testValidation = "", currentVersion="", gitHubWikiName="", issuesListHtml="", issuesList="";
    private int failsCount = 0, totalTestsCount = 0;

    private FecpCommand calibrateCmd;
    private FecpCommand sendKeyCmd;
    private String currentWorkoutMode = "";
    private double currentIncline = 0.0;
    private double actualInlcine = 0.0;
    private final int NUM_TESTS = 1;
    private DeviceId devId;
    private String emailAddress;

    public TestIncline(FecpController fecpController, BaseTest act, SFitSysCntrl ctrl) {
        //Get controller sent from the main activity (TestApp)
        try {
            this.mFecpController = fecpController;
            this.mAct = act;
            this.mSFitSysCntrl = ctrl;
            this.emailAddress = "jc.almonte@iconfitness.com";
            hCmd = new HandleCmd(this.mAct);// Init handlers
            //Get current system device
            MainDevice = this.mFecpController.getSysDev();
            devId = MainDevice.getInfo().getDevId(); // Get type of machine (Treadmill, Incline trainer, Eliptical...)
            this.currentVersion = "SAL v"+ String.valueOf(mFecpController.getVersion());
            ByteBuffer secretKey = ByteBuffer.allocate(32);
            for(int i = 0; i < 32; i++)
            {
                secretKey.put((byte)i);
            }
            try {
                //unlock the system
                this.mSFitSysCntrl.getFitProCntrl().unlockSystem(secretKey);
                Thread.sleep(1000);
                //Get current system device
                MainDevice = this.mFecpController.getSysDev();
                this.currentVersion = "SAL v"+ String.valueOf(mFecpController.getVersion());
                this.wrCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);
                this.rdCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd,0,100);
                ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.MAX_GRADE);
                ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.MIN_GRADE);
                ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.ACTUAL_INCLINE);
                ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.GRADE);
                ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
                ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.TRANS_MAX);
                ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.KPH);
                ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.MAX_KPH);
                ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.MIN_KPH);


                mSFitSysCntrl.getFitProCntrl().addCmd(rdCmd);
                Thread.sleep(1000);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Verify all incline values can be set properly and incline changes accordingly
     * @return text log of test results
     * @throws Exception
     */
    public String inclineController() throws Exception{
        //outline for code support #928 in redmine
        //Read the Max Incline value from the brainboard
        //Read the Min Incline value from the brainboard
        //Read the TransMax value from the brainboard
        //Set the incline to Max Incline
        //Read current sent incline
        //Read actual incline
        //Check current sent incline against actual incline
        //Run the above logic for the entire range of incline values from Max Incline to Min Incline in 0.5% grade steps

        String results="";

        gitHubWikiName = "Incline%20Controller";
        testValidation ="PASSED";
        issuesListHtml = "";
        issuesList = "";
        failsCount=0;
        totalTestsCount = 0;
        mAct.filename = "Incline Controller.txt";

        appendMessage("<br>----------------------INCLINE CONTROLLER TEST RESULTS----------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        results+="\n----------------------INCLINE CONTROLLER TEST RESULTS----------------------\n\n";
        results+=Calendar.getInstance().getTime() + "\n\n";

        final double MAX_INCLINE;
        final double MIN_INCLINE;
        double currentActualIncline;
        double transMax;
        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();
        long time = 500;

        MIN_INCLINE = hCmd.getMinIncline();
        MAX_INCLINE = hCmd.getMaxIncline();
        appendMessage("Min Incline is " + MIN_INCLINE + "%<br>");

        results+="Min Incline is " + MIN_INCLINE + "%\n";

        System.out.println("Min Incline is " + MIN_INCLINE + "%<br>");


        //If TransMax is ever set to a ridiculously high number or something, you can reset it to this more reasonable TransMax value
        //fixed but sometimes reading 92 instead of 183
        //((WriteReadDataCmd)setTransMax.getCommand()).addWriteData(BitFieldId.TRANS_MAX, 183);
        //mFecpController.addCmd(setTransMax);
        //Thread.sleep(1000);

        transMax = hCmd.getTransMax();
        appendMessage("TransMax is " + transMax + "<br><br>");

        results+="TransMax is " + transMax + "\n\n";

        System.out.println("TransMax is " + transMax + "%<br>");

        //--------------------------------------------------------------------------------------------------------------//
        //Run through all incline settings, going from -3% to 15% (hard-coded until min and max incline are implemented)//
        //--------------------------------------------------------------------------------------------------------------//
        long elapsedTime = 0;
        double seconds = 0;
        long startime = 0;
        for(int i = 0; i < NUM_TESTS; i++)
        {
            //Set MAx incline harcoded to be 15% since that is our motor's max capacity.
            //This value "J" will be set to MAX_INCLINE later on when we use it on a motor with higher incline range
            for(double j = 15; j >= MIN_INCLINE; j = j-0.5) //TODO: Replace "J = 15" with "J = MAX_INCLINE" once we have an incline motor that goes to 40%
            {
                appendMessage("Sending a command to set incline to " + j + "%<br>");

                results+="Sending a command to set incline to " + j + "%\n";

            do{
                //Set value for the incline
                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, j);
                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                Thread.sleep(time);
                appendMessage("Status of setting incline to " + j + "%: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
                results+="Status of setting incline to " + j + "%: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";

            }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

            //Check status of the command to send the incline
                appendMessage("Checking incline will reach set value...<br>");
                results+="Checking incline will reach set value...\n";
                startime= System.nanoTime();
                do
                {
                    currentActualIncline = hCmd.getActualIncline();
                    Thread.sleep(300);
                    appendMessage("Current Incline is: " + currentActualIncline+ " goal: " + j+" time elapsed: "+String.format("%.2f",seconds)+" secs<br>");
                    results+="Current Incline is: " + currentActualIncline+ " goal: " + j+" time elapsed: "+String.format("%.2f",seconds)+" secs\n";
                    elapsedTime = System.nanoTime() - startime;
                    seconds = elapsedTime / 1.0E09;
                } while(j!=currentActualIncline && seconds < 60);//Do while the incline hasn't reached its point yet o.r took more than 1 mins

                currentWorkoutMode = "Workout mode of incline at " + j + "% is " + hCmd.getMode() + "<br>";
                appendMessage(currentWorkoutMode);

                results+="Workout mode of incline at " + j + "% is " + hCmd.getMode() + "\n";

                //Read the actual incline off of device
                actualInlcine = hCmd.getActualIncline();

                if(j == actualInlcine)
                {
                    appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");

                    appendMessage("The current actual incline matches set value: " + actualInlcine + "%<br><br>");



                    results+="\n* PASS *\n\n";
                    results+="The current actual incline matches set value: "  + actualInlcine + "%\n\n";

                }
                else
                {
                    appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>The incline is off by " + (j - actualInlcine) + "%<br><br>");
                    appendMessage("The current actual incline "+actualInlcine + " DOES NOT match set value: " + j + "%<br><br>");
                    issuesListHtml+="<br>- "+"The current actual incline "+actualInlcine + " DOES NOT match set value: " + j + "%<br>";
                    results+="\n* FAIL *\n\nThe incline is off by " + (j - actualInlcine) + "%\n\n";
                    results+="The current actual incline "+actualInlcine + " DOES NOT match set value: " + j + "%\n\n";
                    issuesListHtml+="\n- "+"The current actual incline "+actualInlcine + " DOES NOT match set value: " + j + "%\n";
                    testValidation = "FAILED";
                    failsCount++;

                }
                totalTestsCount++;
                Thread.sleep(3000);
            }
        }

        do{
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(time);
        appendMessage("<br>Status of settting mode to PAUSE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
        results+="\nStatus of settting mode to pause"+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";

    }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

    do{
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(time);
        appendMessage("<br>Status of settting mode to RESULTS "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
        results+="\nStatus of settting mode to RESULTS"+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";

    }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again
    do{
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(time);
        appendMessage("<br>Status of settting mode to IDLE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
        results+="\nStatus of settting mode to IDLE"+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";

    }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

        timeOfTest = System.nanoTime() - startTestTimer;
        timeOfTest = timeOfTest / 1.0E09;

        appendMessage("<br>This test took a total of "+String.format("%.2f",timeOfTest)+" secs <br>");
        results+="\nThis test took a total of "+String.format("%.2f",timeOfTest)+" secs \n";
        results+=resultsSummaryTemplate(testValidation,currentVersion,gitHubWikiName,failsCount,issuesList,issuesListHtml,totalTestsCount);
        return results;
    }


    /**
     * Verifies the incline stops going as soon as stop button is pressed
     * @return text log of test results
     * @throws Exception
     */
    public String stopIncline() throws Exception{
        //Redmine Support #1182
        //Set Incline to 0 or Min Incline
        //Set Incline to Max Incline
        //Send Stop key command before the incline has reached Max
        //Read the Incline
        //Validate that the Incline is not set to the Max Incline
        //Set Incline to Max Incline
        //Set Incline to Min or 0
        //Send Stop key command before the incline has reached Min
        //Read the Incline
        //Validate that the Incline is not set to the Min Incline
        
        String results="";
        gitHubWikiName = "Stop-Incline";
        testValidation ="PASSED";
        issuesListHtml = "";
        issuesList = "";
        failsCount=0;
        totalTestsCount = 0;
        mAct.filename = "Stop Incline.txt";

        final double MAX_INCLINE = hCmd.getMaxIncline();;
        final double MIN_INCLINE = hCmd.getMinIncline();
        double maxToMinIncline1;
        double maxToMinIncline2;
        double minToMaxIncline1;
        double minToMaxIncline2;

        appendMessage("<br><br>----------------------STOP INCLINE TEST RESULTS----------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        results+="\n\n----------------------STOP INCLINE TEST RESULTS----------------------\n\n";
        results+=Calendar.getInstance().getTime() + "\n\n";

        double currentActualIncline;
        long elapsedTime = 0, startime = 0;
        double seconds = 0;
        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();
        double setIncline;
        long time = 500;


        appendMessage("Max Incline is " + MAX_INCLINE + "%<br>");

        results+="Max Incline is " + MAX_INCLINE + "%\n";

        System.out.println("Max Incline is " + MAX_INCLINE + "%<br>");


        appendMessage("Min Incline is " + MIN_INCLINE + "%<br>");

        results+="Min Incline is " + MIN_INCLINE + "%\n";


        System.out.println("MinIncline is " + MIN_INCLINE + "%<br>");
        do{
            //Set Incline to Min Incline
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, MIN_INCLINE);
            mFecpController.addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("Status of setting incline to min: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
            results+="Status of setting incline to min: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again


        appendMessage("Checking incline will reach set value...<br>");
        results+="Checking incline will reach set value...\n";


        //check actual incline until value reaches MIN_INCLINE
        startime = System.nanoTime();
        do
        {
            currentActualIncline = hCmd.getActualIncline();
            Thread.sleep(300);
            appendMessage("Current Incline is: " + currentActualIncline+ " goal: " + MIN_INCLINE+" time elapsed: "+seconds+"<br>");

            results+="Current Incline is: " + currentActualIncline+ " goal: " + MIN_INCLINE+" time elapsed: "+seconds+"\n";

            elapsedTime = System.nanoTime() - startime;
            seconds = elapsedTime / 1.0E09;
        } while(MIN_INCLINE!=currentActualIncline && seconds < 90);//Do while the incline hasn't reached its point yet or took more than 1.5 mins



        do{
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, 15);//TODO: change "15" to "MAX_INCLINE" when we get motor that reaches 40% grade
            mFecpController.addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("Status of setting incline to max: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
            results+="Status of setting incline to max: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

        appendMessage("Checking incline will reach set value...<br>");
        results+="Checking incline will reach set value...\n";
        startime = System.nanoTime();
        setIncline = 7;
        do
        {
            currentActualIncline = hCmd.getActualIncline();
            Thread.sleep(350);
            appendMessage("Current Incline is: " + currentActualIncline+ " goal: " + 15+" time elapsed: "+seconds+"<br>"); //TODO: Replace "15" by MAX_INCLINE
            results+="Current Incline is: " + currentActualIncline+ " goal: " + 15+" time elapsed: "+seconds+"\n";
            elapsedTime = System.nanoTime() - startime;
            seconds = elapsedTime / 1.0E09;
        } while(setIncline!=currentActualIncline && seconds < 90);//Do while the incline hasn't reached its point yet or took more than 1.5 mins

        //Stop
        appendMessage("Send stop command...<br>");
        results+="Send stop command...\n";
//        TODO: Use this part instead of setting mode to Pause once set_testing_key is working
//        Device keyPressTemp = this.MainDevice.getSubDevice(DeviceId.KEY_PRESS);
//
//        if(keyPressTemp != null){
//            Command writeKeyPressCmd = keyPressTemp.getCommand(CommandId.SET_TESTING_KEY);
//            if(writeKeyPressCmd != null){
//                sendKeyCmd = new FecpCommand(writeKeyPressCmd, hCmd);
//                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyCode(KeyCodes.STOP);
//                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyOverride(true);
//                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setTimeHeld(1000);
//                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setIsSingleClick(true);
//            }
//        }
//       do{
//          mSFitSysCntrl.getFitProCntrl().addCmd(sendKeyCmd);
//          Thread.sleep(time);
//          appendMessage("Status of sending Stop key command: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
//          results+="Status of sending Stop key command: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
//      }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

    do{
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
            mFecpController.addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("Status of setting mode to PAUSE: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
            results+="Status of setting mode to PAUSE: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
    }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

        appendMessage("reading actual incline...<br>");
        results+="reading actual incline...\n";
        minToMaxIncline1 = hCmd.getActualIncline();
        appendMessage("wait 5 secs...<br>");
        results+="wait 5 secs...\n";
        Thread.sleep(5000);
        appendMessage("read actual incline again...<br>");
        results+="read actual incline again...\n";
        minToMaxIncline2 = hCmd.getActualIncline();

        totalTestsCount++;
        if(minToMaxIncline1 == minToMaxIncline2){
            appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
            appendMessage("The incline value from Min Incline to Max Incline was the same in both readings indicating incline stopped: " + minToMaxIncline1 + "%<br><br>");

            results+="\n* PASS *\n\n";
            results+="The incline value from Min Incline to Max Incline was the same in both readings indicating incline stopped: " + minToMaxIncline1 + "%\n\n";

        }
        else{
            appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
            appendMessage("The first incline value read -> "+minToMaxIncline1+  " was different than second incline value read -> "+minToMaxIncline2+ " indicating incline DID NOT stop<br><br>");
            issuesListHtml+="<br>- "+"The first incline value read -> "+minToMaxIncline1+  " was different than second incline value read -> "+minToMaxIncline2+ " indicating incline DID NOT stop<br>";
            results+="\n* FAIL *\n\n";
            results+="The first incline value read -> "+minToMaxIncline1+  " was different than second incline value read -> "+minToMaxIncline2+ " indicating incline DID NOT stop\n\n";
            issuesList+="\n- "+"The first incline value read -> "+minToMaxIncline1+  " was different than second incline value read -> "+minToMaxIncline2+ " indicating incline DID NOT stop\n";
            testValidation = "FAILED";
            failsCount++;
        }

        //set Incline to max
        do{
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, 15);//TODO: change "15" to "MAX_INCLINE" when we get motor that reaches 40% grade
            mFecpController.addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("Status of setting incline to max: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
            results+="Status of setting incline to max: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

        //check actual incline until value reaches MAX_INCLINE
        appendMessage("Checking incline will reach set value...<br>");
        results+="Checking incline will reach set value...\n";
        startime = System.nanoTime();
        do
        {
            currentActualIncline = hCmd.getActualIncline();
            Thread.sleep(300);
            appendMessage("Current Incline is: " + currentActualIncline+ " goal: " + 15+" time elapsed: "+seconds+"<br>");//TODO: Replace "15" by MAX_INCLINE

            results+="Current Incline is: " + currentActualIncline+ " goal: " + 15+" time elapsed: "+seconds+"\n";

            elapsedTime = System.nanoTime() - startime;
            seconds = elapsedTime / 1.0E09;
        } while(15!=currentActualIncline && seconds < 90);//Do while the incline hasn't reached its point yet or took more than 1.5 mins


        do{
            //Set Incline to Min Incline
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, MIN_INCLINE);
            mFecpController.addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("Status of setting incline to min: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
            results+="Status of setting incline to min: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

        appendMessage("Checking incline will reach set value...<br>");
        results+="Checking incline will reach set value...\n";

        setIncline = 5;
        startime = System.nanoTime();
        do
        {
            currentActualIncline = hCmd.getActualIncline();
            Thread.sleep(350);
            appendMessage("Current Incline is: " + currentActualIncline+ " goal: " + MIN_INCLINE+" time elapsed: "+seconds+"<br>");

            results+="Current Incline is: " + currentActualIncline+ " goal: " + MIN_INCLINE+" time elapsed: "+seconds+"\n";

            elapsedTime = System.nanoTime() - startime;
            seconds = elapsedTime / 1.0E09;
        } while(setIncline!=currentActualIncline && seconds < 90);//Do while the incline hasn't reached its point yet or took more than 1.5 mins

        //Stop
        appendMessage("Send stop command...<br>");
        results+="Send stop command...\n";
//        TODO: Use this part instead of setting mode to Pause once set_testing_key is working
//        Device keyPressTemp = this.MainDevice.getSubDevice(DeviceId.KEY_PRESS);
//
//        if(keyPressTemp != null){
//            Command writeKeyPressCmd = keyPressTemp.getCommand(CommandId.SET_TESTING_KEY);
//            if(writeKeyPressCmd != null){
//                sendKeyCmd = new FecpCommand(writeKeyPressCmd, hCmd);
//                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyCode(KeyCodes.STOP);
//                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyOverride(true);
//                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setTimeHeld(1000);
//                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setIsSingleClick(true);
//            }
//        }
//       do{
//          mSFitSysCntrl.getFitProCntrl().addCmd(sendKeyCmd);
//          Thread.sleep(time);
//          appendMessage("Status of sending Stop key command: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
//          results+="Status of sending Stop key command: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
//      }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again
        do{
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
            mFecpController.addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("Status of setting mode to PAUSE: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
            results+="Status of setting mode to PAUSE: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

        appendMessage("reading actual incline...<br>");
        results+="reading actual incline...\n";
        maxToMinIncline1 = hCmd.getActualIncline();
        appendMessage("wait 5 secs...<br>");
        results+="wait 5 secs...\n";
        Thread.sleep(5000);
        appendMessage("read actual incline again...<br>");
        results+="read actual incline again...\n";
        maxToMinIncline2 = hCmd.getActualIncline();

        totalTestsCount++;

        if(maxToMinIncline1 == maxToMinIncline2){
            appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
            appendMessage("The incline value from Max Incline to Min Incline was the same in both readings indicating incline stopped: " + maxToMinIncline1 + "%<br>");

            results+="\n* PASS *\n\n";
            results+="The incline value from Max Incline to Min Incline was the same in both readings indicating incline stopped: " + maxToMinIncline1 + "%\n";
        }
        else{
            appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
            appendMessage("The first incline value read -> "+maxToMinIncline1+  " was different than second incline value read -> "+maxToMinIncline2+ " indicating incline DID NOT stop<br>");

            issuesListHtml+="<br>- "+"The first incline value read -> "+maxToMinIncline1+  " was different than second incline value read -> "+maxToMinIncline2+ " indicating incline DID NOT stop<br>";
            results+="\n* FAIL *\n\n";
            results+="The first incline value read -> "+maxToMinIncline1+  " was different than second incline value read -> "+maxToMinIncline2+ " indicating incline DID NOT stop\n";
            issuesList+="\n- "+"The first incline value read -> "+maxToMinIncline1+  " was different than second incline value read -> "+maxToMinIncline2+ " indicating incline DID NOT stop\n";
            testValidation = "FAILED";
            failsCount++;
        }
    //set mode back to idle to stop the test
        do{
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("<br>Status of settting mode to PAUSE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
            results+="\nStatus of settting mode to pause"+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

        do{
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("<br>Status of settting mode to RESULTS "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
            results+="\nStatus of settting mode to RESULTS"+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again
        do{
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("<br>Status of settting mode to IDLE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
            results+="\nStatus of settting mode to IDLE"+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

        timeOfTest = System.nanoTime() - startTestTimer;
        timeOfTest = timeOfTest / 1.0E09;

        appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
        results+="\nThis test took a total of "+timeOfTest+" secs \n";
        results+=resultsSummaryTemplate(testValidation,currentVersion,gitHubWikiName,failsCount,issuesList,issuesListHtml,totalTestsCount);

        return results;
    }

    /**
     * Tests that incline doesn't change when start button is pressed. In other words that the incline value
     * the console had before pressing start, is retained after pressing start
     * @return text log of test results
     * @throws Exception
     */
    public String retainedIncline() throws Exception {
        //Redmine Support #1077
        //Set Incline to 5%
        //Set mode to Running
        //Read the incline to verify that it hasn't change
        String results="";
        gitHubWikiName = "Retained-Incline";
        testValidation ="PASSED";
        issuesListHtml = "";
        issuesList = "";
        failsCount=0;
        totalTestsCount = 0;
        mAct.filename = "Retained Incline.txt";

        appendMessage("<br><br>----------------------RETAINED INCLINE TEST RESULTS----------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        results+="\n\n----------------------RETAINED INCLINE TEST RESULTS----------------------\n\n";
        results+=Calendar.getInstance().getTime() + "\n\n";

        double currentIncline1, currentIncline2;
        double testIncline = 5;
        double setIncline = 0;
        String currentMode;
        double currentActualIncline =0;

        double timeOfTest = 0; //how long test took in seconds
        long elapsedTime = 0;
        double seconds = 0;
        long startime = 0;
        long startTestTimer = System.nanoTime();
        long time = 500;

        currentMode = "Current Mode is: " + hCmd.getMode();
        appendMessage(currentMode + "<br>");

        results+="Current Mode is: " + hCmd.getMode()+"\n";

        //Set incline to 5% for testing the incline
        do{
            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, testIncline);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("The status of setting the Incline to 5%: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
            results+="The status of setting the Incline to 5%: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

        setIncline = hCmd.getIncline();

        //Wait for the incline motor to go to 5%
        appendMessage("Checking incline will reach set value...<br>");
        results+="Checking incline will reach set value...\n";
        startime= System.nanoTime();
        do
        {
            currentActualIncline = hCmd.getActualIncline();
            Thread.sleep(300);
            appendMessage("Current Incline is: " + currentActualIncline+ " goal: " + setIncline+" time elapsed: "+seconds+"<br>");

            results+="Current Incline is: " + currentActualIncline+ " goal: " + setIncline+" time elapsed: "+seconds+"\n";
            elapsedTime = System.nanoTime() - startime;
            seconds = elapsedTime / 1.0E09;
        } while(setIncline!=currentActualIncline && seconds < 60);//Do while the incline hasn't reached its target point. Break the  loop if it took more than a minute to reach target incline

        currentIncline1 = hCmd.getActualIncline();

        appendMessage("The actual incline is " + currentIncline1 +" % <br>");
        results+="The actual incline is " + currentIncline1 +" % \n";

        //Set Mode to Running
        do{
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE,ModeId.RUNNING);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("Status of setting the mode to Running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
            results+="Status of setting the mode to Running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again


        currentMode = hCmd.getMode().toString();
        appendMessage("Current Mode is: " + currentMode + "<br>");

        results+="Current Mode is: " + currentMode + "\n";
        appendMessage("Wait 30 secs...<br>");
        results+="Wait 30 secs...\n";
        //let the workout run for 30 sec
        Thread.sleep(30000);
        totalTestsCount++;
        currentIncline2 = hCmd.getActualIncline();

        if(currentIncline1 == currentIncline2 && currentIncline1 == testIncline && currentMode.equals("RUNNING")){
            appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
            appendMessage("The Incline went to " + testIncline + " and did not change when the mode was changed to Running<br>");

            results+="\n* PASS *\n\n";
            results+="The Incline went to " + testIncline + " and did not change when the mode was changed to Running\n";
        }
        else{
            if(!currentMode.equals("RUNNING")){
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("Mode didn't change to Running<br>");
                issuesListHtml+="<br>- "+"Mode didn't change to Running<br>";
                results+="\n* FAIL *\n\n";
                results+="Mode didn't change to Running\n";
                issuesList+="\n- "+"Mode didn't change to Running\n";
                testValidation  = "FAILED";
                failsCount++;
            }
            if(currentIncline1 != currentIncline2) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("Incline should be " + currentIncline1 + "%, but is " + currentIncline2 + "%<br>");

                results+="\n* FAIL *\n\n";
                results+="Incline should be " + currentIncline1 + "%, but is " + currentIncline2 + "%\n";
            }
            if(currentIncline1!= testIncline || currentIncline2 != testIncline) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("The incline did not go to " + testIncline + " %<br>");
                appendMessage("The incline was at " + currentIncline1 + " % before mode change and " + currentIncline2 + " % afterwards <br>");
                issuesListHtml+="<br>- "+"The incline was at " + currentIncline1 + " % before mode change and " + currentIncline2 + " % afterwards <br>";
                results+="\n* FAIL *\n\n";
                results+="The incline did not go to " + testIncline + " %\n";
                results+="The incline was at " + currentIncline1 + " % before mode change and " + currentIncline2 + " % afterwards \n";
                issuesList+="\n- "+"The incline was at " + currentIncline1 + " % before mode change and " + currentIncline2 + " % afterwards \n";
                testValidation  = "FAILED";
                failsCount++;
            }
        }

        //set mode back to idle to stop the test
        do{
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("<br>Status of settting mode to PAUSE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
            results+="\nStatus of settting mode to pause"+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

        do{
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("<br>Status of settting mode to RESULTS "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
            results+="\nStatus of settting mode to RESULTS"+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again
        do{
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("<br>Status of settting mode to IDLE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
            results+="\nStatus of settting mode to IDLE"+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

        //TODO: At this point if console is home unit, incline should stay the same. If console is club unit, incline resets to 0. Test for those conditions HERE

        timeOfTest = System.nanoTime() - startTestTimer;
        timeOfTest = timeOfTest / 1.0E09;

        appendMessage("<br>This test took a total of " + timeOfTest + " secs <br>");
        results+="\nThis test took a total of "+timeOfTest+" secs \n";
        results+=resultsSummaryTemplate(testValidation,currentVersion,gitHubWikiName,failsCount,issuesList,issuesListHtml,totalTestsCount);
        return results;
    }

    /**
     * Verifies speed changes as Incline changes according to the rules specified on software
     * checklist #201B, #9
     * @return text log of test results
     * @throws Exception
     */
    public String speedInclineLimits() throws Exception {
        //TODO: As of 3/12/14, this functionality is not yet implemented
        //Redmine issue #953
        //Testing limits on the incline and on the speed
        //Limits are (Software Checklist 201B, #9):
        //-6% to -5% = 6 mph
        //-4.5% to 3.1% = 7 mph
        //-3% to -2.1% = 8 mph
        //-2% to -1.1% = 8.5 mph
        //-1% to -0.1% = 9 mph
        //0% to 15% = full speed (up to 12 mph)
        //Incline Trainers:
        //>15% to 25% = 8 mph
        //>25% to 40% = 6 mph
        //Club Units:
        //As long as the incline is negative, speed limit is always 10 mph

        //Test
        //Set mode to Running
        //Make sure Incline is set to 0%
        //Set speed to max speed (20 kph for now)
        //Read speed to ensure it's at 20 kph
        //Set Incline to a negative limit
        //Read speed to ensure it was lowered and matches the above limits
        long time = 500;
        BigDecimal speedRounded;
        boolean isHomeUnit = false;
        gitHubWikiName = "Speed-Incline-Limits";
        testValidation ="PASSED";
        issuesListHtml = "";
        issuesList = "";
        failsCount=0;
        totalTestsCount = 0;
        mAct.filename = "Speed Incline Limits.txt";

        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();
        String results="";

        if(isHomeUnit) {
            speedLimitsHomeUnits();
        }
        else
        {
            results+=speedLimitsClubUnits();
        }

//
        //set mode back to idle to stop the test
        //set mode back to idle to stop the test
        do{
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("<br>Status of settting mode to PAUSE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
            results+="\nStatus of settting mode to pause"+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

        do{
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("<br>Status of settting mode to RESULTS "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
            results+="\nStatus of settting mode to RESULTS"+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again
        do{
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("<br>Status of settting mode to IDLE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
            results+="\nStatus of settting mode to IDLE"+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

        timeOfTest = System.nanoTime() - startTestTimer;
        timeOfTest = timeOfTest / 1.0E09;

        appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
        results+="\nThis test took a total of "+timeOfTest+" secs \n";
        results+=resultsSummaryTemplate(testValidation,currentVersion,gitHubWikiName,failsCount,issuesList,issuesListHtml,totalTestsCount);
        return results;
    }

    /***
     * Verifies speed/incline limits for <b>Home Units</b> (Treadmill and Incline Trainer)
     * @return
     * @throws Exception
     */

    private String speedLimitsHomeUnits() throws Exception{
        String results = "";
        double currentActualIncline = 0;
        long elapsedTime = 0;
        double seconds = 0;
        long startime = 0;
        double setIncline =0;
        double currentSpeed = 0;
        BigDecimal speedRounded;
        boolean isHomeUnit = false;
        final double  MAX_SPEED = hCmd.getMaxSpeed();
        final double  MIN_INCLINE = hCmd.getMinIncline();
        final double  MAX_INCLINE= hCmd.getMaxIncline();

        appendMessage("<br><br>----------------------Speed Incline Limits for "+devId.getDescription()+" Home Unit----------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        results+="\n\n----------------------Speed Incline Limits for "+devId.getDescription()+" Home Unit----------------------\n\n";
        results+=Calendar.getInstance().getTime() + "\n\n";

        //Set mode to running
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        appendMessage("Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
        appendMessage("current mode: "+hCmd.getMode()+"\n");

        results+="Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
        results+="current mode: "+hCmd.getMode()+"\n";

       // set Incline to zero
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, 0);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        currentIncline = hCmd.getIncline();
        appendMessage("Status of setting incline to zero: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");

        results+="Status of setting incline to zero: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";

        appendMessage("Checking incline will reach set value...<br>");

        results+="Checking incline will reach set value...\n";
        //Wait til incline reaches target value
        startime = System.nanoTime();
        do
        {
            currentActualIncline = hCmd.getActualIncline();
            Thread.sleep(350);
            appendMessage("Current Incline is: " + currentActualIncline+ " goal: " + currentIncline+" time elapsed: "+seconds+"<br>");

            results+="Current Incline is: " + currentActualIncline+ " goal: " + currentIncline+" time elapsed: "+seconds+"\n";

            elapsedTime = System.nanoTime() - startime;
            seconds = elapsedTime / 1.0E09;
        } while(currentIncline!=currentActualIncline && seconds < 60);//Do while the incline hasn't reached its point yet or took more than 1 mins

        //set speed to max
        appendMessage("set speed to max...<br>");
        results+="set speed to max...\n";
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, MAX_SPEED);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        appendMessage("Status of setting speed to max: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");

        results+="Status of setting speede to max: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
        appendMessage("Wait 23 seconds...<br>");
        results+="Wait 23 seconds...\n";
        Thread.sleep(23000); // give it 23 secs to reach max speed
/* TODO: THIS PART WILL BE UNCOMMENTED ONCE ACTUAL SPEED IS ACCURATE
                startime= System.nanoTime();
                do
                {
                    actualSpeed = hCmd.getActualSpeed();
                    Thread.sleep(300);
                    appendMessage("Current Speed is: " + actualSpeed+ " goal: " + j+" time elapsed: "+seconds+"<br>");
                    results+="Current Speed is: " + actualSpeed+ " goal: " + j+" time elapsed: "+seconds+"\n";
                    elapsedTime = System.nanoTime() - startime;
                    seconds = elapsedTime / 1.0E09;
                } while(j!=actualSpeed && seconds < 20);//Do while the incline hasn't reached its point yet or took more than 20 secs

*/
        //Check positive incline limits
        for(int i = 0; i <= 15; i+=5) {  //TODO: Repalce i <= 15 with "i <= MAX_INCLINE" once we get motor that gets to 40%
            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, i);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(1000);

            appendMessage("<br>Status of sending incline at " + i + "%: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");

            results+="\nStatus of sending incline at " + i + "%: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";


            currentIncline = hCmd.getIncline();

            appendMessage("Current Incline is set to " + currentIncline + "%<br>");

            results+="Current Incline is set to " + currentIncline + "%\n";


            appendMessage("Checking incline will reach set value...<br>");

            results+="Checking incline will reach set value...\n";
            //Wait til incline reaches target value
            startime = System.nanoTime();
            do
            {
                currentActualIncline = hCmd.getActualIncline();
                Thread.sleep(350);
                appendMessage("Current Incline is: " + currentActualIncline+ " goal: " + currentIncline+" time elapsed: "+seconds+"<br>");

                results+="Current Incline is: " + currentActualIncline+ " goal: " + currentIncline+" time elapsed: "+seconds+"\n";

                elapsedTime = System.nanoTime() - startime;
                seconds = elapsedTime / 1.0E09;
            } while(currentIncline!=currentActualIncline && seconds < 60);//Do while the incline hasn't reached its point yet or took more than 1 mins


            currentSpeed = hCmd.getSpeed();
            speedRounded = new BigDecimal(currentSpeed*0.625);
            speedRounded = speedRounded.setScale(0, BigDecimal.ROUND_HALF_UP);

            totalTestsCount++;
            //0% to 15% = 12 mph
            if( ( i>=0 && i <= 15)  && currentSpeed == 19.31) {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("At Incline " +currentIncline+", Current speed is " + speedRounded + " mph or "+currentSpeed+" kph <br>");

                results+="\n* PASS *\n\n";
                results+="At Incline " +currentIncline+", Current speed is " + speedRounded + " mph or "+currentSpeed+" kph \n";
            }
            else if (( i>= 0 && i <= 15) && currentSpeed != 19.31) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("At Incline " +currentIncline+" Current speed should be 12 MPH, but it is " +speedRounded + " mph or "+currentSpeed+" kph <br>");

                results+="\n* FAIL *\n\n";
                results+="At Incline " +currentIncline+" Current speed should be 12 MPH, but it is " + speedRounded + " mph or "+currentSpeed+" kph \n";
                issuesListHtml = "<br>- At Incline " + currentIncline + " Current speed should be 12 MPH, but it is " + speedRounded + " mph or " + currentSpeed + " kph <br>";
                issuesList+="\n- At Incline " + currentIncline + " Current speed should be 12 MPH, but it is " + speedRounded + " mph or " + currentSpeed + " kph \n";
                testValidation  = "FAILED";
                failsCount++;
            }

            //15.5% to 25% = 8 mph
            if( ( i>= 15.5 && i <= 25)  && currentSpeed == 12.87) {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("At Incline " +currentIncline+", Current speed is " + speedRounded + " mph or "+currentSpeed+" kph <br>");

                results+="\n* PASS *\n\n";
                results+="At Incline " +currentIncline+", Current speed is " + speedRounded + " mph or "+currentSpeed+" kph \n";
            }
            else if (( i>= 15.5 && i <= 25) && currentSpeed !=12.87) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("At Incline " +currentIncline+" Current speed should be 8 MPH, but it is " + speedRounded + " mph or "+currentSpeed+" kph <br>");

                results+="\n* FAIL *\n\n";
                results+="At Incline " +currentIncline+" Current speed should be 8 MPH, but it is " + speedRounded + " mph or "+currentSpeed+" kph \n";
                issuesListHtml = "<br>- At Incline " + currentIncline + " Current speed should be 8 MPH, but it is " + speedRounded + " mph or " + currentSpeed + " kph <br>";
                issuesList+="\n- At Incline " + currentIncline + " Current speed should be 8 MPH, but it is " + speedRounded + " mph or " + currentSpeed + " kph \n";
                testValidation  = "FAILED";
                failsCount++;
            }

            //25.5% to 40% = 6 mph
            if( ( i>= 25.5 && i <= 40)  && currentSpeed == 9.65) {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("At Incline " +currentIncline+", Current speed is " + speedRounded + " mph or "+currentSpeed+" kph <br>");

                results+="\n* PASS *\n\n";
                results+="At Incline " +currentIncline+", Current speed is " + speedRounded + " mph or "+currentSpeed+" kph \n";
            }
            else if (( i>= 25.5 && i <= 40) && currentSpeed != 9.65) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("At Incline " +currentIncline+" Current speed should be 6 MPH, but it is " + speedRounded + " mph or "+currentSpeed+" kph <br>");

                results+="\n* FAIL *\n\n";
                results+="At Incline " +currentIncline+" Current speed should be 6 MPH, but it is " + speedRounded + " mph or "+currentSpeed+" kph \n";
                issuesListHtml = "<br>- At Incline " + currentIncline + " Current speed should be 6 MPH, but it is " + speedRounded + " mph or " + currentSpeed + " kph <br>";
                issuesList+="\n- At Incline " + currentIncline + " Current speed should be 6 MPH, but it is " + speedRounded + " mph or " + currentSpeed + " kph \n";
                testValidation  = "FAILED";
                failsCount++;
            }
        }

        //set speed to max
        appendMessage("set speed to max...<br>");
        results+="set speed to max...\n";
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, MAX_SPEED);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        appendMessage("Status of setting speed to max: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");

        results+="Status of setting speede to max: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
        appendMessage("Wait 23 seconds...<br>");
        results+="Wait 23 seconds...\n";
        Thread.sleep(23000); // give it 23 secs to reach max speed
/* THIS PART WILL BE UNCOMMENTED ONCE ACTUAL SPEED IS ACCURATE
                startime= System.nanoTime();
                do
                {
                    actualSpeed = hCmd.getActualSpeed();
                    Thread.sleep(300);
                    appendMessage("Current Speed is: " + actualSpeed+ " goal: " + j+" time elapsed: "+seconds+"<br>");
                    results+="Current Speed is: " + actualSpeed+ " goal: " + j+" time elapsed: "+seconds+"\n";
                    elapsedTime = System.nanoTime() - startime;
                    seconds = elapsedTime / 1.0E09;
                } while(j!=actualSpeed && seconds < 20);//Do while the incline hasn't reached its point yet or took more than 20 secs

*/

        //Check negative Incline limits
        for(int i = -1; i >= MIN_INCLINE; i--) {
            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, i);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(1000);

            appendMessage("Status of sending incline at " + i + "%: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");

            results+="Status of sending incline at " + i + "%: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";


            currentIncline = hCmd.getIncline();

            appendMessage("Current Incline is set to " + currentIncline + "%<br>");

            results+="Current Incline is set to " + currentIncline + "%\n";


            appendMessage("Checking incline will reach set value...<br>");

            results+="Checking incline will reach set value...\n";
            //Wait til incline reaches target value
            startime = System.nanoTime();
            do
            {
                currentActualIncline = hCmd.getActualIncline();
                Thread.sleep(350);
                appendMessage("Current Incline is: " + currentActualIncline+ " goal: " + currentIncline+" time elapsed: "+seconds+"<br>");

                results+="Current Incline is: " + currentActualIncline+ " goal: " + currentIncline+" time elapsed: "+seconds+"\n";

                elapsedTime = System.nanoTime() - startime;
                seconds = elapsedTime / 1.0E09;
            } while(currentIncline!=currentActualIncline && seconds < 60);//Do while the incline hasn't reached its point yet or took more than 1 mins


            currentSpeed = hCmd.getSpeed();
            speedRounded = new BigDecimal(currentSpeed*0.625);
            speedRounded = speedRounded.setScale(0, BigDecimal.ROUND_HALF_UP);

            totalTestsCount++;

            //-1% to -0.1% = 9 mph
            if( ( i< 0 && i >= -1)  && currentSpeed == 14.48) {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("At Incline " +currentIncline+", Current speed is " + speedRounded + " mph or "+currentSpeed+" kph <br>");

                results+="\n* PASS *\n\n";
                results+="At Incline " +currentIncline+", Current speed is " + speedRounded + " mph or "+currentSpeed+" kph \n";
            }
            else if (( i< 0 && i >= -1) && currentSpeed != 14.48) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("At Incline " +currentIncline+" Current speed should be 9 MPH, but it is " + speedRounded + " mph or "+currentSpeed+" kph <br>");

                results+="\n* FAIL *\n\n";
                results+="At Incline " +currentIncline+" Current speed should be 9 MPH, but it is " + speedRounded + " mph or "+currentSpeed+" kph \n";
                issuesListHtml = "<br>- At Incline " + currentIncline + " Current speed should be 9 MPH, but it is " + speedRounded + " mph or " + currentSpeed + " kph <br>";
                issuesList+="\n- At Incline " + currentIncline + " Current speed should be 9 MPH, but it is " + speedRounded + " mph or " + currentSpeed + " kph \n";
                testValidation  = "FAILED";
                failsCount++;
            }

            //-2% to -1.1% = 8.5 mph
            if((i < -1 && i >= -2)  && currentSpeed == 13.60) {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("At Incline " +currentIncline+", Current speed is " + speedRounded + " mph or "+currentSpeed+" kph <br>");

                results+="\n* PASS *\n\n";
                results+="At Incline " +currentIncline+", Current speed is " + speedRounded + " mph or "+currentSpeed+" kph \n";
            }
            else if ( (i< -1 && i >= -2) && currentSpeed != 13.6) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("At Incline " +currentIncline+" Current speed should be 8.5 MPH, but it is " +speedRounded + " mph or "+currentSpeed+" kph <br>");

                results+="\n* FAIL *\n\n";
                results+="At Incline " +currentIncline+" Current speed should be 8.5 MPH, but it is " + speedRounded + " mph or "+currentSpeed+" kph \n";
            }

            //-3% to -2.1% = 8 mph
            if( (i< -2 && i >= -3) && currentSpeed == 12.87) {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("At Incline " +currentIncline+", Current speed is " + speedRounded + " mph or "+currentSpeed+" kph <br>");

                results+="\n* PASS *\n\n";
                results+="At Incline " +currentIncline+", Current speed is " + speedRounded + " mph or "+currentSpeed+" kph \n";
            }
            else if ( (i< -2 && i >= -3) && currentSpeed != 12.87) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("At Incline " +currentIncline+" Current speed should be 8.0 MPH, but it is " + speedRounded + " mph or "+currentSpeed+" kph <br>");

                results+="\n* FAIL *\n\n";
                results+="At Incline " +currentIncline+" Current speed should be 8.0 MPH, but it is " + speedRounded + " mph or "+currentSpeed+" kph \n";
            }

            //-5% to -3.1% = 7 mph
            if( (i< -3 && i >= -5) && currentSpeed == 11.26) {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("At Incline " +currentIncline+", Current speed is " + speedRounded + " mph or "+currentSpeed+" kph <br>");

                results+="\n* PASS *\n\n";
                results+="At Incline " +currentIncline+", Current speed is " + speedRounded + " mph or "+currentSpeed+" kph \n";
            }
            else if ( (i< -3 && i >= -5) && currentSpeed != 11.26) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("At Incline " +currentIncline+" Current speed should be 7.0 MPH, but it is " + speedRounded + " mph or "+currentSpeed+" kph <br>");

                results+="\n* FAIL *\n\n";
                results+="At Incline " +currentIncline+" Current speed should be 7.0 MPH, but it is " + speedRounded + " mph or "+currentSpeed+" kph \n";
            }

            //-6% to -5% = 6 mph
            if( (i< -5 && i >= -6) && currentSpeed == 9.65) {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("At Incline " +currentIncline+", Current speed is " + speedRounded + " mph or "+currentSpeed+" kph <br>");

                results+="\n* PASS *\n\n";
                results+="At Incline " +currentIncline+", Current speed is " + speedRounded + " mph or "+currentSpeed+" kph \n";
            }
            else if ( (i< -5 && i >= -6) && currentSpeed != 9.65) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("At Incline " +currentIncline+" Current speed should be 6.0 MPH, but it is " + speedRounded + " mph or "+currentSpeed+" kph <br>");

                results+="\n* FAIL *\n\n";
                results+="At Incline " +currentIncline+" Current speed should be 6.0 MPH, but it is " + speedRounded + " mph or "+currentSpeed+" kph \n";
            }

        }
        return results;
    }

    /***
     * Verifies speed/incline limits for <b>Club Units</b>  (Treadmill and Incline Trainer)
     * @return
     * @throws Exception
     */

    private String speedLimitsClubUnits() throws Exception {
        String results = "";
        double currentActualIncline = 0;
        long elapsedTime = 0;
        double seconds = 0;
        long startime = 0;
        long time = 500;
        double setIncline =0;
        double currentSpeed = 0;
        BigDecimal speedRounded;
        final double  MAX_SPEED = hCmd.getMaxSpeed();
        final double  MIN_INCLINE = hCmd.getMinIncline();
        final double  MAX_INCLINE= hCmd.getMaxIncline();

        appendMessage("<br><br>----------------------Speed Incline Limits for "+devId.getDescription()+" Club Unit----------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        results+="\n\n----------------------Speed Incline Limits for "+devId.getDescription()+" Club Unit----------------------\n\n";
        results+=Calendar.getInstance().getTime() + "\n\n";

        //Set mode to running
        do{
            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
            results+="Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

        appendMessage("current mode: "+hCmd.getMode()+"\n");
        results+="current mode: "+hCmd.getMode()+"\n";

        //Start incline at -1, then go to -6
        for(int i = -1; i >= MIN_INCLINE; i--) {
            //set Incline to zero
            do{
                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, 0);
                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                Thread.sleep(time);
                appendMessage("Status of setting incline to zero: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
                results+="Status of setting incline to zero: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
            }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

            currentIncline = hCmd.getIncline();
            appendMessage("Checking incline will reach set value...<br>");
            results+="Checking incline will reach set value...\n";
            //Wait til incline reaches target value
            startime = System.nanoTime();
            do
            {
                currentActualIncline = hCmd.getActualIncline();
                Thread.sleep(350);
                appendMessage("Current Incline is: " + currentActualIncline+ " goal: " + currentIncline+" time elapsed: "+seconds+"<br>");

                results+="Current Incline is: " + currentActualIncline+ " goal: " + currentIncline+" time elapsed: "+seconds+"\n";

                elapsedTime = System.nanoTime() - startime;
                seconds = elapsedTime / 1.0E09;
            } while(0!=currentActualIncline && seconds < 60);//Do while the incline hasn't reached its point yet or took more than 1 mins

            //set speed to max
            appendMessage("set speed to max...<br>");
            results+="set speed to max...\n";
            do{
                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, MAX_SPEED);
                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                Thread.sleep(time);
                appendMessage("Status of setting speed to max: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
                results+="Status of setting speede to max: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
            }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

            /*TODO: THIS PART WILL BE UNCOMMENTED ONCE ACTUAL SPEED IS ACCURATE
                startime= System.nanoTime();
                do
                {
                    actualSpeed = hCmd.getActualSpeed();
                    Thread.sleep(300);
                    appendMessage("Current Speed is: " + actualSpeed+ " goal: " + j+" time elapsed: "+seconds+"<br>");
                    results+="Current Speed is: " + actualSpeed+ " goal: " + j+" time elapsed: "+seconds+"\n";
                    elapsedTime = System.nanoTime() - startime;
                    seconds = elapsedTime / 1.0E09;
                } while(j!=actualSpeed && seconds < 20);//Do while the incline hasn't reached its point yet or took more than 20 secs

*/
            appendMessage("Wait 10 seconds...<br>");
            results+="Wait 10 seconds...\n";
            Thread.sleep(10000); // give it 10 secs to reach max speed
            appendMessage("Current speed is: "+hCmd.getSpeed()+"<br>");
            results+="Current speed is: "+hCmd.getSpeed()+"\n";
            do{
                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, i);
                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                Thread.sleep(time);
                appendMessage("Status of sending incline at " + i + "%: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
                results += "Status of sending incline at " + i + "%: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
            }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

            currentIncline = hCmd.getIncline();

            appendMessage("Current Incline is set to " + currentIncline + "%<br>");
            results += "Current Incline is set to " + currentIncline + "%\n";


            appendMessage("Checking incline will reach set value...<br>");
            results += "Checking incline will reach set value...\n";

            //Wait til incline reaches target value
            startime = System.nanoTime();
            do {
                currentActualIncline = hCmd.getActualIncline();
                Thread.sleep(350);
                appendMessage("Current Incline is: " + currentActualIncline + " goal: " + currentIncline + " time elapsed: " + seconds + "<br>");
                results += "Current Incline is: " + currentActualIncline + " goal: " + currentIncline + " time elapsed: " + seconds + "\n";
                elapsedTime = System.nanoTime() - startime;
                seconds = elapsedTime / 1.0E09;
            }
            while (currentIncline != currentActualIncline && seconds < 60);//Do while the incline hasn't reached its point yet or took more than 1 mins


            currentSpeed = hCmd.getSpeed();
            speedRounded = new BigDecimal(currentSpeed * 0.625);
            speedRounded = speedRounded.setScale(0, BigDecimal.ROUND_HALF_UP);

            totalTestsCount++;
            if ((currentIncline < 0) && currentSpeed == 16.09) {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("At Incline " + currentIncline + ", Current speed dropped to" + speedRounded + " mph or " + currentSpeed + " kph <br><br>");

                results += "\n* PASS *\n\n";
                results += "At Incline " + currentIncline + ", Current speed dropped to " + speedRounded + " mph or " + currentSpeed + " kph \n\n";
            } else if ((currentIncline < 0) && currentSpeed != 16.09) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("At Incline " + currentIncline + " Current speed should be 10 MPH, but it is " + speedRounded + " mph or " + currentSpeed + " kph <br><br>");

                results += "\n* FAIL *\n\n";
                results += "At Incline " + currentIncline + " Current speed should be 10 MPH, but it is " + speedRounded + " mph or " + currentSpeed + " kph \n\n";
                issuesListHtml = "<br>- At Incline " + currentIncline + " Current speed should be 10 MPH, but it is " + speedRounded + " mph or " + currentSpeed + " kph <br>";
                issuesList+="\n- At Incline " + currentIncline + " Current speed should be 10 MPH, but it is " + speedRounded + " mph or " + currentSpeed + " kph \n";
                testValidation  = "FAILED";
                failsCount++;
            }
        }
        return results;
    }


    /**
     * Testing that Incline value is retained after DMK key is pulled and put back (software checklist #44)
     * @return text log of test results
     * @throws Exception
     */
    public String inclineRetentionDmkRecall() throws Exception {
             //From Software Checklist #44
             //Redmine Support #1079
             //Set mode to Idle
             //Set Incline to 0
             //Set Incline to max incline
             //Halfway up, set mode to Running
             //Set mode to Pause
             //Set mode to DMK
             //Read actual Incline to verify the console has correct current incline
        String results="";
        gitHubWikiName = "Incline-Retention-After-DMK-&-DMK-Incline-Recall";
        testValidation ="PASSED";
        issuesListHtml = "";
        issuesList = "";
        failsCount=0;
        totalTestsCount = 0;
        mAct.filename = "Dmk Incline.txt";

        long elapsedTime = 0;
        double seconds = 0;
        long startime = 0;
        double setIncline;
        double inclineAtDMKpull = 0;
        double inclineAfterDMKpull = 0;
        double actualIncline;
        final double MAX_INCLINE = hCmd.getMaxIncline();

        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();
        long time = 500;

            appendMessage("<br>----------------------INCLINE RETENTION AFTER DMK TEST RESULTS----------------------<br><br>");
            appendMessage(Calendar.getInstance().getTime() + "<br><br>");

            results+="\n----------------------INCLINE RETENTION AFTER DMK TEST RESULTS----------------------\n\n";
            results+=Calendar.getInstance().getTime() + "\n\n";


             //Set Incline to 0
            setIncline = 0;
           do{
              ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, setIncline);
               mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
               Thread.sleep(time);
               appendMessage("Status of setting incline to 0%: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
               results+="Status of setting incline to 0%: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
            }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

            appendMessage("Checking incline will reach set value...<br>");
            results+="Checking incline will reach set value...\n";
             //Wait for the incline motor to go setIncline
             startime= System.nanoTime();
             do
             {
                 actualIncline = hCmd.getActualIncline();
                 Thread.sleep(300);
                 appendMessage("Current Incline is: " + actualIncline+ " goal: " + setIncline+" time elapsed: "+seconds+"<br>");
                 results+="Current Incline is: " + actualIncline+ " goal: " + setIncline+" time elapsed: "+seconds+"\n";
                 elapsedTime = System.nanoTime() - startime;
                 seconds = elapsedTime / 1.0E09;
             } while(setIncline!=actualIncline && seconds < 60);//Do while the incline hasn't reached its target point. Break the  loop if it took more than a minute to reach target incline

            do{
                 //Set Incline to Max Incline
                 ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, 15); //TODO: replace 15 by MAX_INCLINE
                 mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                 Thread.sleep(time);
                 appendMessage("Status of setting incline to " + 15 + "% (Max Incline): " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
                 results+="Status of setting incline to " + 15 + "% (Max Incline): " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
            }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

        //Wait a little while to get past 0, but not to max incline
            Thread.sleep(2000);


        //Set Mode to Running
            do{
                ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
                 mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                 Thread.sleep(time);
                 appendMessage("Status of setting mode to Running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
                 results+="Status of setting mode to Running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
             }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

        //Set Mode to Pause
        do{
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("Status of setting mode to PAUSE: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
            results+="Status of setting mode to PAUSE: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

             System.out.println("Pull DMK key now!");
             appendMessage("Waiting for DMK key to be pulled...<br>");
             results+="Waiting for DMK key to be pulled...\n";

            while(hCmd.getMode()!=ModeId.DMK);
             {
                 // Stay here until DMK Key is pulled
             }
             inclineAtDMKpull = hCmd.getActualIncline();
             System.out.println("DMK Key Pulled!");
             appendMessage("DMK key pulled!<br>");

             results+="DMK key pulled!\n";

             appendMessage("Waiting 5 secs...<br>");

             results+="Waiting 5 secs...\n";
              Thread.sleep(5000);
              inclineAfterDMKpull = hCmd.getActualIncline();

        //Read Incline and verify it is not equal to max incline or less than, or equal to, zero
             totalTestsCount++;
             if(inclineAfterDMKpull == inclineAtDMKpull){
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("Actual Incline before DMK Pulled was: " + inclineAtDMKpull + "which matches actual incline after DMK was put back and waited 5 secs<br>");

                results+="\n* PASS *\n\n";
                results+="Actual Incline before DMK Pulled was: " + inclineAtDMKpull + "which matches actual incline after DMK was put back and waited 5 secs\n";
             }
             else {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("Actual Incline should be " + inclineAtDMKpull + "%, but it is currently at " + inclineAfterDMKpull + "%<br>");
                issuesList+="<br>- "+"Actual Incline should be " + inclineAtDMKpull + "%, but it is currently at " + inclineAfterDMKpull + "%<br>";
                results+="\n* FAIL *\n\n";
                results+="Actual Incline should be " + inclineAtDMKpull + "%, but it is currently at " + inclineAfterDMKpull + "%\n";
                issuesList+="\n- "+"Actual Incline should be " + inclineAtDMKpull + "%, but it is currently at " + inclineAfterDMKpull + "%\n";
                testValidation  = "FAILED";
                failsCount++;
             }

            appendMessage("<br>----------------------DMK RECALL INCLINE TEST RESULTS----------------------<br><br>");

            results+="\n----------------------DMK RECALL INCLINE TEST RESULTS----------------------\n\n";


        System.out.println("Put DMK key back on console");
             appendMessage("Waiting for DMK key to be put pack on console...<br>");

             results+="Waiting for DMK key to be put pack on console...\n";

        while(hCmd.getMode()==ModeId.DMK);
             {
                 // Stay here until DMK Key put back on console
             }
             actualIncline = hCmd.getActualIncline();
             System.out.println("DMK Key Put back");
             appendMessage("DMK key put back!<br>");

             results+="DMK key put back!\n";

            totalTestsCount++;
        //Compare the value read for actual incline after key has been pulled to value read after key was put back
             if(actualIncline ==inclineAfterDMKpull){
                 appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                 appendMessage("Actual Incline after DMK put back is at " + actualIncline + "% which is the same as when the key was pulled<br>");

                 results+="\n* PASS *\n\n";
                 results+="Actual Incline after DMK put back is at " + actualIncline + "% which is the same as when the key was pulled\n";
             }
             else {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("Actual Incline should be " + inclineAfterDMKpull + "%, but it is currently at " + actualIncline + "%<br>");
                issuesListHtml+="<br>- "+"Actual Incline should be " + inclineAfterDMKpull + "%, but it is currently at " + actualIncline + "%<br>";
                results+="\n* FAIL *\n\n";
                results+="Actual Incline should be " + inclineAfterDMKpull + "%, but it is currently at " + actualIncline + "%\n";
                issuesList+="\n- "+"Actual Incline should be " + inclineAfterDMKpull + "%, but it is currently at " + actualIncline + "%\n";
                testValidation  = "FAILED";
                failsCount++;
             }

             //set mode back to idle to stop the test
        do{
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("<br>Status of settting mode to PAUSE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
            results+="\nStatus of settting mode to pause"+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

        do{
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("<br>Status of settting mode to RESULTS "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
            results+="\nStatus of settting mode to RESULTS"+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again
        do{
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("<br>Status of settting mode to IDLE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
            results+="\nStatus of settting mode to IDLE"+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

        timeOfTest = System.nanoTime() - startTestTimer;
        timeOfTest = timeOfTest / 1.0E09;

        appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
        results+="\nThis test took a total of "+timeOfTest+" secs \n";
        results+=resultsSummaryTemplate(testValidation,currentVersion,gitHubWikiName,failsCount,issuesList,issuesListHtml,totalTestsCount);
        return results;
         }

    /**
     * Verifies that there is a minimum of 400ms pause between incline direction changes
     * @return text log of test results
     * @throws Exception
     */

    public String incline400msPause() throws Exception
    {
        String results="";
        gitHubWikiName = "Incline-Pause-Direction";
        testValidation ="PASSED";
        issuesListHtml = "";
        issuesList = "";
        failsCount=0;
        totalTestsCount = 0;
        mAct.filename = "Incline 400ms Pause.txt";

        long elapsedTime = 0;
        double seconds = 0;
        long startime = 0;
        double setIncline = 0;


        double actualIncline;
        double maxIncline;

        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();
        long time = 500;

        double incline1=0;
        double incline2=0.1;
        double aIncline1=0;
        double aIncline2 = 0;
        double timeMillisecs = 0;

        appendMessage("<br>----------------------400 ms INCLINE PAUSE DIRECTION TEST----------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        results+="\n----------------------400 ms INCLINE PAUSE DIRECTION TEST----------------------\n\n";
        results+=Calendar.getInstance().getTime() + "\n\n";

        //Set Incline to 2
        setIncline = 0;
        do{
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, setIncline);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("Status of setting incline to "+setIncline+"%: "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
            results+="Status of setting incline to "+setIncline+"%: "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

        appendMessage("Checking incline will reach set value...<br>");
        results+="Checking incline will reach set value...\n";
        //Wait for the incline motor to go setIncline
        startime= System.nanoTime();
        do
        {
            actualIncline = hCmd.getActualIncline();
            Thread.sleep(350);
            appendMessage("Current Incline is: " + actualIncline+ " goal: " + setIncline+" time elapsed: "+seconds+"<br>");
            results+="Current Incline is: " + actualIncline+ " goal: " + setIncline+" time elapsed: "+seconds+"\n";
            elapsedTime = System.nanoTime() - startime;
            seconds = elapsedTime / 1.0E09;
        } while(setIncline!=actualIncline && seconds < 60);//Do while the incline hasn't reached its target point. Break the  loop if it took more than a minute to reach target incline

        setIncline = 5;
        appendMessage("Set incline to "+setIncline+"% <br>");
        results+="Set incline to "+setIncline+"% \n";
        do{
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, setIncline);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(500);
            appendMessage("Status of setting incline to" +setIncline+"% "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
            results+="Status of setting incline to " +setIncline+"% "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

        Thread.sleep(1500); // wait a few seconds to let incline move some % before setting it back to zero
        setIncline = 0;
       do{
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, setIncline);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(50);
            appendMessage("Status of setting incline to" +setIncline+"% "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
            results+="Status of setting incline to to" +setIncline+"% "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

    // If incline1 < incline2 it means that is still moving towards first incline set ( 5 in our case)
      // And it has not stopped to change directions
        appendMessage("Checking if incline has stopped.. <br>");
        results+="Checking if incline has stopped.. \n";
       do
        {
            incline1= hCmd.getActualIncline();
            Thread.sleep(350);
            incline2 = hCmd.getActualIncline();
            appendMessage(" incline1: "+incline1+" incline 2: "+incline2+"<br>");
            results+=" incline1: "+incline1+" incline 2: "+incline2+"\n";
        } while(incline1<incline2);
        startime = System.nanoTime();

        // At this point the inclined stopped to change directions
        // Once incline1 != incline2, it means it is moving again in the opposite direction.
        aIncline1 = hCmd.getActualIncline();
        appendMessage("Incline has stopped! current incline is "+aIncline1+"<br>");
        results+="Incline has stopped! current incline is"+aIncline1+"\n";
        while(incline1==incline2)
        {
            incline1= hCmd.getActualIncline();
            Thread.sleep(50);
            incline2 = hCmd.getActualIncline();
            elapsedTime = System.nanoTime()-startime;
            appendMessage(" incline1: "+incline1+" incline 2: "+incline2+" elapsed time: "+elapsedTime+ "<br>");
           results+=" incline1: "+incline1+" incline 2: "+incline2+" elapsed time: "+elapsedTime+ "\n";
        }
        elapsedTime = System.nanoTime()-startime;
        aIncline2 = hCmd.getActualIncline();
        timeMillisecs = elapsedTime/1.0E06;
        appendMessage("Pause time was "+timeMillisecs+" milliseconds<br>");
        results+="Pause time was "+timeMillisecs+" milliseconds\n";

        totalTestsCount++;
        if(timeMillisecs > 400 && timeMillisecs < 600)
        {
            if(aIncline2 < aIncline1) {
                results +="\n* PASS *\n\n";
                results+="Elapsed time was "+timeMillisecs+" ms which is within the valid range of 400-600 ms and the incline changed direction!\n";
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("Elapsed time was "+timeMillisecs+" ms which is within the valid range of 400-600 ms and the incline changed direction!<br>");

            }
            else
            {
                results +="\n* FAIL *\n\n";
                results+="Elapsed time was "+timeMillisecs+" ms which is within the valid range of 400-600 ms BUT incline DIDN'T changed direction!\n";
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("Elapsed time was "+timeMillisecs+" ms which is within the valid range of 400-600 ms BUT incline DIDN'T changed direction!<br>");
                issuesListHtml+="<br>- "+"Elapsed time was "+timeMillisecs+" ms which is within the valid range of 400-600 ms BUT incline DIDN'T changed direction!<br>";
                issuesList+="\n- "+"Elapsed time was "+timeMillisecs+" ms which is within the valid range of 400-600 ms BUT incline DIDN'T changed direction!\n";
                testValidation  = "FAILED";
                failsCount++;
            }
        }
        else
        {
            results +="\n* FAIL *\n\n";
            results+="Elapsed time was "+timeMillisecs+" ms which is out of the valid range of 400-600 ms\n";
            appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
            appendMessage("Elapsed time was "+timeMillisecs+" ms which is out of the valid range of 400-600 ms<br>");
            issuesListHtml+="<br>- "+"Elapsed time was "+timeMillisecs+" ms which is out of the valid range of 400-600 ms<br>";
            issuesListHtml+="\n- "+"Elapsed time was "+timeMillisecs+" ms which is out of the valid range of 400-600 ms\n";
            testValidation  = "FAILED";
            failsCount++;

        }

        results+=resultsSummaryTemplate(testValidation,currentVersion,gitHubWikiName,failsCount,issuesList,issuesListHtml,totalTestsCount);
        return results;
    }

    /**
     * Tests incline calibration routine
     * @return text log of test results
     * @throws Exception
     */

    public String inclineCalibration() throws Exception{
        String results = "";
        long time = 500;

        do{
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE,ModeId.MAINTENANCE);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again


        do{
            calibrateCmd = new FecpCommand(MainDevice.getSubDevice(DeviceId.GRADE).getCommand(CommandId.CALIBRATE),hCmd);
            mSFitSysCntrl.getFitProCntrl().addCmd(calibrateCmd);
            Thread.sleep(time);
        }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again


        return results;
    }

    /**
     * Runs all Incline tests
     * @return text log of test results
     * @throws Exception
     */
        @Override
    public String runAll() {
        String results="";
        try {
//        results+=this.inclineRetentionDmkRecall();
          results+=this.incline400msPause();
          results+=this.retainedIncline();
          results+=this.speedInclineLimits();
          results+=this.stopIncline();
          results+=this.inclineController();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
            return results;
    }
}
