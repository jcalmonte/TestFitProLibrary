package com.ifit.sfit.sparky.tests;

import com.ifit.sfit.sparky.helperclasses.CommonFeatures;
import com.ifit.sfit.sparky.helperclasses.HandleCmd;
import com.ifit.sfit.sparky.helperclasses.SFitSysCntrl;
import com.ifit.sfit.sparky.testsdrivers.BaseTest;
import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeId;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.SetTestingKeyCmd;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.device.Device;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.key.KeyCodes;
import com.ifit.sparky.fecp.interpreter.status.StatusId;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Calendar;

/**
 * Created by jc.almonte on 8/6/14.
 */
public class TestTreadmillKeyCodes extends CommonFeatures {

        private String testValidation = "", currentVersion="", gitHubWikiName="", issuesListHtml="", issuesList="";
        private int failsCount = 0, totalTestsCount = 0;
        private FecpCommand sendKeyCmd;
        private String emailAddress;
        private DeviceId deviceId;
        public TestTreadmillKeyCodes(FecpController fecpController, BaseTest act, SFitSysCntrl ctrl) {
            //Get controller sent from the main activity (TestApp)
            try {
                this.mFecpController = fecpController;
                this.mAct = act;
                this.mSFitSysCntrl = ctrl;
                this.emailAddress = "jc.almonte@iconfitness.com";
                hCmd = new HandleCmd(this.mAct);// Init handlers
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
                    deviceId = mFecpController.getSysDev().getInfo().getDevId();
                    this.currentVersion = "SAL v"+ String.valueOf(mFecpController.getVersion());
                    this.wrCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);
                    this.rdCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd,0,100);
                    ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.MAX_GRADE);
                    ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.MIN_GRADE);
                    ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.ACTUAL_INCLINE);
                    ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.ACTUAL_KPH);
                    ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.KPH);
                    ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.GRADE);
                    ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.AGE);
                    ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.WEIGHT);
                    ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
                    ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.PAUSE_TIMEOUT);
                    ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.IDLE_TIMEOUT);
                    ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.RUNNING_TIME);
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



//TODO: In the past when this tests worked, you could hear the buzzer clicking sound when key command was sent
//TODO: Write the code for the rest of the Keys to have it ready to go as soon as set_testing_key is working

    /**
     * Simulates stop key press and verifies it works as expected
     * @return text log of test results
     * @throws Exception
     */
 public String stopKey() throws Exception{
            //Redmine Support #925
            //Test Stop button press
            //Set mode to Running
            //Simulate Stop button press
            //Validate that mode is changed to Pause

            String results="";
            gitHubWikiName = "Stop-Key-Test";
            testValidation ="PASSED";
            issuesListHtml = "";
            issuesList = "";
            failsCount=0;
            totalTestsCount = 0;
            mAct.filename = "Stop Key.txt";

            double timeOfTest = 0; //how long test took in seconds
            long startTestTimer = System.nanoTime();
            long time = 500;
            String currentMode;

            results += "\n\n------------------STOP KEY TEST---------------\n\n";
            results+= Calendar.getInstance().getTime() + "\n\n";
            appendMessage("<br><br>------------------STOP KEY TEST------------------<br><br>");
            appendMessage(Calendar.getInstance().getTime() + "<br><br>");

            currentMode = hCmd.getMode().getDescription();
            results += "Mode currently set to " + currentMode + "\n";
            appendMessage("Mode currently set to " + currentMode + "<br>");
            results += "setting the mode to running...\n";
            appendMessage("setting the mode to running...<br>");

            //set the mode to running
         do{
             ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
             Thread.sleep(time);
             appendMessage("Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
             results+="Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
         }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

            appendMessage("Wait 5 secs...<br>");
            results+="Wait 5 secs...\n";

            Thread.sleep(5000 );//Run for 5 seconds
            currentMode = hCmd.getMode().getDescription();

            results += "Mode currently set to " + currentMode + "\n";
            appendMessage("Mode currently set to " + currentMode + "<br>");

            results += "sending the stop key command...\n";
            appendMessage("sending the stop key command...<br>");
            Device keyPressTemp = this.MainDevice.getSubDevice(DeviceId.KEY_PRESS);

            if(keyPressTemp != null){
                Command writeKeyPressCmd = keyPressTemp.getCommand(CommandId.SET_TESTING_KEY);
                if(writeKeyPressCmd != null){
                    sendKeyCmd = new FecpCommand(writeKeyPressCmd, hCmd);
                    ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyCode(KeyCodes.STOP);
                    ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyOverride(true);
                    ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setTimeHeld(1000);
                    ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setIsSingleClick(true);
                }
            }

            do{
                mSFitSysCntrl.getFitProCntrl().addCmd(sendKeyCmd);
                Thread.sleep(time);
                results += "Status of sending Stop key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
                appendMessage("Status of sending Stop key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
            }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again


            do{
                 mSFitSysCntrl.getFitProCntrl().removeCmd(sendKeyCmd);
                 Thread.sleep(time);
                 results += "Status of removing Stop key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
                 appendMessage("Status of removing Stop key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
            }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

            currentMode = hCmd.getMode().getDescription();
            totalTestsCount++;
            //When the Stop key is pressed, it should change the mode from Running to Pause Mode
            if(currentMode.equals("Pause Mode")){
                results += "\n* PASS *\n\n";
                results += "Stop key successfully changed Running Mode to Pause Mode\n";

                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("Stop key successfully changed Running Mode to Pause Mode\n");

            }
            else{
                results += "\n* FAIL *\n\n";
                results += "Mode should be changed to Pause Mode, but is currently set at " + currentMode + "\n";

                appendMessage(" <br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("Mode should have changed to Pause Mode, but is currently set at " + currentMode + "<br>");
                issuesListHtml+="<br>- Mode should have changed to Pause Mode, but is currently set at " + currentMode + "<br>";
                issuesList+="\n- Mode should have changed to Pause Mode, but is currently set at " + currentMode + "\n";
                failsCount++;
                testValidation = "FAILED";
            }


         do{
             ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
             Thread.sleep(time);
             appendMessage("<br>Status of settting mode to PAUSE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
             results+="\nStatus of settting mode to PAUSE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
         }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

         do{
             ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
             Thread.sleep(time);
             appendMessage("<br>Status of settting mode to RESULTS "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
             results+="\nStatus of settting mode to RESULTS "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
         }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again
         do{
             ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
             Thread.sleep(time);
             appendMessage("<br>Status of settting mode to IDLE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
             results+="\nStatus of settting mode to IDLE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
         }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

     timeOfTest = System.nanoTime() - startTestTimer;
            timeOfTest = timeOfTest / 1.0E09;

            appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
            results+="\nThis test took a total of "+timeOfTest+" secs \n";
            results+=resultsSummaryTemplate(testValidation,currentVersion,gitHubWikiName,failsCount,issuesList,issuesListHtml,totalTestsCount);
            return results;
        }

    /**
     * Simulates start key press and verifies it works as expected
     * @return text log of test results
     * @throws Exception
     */
 public String startKey() throws Exception{
        //Redmine Support #1170
        //Testing Start Key Press
//        Read current mode
//        Simulate Start key press
//        Validate that mode is changed to Running
//        Change mode to Pause (stop)

        String results="";
        gitHubWikiName = "Start-Key-Test";
        testValidation ="PASSED";
        issuesListHtml = "";
        issuesList = "";
        failsCount=0;
        totalTestsCount = 0;
        mAct.filename = "Start Key.txt";

        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();
        long time = 500;
        String currentMode;

        results += "\n\n------------------START KEY TEST---------------\n\n";
        results+= Calendar.getInstance().getTime() + "\n\n";
        appendMessage("<br><br>------------------START KEY TEST------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        currentMode = hCmd.getMode().getDescription();

        results += "Mode currently set to " + currentMode + "\n";
        appendMessage("Mode currently set to " + currentMode + "<br>");

        results += "sending the start key command...\n";
        appendMessage("sending the start key command...<br>");

        Device keyPressTemp = this.MainDevice.getSubDevice(DeviceId.KEY_PRESS);

        if(keyPressTemp != null){
            Command writeKeyPressCmd = keyPressTemp.getCommand(CommandId.SET_TESTING_KEY);
            if(writeKeyPressCmd != null){
                sendKeyCmd = new FecpCommand(writeKeyPressCmd, hCmd);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyCode(KeyCodes.START);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyOverride(true);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setTimeHeld(1000);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setIsSingleClick(true);
            }
        }

     do{
         mSFitSysCntrl.getFitProCntrl().addCmd(sendKeyCmd);
         Thread.sleep(time);
         results += "Status of sending Start key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
         appendMessage("Status of sending Start key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
     }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again


     do{
         mSFitSysCntrl.getFitProCntrl().removeCmd(sendKeyCmd);
         Thread.sleep(time);
         results += "Status of removing Start key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
         appendMessage("Status of removing Start key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
     }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again


        currentMode = hCmd.getMode().getDescription();
        totalTestsCount++;
        //When the Stop key is pressed, it should change the mode from Running to Pause Mode
        if(currentMode.equals("Running Mode")){
            results += "\n* PASS *\n\n";
            results += "Start key successfully changed Mode to Running\n";

            appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
            appendMessage("Start key successfully changed Mode to Running\n");


        }
        else{
            results += "\n* FAIL *\n\n";
            results += "Mode should be changed to Running, but is currently set at " + currentMode + "\n";

            appendMessage(" <br><font color = #ff0000>* FAIL *</font><br><br>");
            appendMessage("Mode should be changed to Running, but is currently set at " + currentMode + "<br>");
            issuesListHtml+="<br>- Mode should have changed to Running, but is currently set at " + currentMode + "<br>";
            issuesList+="\n- Mode should have changed to Running, but is currently set at " + currentMode + "\n";
            failsCount++;
            testValidation = "FAILED";
        }


         do{
             ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
             Thread.sleep(time);
             appendMessage("<br>Status of settting mode to PAUSE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
             results+="\nStatus of settting mode to PAUSE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
         }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

         do{
             ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
             Thread.sleep(time);
             appendMessage("<br>Status of settting mode to RESULTS "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
             results+="\nStatus of settting mode to RESULTS "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
         }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again
         do{
             ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
             Thread.sleep(time);
             appendMessage("<br>Status of settting mode to IDLE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
             results+="\nStatus of settting mode to IDLE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
         }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again


     timeOfTest = System.nanoTime() - startTestTimer;
        timeOfTest = timeOfTest / 1.0E09;

        appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
        results+="\nThis test took a total of "+timeOfTest+" secs \n";
        results+=resultsSummaryTemplate(testValidation,currentVersion,gitHubWikiName,failsCount,issuesList,issuesListHtml,totalTestsCount);
        return results;
    }

    /**
     * Simulates Incline up key press and verifies it works as expected
     * @return text log of test results
     * @throws Exception
     */
 public String inclineUpKey() throws Exception{
     //Redmine Support #1171
     //Testing Incline Up key press
     //1. Initialize Incline to min
     //2. Simulate Incline Up key press
     //3. Validate that Incline went up 0.5%
     //4. Repeat steps 2-3 until max incline reached

     String results ="";
     gitHubWikiName = "Incline-Up-Test";
     testValidation ="PASSED";
     issuesListHtml = "";
     issuesList = "";
     failsCount=0;
     totalTestsCount = 0;
     mAct.filename = "Incline Up Key.txt";
     double timeOfTest = 0; //how long test took in seconds
     long startTestTimer = System.nanoTime();
     double currentActualIncline = 0;
     double incline1 = 0;
     double incline2 = 0;
     final double MAX_INCLINE =  15; //hCmd.getMaxIncline(); // The motor we are using has max incline of 15%
     final double MIN_INCLINE = hCmd.getMinIncline();
     long elapsedTime = 0;
     double seconds = 0;
     long startime = 0;
     long time = 500;


     results += "\n\n------------------INCLINE UP KEY TEST---------------\n\n";
     results+= Calendar.getInstance().getTime() + "\n\n";
     appendMessage("<br><br>------------------INCLINE UP KEY TEST------------------<br><br>");
     appendMessage(Calendar.getInstance().getTime() + "<br><br>");


     results += "setting incline to min...\n";
     appendMessage("setting incline to min...<br>");
    //Set value for the incline
     do{
         ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, MIN_INCLINE);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(time);
         appendMessage("Status of setting incline to min: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
         results+="Status of setting incline to min: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
     }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

     appendMessage("Checking incline will reach set value...<br>");
     results+="Checking incline will reach set value...\n";
     startime= System.nanoTime();
     do
     {
         currentActualIncline = hCmd.getActualIncline();
         Thread.sleep(350);
         appendMessage("Current Incline is: " + currentActualIncline+ " goal: " + MIN_INCLINE+" time elapsed: "+seconds+"<br>");
         results+="Current Incline is: " + currentActualIncline+ " goal: " + MIN_INCLINE+" time elapsed: "+seconds+"\n";
         elapsedTime = System.nanoTime() - startime;
         seconds = elapsedTime / 1.0E09;
     } while(MIN_INCLINE!=currentActualIncline && seconds < 60);//Do while the incline hasn't reached its point yet or took more than 1.5 mins

     results += "sending the start key command...\n";
     appendMessage("sending the start key command...<br>");
     Device keyPressTemp = this.MainDevice.getSubDevice(DeviceId.KEY_PRESS);

     if(keyPressTemp != null){
         Command writeKeyPressCmd = keyPressTemp.getCommand(CommandId.SET_TESTING_KEY);
         if(writeKeyPressCmd != null){
             sendKeyCmd = new FecpCommand(writeKeyPressCmd, hCmd);
             ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyCode(KeyCodes.INCLINE_UP);
             ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyOverride(true);
             ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setTimeHeld(1000);
             ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setIsSingleClick(true);
         }
     }

     //Increment incline by 0.5 and verify the increment happened. Repeat until max incline reached

     for (double i = MIN_INCLINE; i<=MAX_INCLINE; i+=0.5)
     {
         incline1 = hCmd.getActualIncline();

         do{
             mSFitSysCntrl.getFitProCntrl().addCmd(sendKeyCmd);
             Thread.sleep(time);
             results += "Status of sending Incline Up key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
             appendMessage("Status of sending Incline Up key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
         }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

         Thread.sleep(1500); //Wait 1.5 secs to make sure incline incremented by 0.5
         incline2 = hCmd.getActualIncline();

        /* This part might be optional. Depends on wheter a command already added exception is thrown or not
         mSFitSysCntrl.getFitProCntrl().removeCmd(sendKeyCmd);
         Thread.sleep(1000); */
         totalTestsCount++;
         if((incline2 - incline1) == 0.5)
         {
             appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
             appendMessage("Previous incline: "+incline1+" current incline: "+incline2+". Incline correclty incremented by "+(incline2-incline1)+" %<br>");

             results+="\n* PASS *\n\n";
             results+="Previous incline: "+incline1+" current incline: "+incline2+". Incline correclty incremented by "+(incline2-incline1)+" %\n";
         }
         else
         {
             appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
             appendMessage("Previous incline: "+incline1+" current incline: "+incline2+". Incline increment was "+(incline2-incline1)+" % and should have been 0.5 %<br>");

             results+="\n* FAIL *\n\n";
             results+="Previous incline: "+incline1+" current incline: "+incline2+". Incline increment was "+(incline2-incline1)+" % and should have been 0.5 %<br>";
             issuesListHtml+="<br>- Previous incline: "+incline1+" current incline: "+incline2+". Incline increment was "+(incline2-incline1)+" % and should have been 0.5 %<br>";
             issuesList+="\n- Previous incline: "+incline1+" current incline: "+incline2+". Incline increment was "+(incline2-incline1)+" % and should have been 0.5 %\n";
             failsCount++;
             testValidation = "FAILED";
         }
     }

     timeOfTest = System.nanoTime() - startTestTimer;
     timeOfTest = timeOfTest / 1.0E09;

     appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
     results+="\nThis test took a total of "+timeOfTest+" secs \n";
     results+=resultsSummaryTemplate(testValidation,currentVersion,gitHubWikiName,failsCount,issuesList,issuesListHtml,totalTestsCount);
     return results;
 }

    /**
     * Simulates Incline down key press and verifies it works as expected
     * @return text log of test results
     * @throws Exception
     */
 public String inclineDownKey() throws Exception{
        //Redmine Support #1171
        //Testing Incline Up key press
        //1. Initialize Incline to min
        //2. Simulate Incline Up key press
        //3. Validate that Incline went up 0.5%
        //4. Repeat steps 2-3 until max incline reached

        String results ="";
        gitHubWikiName = "Incline-Down-Test";
        testValidation ="PASSED";
        issuesListHtml = "";
        issuesList = "";
        failsCount=0;
        totalTestsCount = 0;
        mAct.filename = "Incline Down Key.txt";
        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();
        double currentActualIncline = 0;
        double incline1 = 0;
        double incline2 = 0;
        final double MAX_INCLINE =  15; //hCmd.getMaxIncline(); // The motor we are using has max incline of 15%
        final double MIN_INCLINE = hCmd.getMinIncline();
        long elapsedTime = 0;
        double seconds = 0;
        long startime = 0;
        long time = 500;


        results += "\n\n------------------INCLINE DOWN KEY TEST---------------\n\n";
        results+= Calendar.getInstance().getTime() + "\n\n";
        appendMessage("<br><br>------------------INCLINE DOWN KEY TEST------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");


        results += "setting incline to max...\n";
        appendMessage("setting incline to max...<br>");
        //Set value for the incline
         do{
             ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, MAX_INCLINE);
             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
             Thread.sleep(time);
             appendMessage("Status of setting incline to max: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
             results+="Status of setting incline to max: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
         }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

        appendMessage("Checking incline will reach set value...<br>");
        results+="Checking incline will reach set value...\n";

        startime= System.nanoTime();
        do
        {
            currentActualIncline = hCmd.getActualIncline();
            Thread.sleep(350);
            appendMessage("Current Incline is: " + currentActualIncline+ " goal: " + MAX_INCLINE+" time elapsed: "+seconds+"<br>");
            results+="Current Incline is: " + currentActualIncline+ " goal: " + MAX_INCLINE+" time elapsed: "+seconds+"\n";
            elapsedTime = System.nanoTime() - startime;
            seconds = elapsedTime / 1.0E09;
        } while(MAX_INCLINE!=currentActualIncline && seconds < 60);//Do while the incline hasn't reached its point yet or took more than 1.5 mins

        results += "sending the start key command...\n";
        appendMessage("sending the start key command...<br>");
        Device keyPressTemp = this.MainDevice.getSubDevice(DeviceId.KEY_PRESS);

        if(keyPressTemp != null){
            Command writeKeyPressCmd = keyPressTemp.getCommand(CommandId.SET_TESTING_KEY);
            if(writeKeyPressCmd != null){
                sendKeyCmd = new FecpCommand(writeKeyPressCmd, hCmd);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyCode(KeyCodes.INCLINE_DOWN);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyOverride(true);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setTimeHeld(1000);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setIsSingleClick(true);
            }
        }

        //Decrement incline by 0.5 and verify the decrement happened. Repeat until min incline reached

        for (double i = MAX_INCLINE; i>=MIN_INCLINE; i-=0.5)
        {
            incline1 = hCmd.getActualIncline();

            do{
                mSFitSysCntrl.getFitProCntrl().addCmd(sendKeyCmd);
                Thread.sleep(time);
                results += "Status of sending Incline Down key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
                appendMessage("Status of sending Incline Down key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
            }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

            Thread.sleep(1500); //Wait 1.5 secs to make sure incline decremented by 0.5
            incline2 = hCmd.getActualIncline();

        /* This part might be optional. Depends on wheter a command already added exception is thrown or not
         mSFitSysCntrl.getFitProCntrl().removeCmd(sendKeyCmd);
         Thread.sleep(1000); */
            totalTestsCount++;
            if( (incline2 - incline1) == -0.5)
            {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("Previous incline: "+incline1+" current incline: "+incline2+". Incline correclty decremented by "+(incline2-incline1)+" %<br>");

                results+="\n* PASS *\n\n";
                results+="Previous incline: "+incline1+" current incline: "+incline2+". Incline correclty decremented by "+(incline2-incline1)+" %\n";
            }
            else
            {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("Previous incline: "+incline1+" current incline: "+incline2+". Incline decrement was "+(incline2-incline1)+" % and should have been -0.5 %<br>");

                results+="\n* FAIL *\n\n";
                results+="Previous incline: "+incline1+" current incline: "+incline2+". Incline decrement was "+(incline2-incline1)+" % and should have been 0.5 %<br>";
                issuesListHtml+="<br>- Previous incline: "+incline1+" current incline: "+incline2+". Incline decrement was "+(incline2-incline1)+" % and should have been -0.5 %<br>";
                issuesList+="\n- Previous incline: "+incline1+" current incline: "+incline2+". Incline increment was "+(incline2-incline1)+" % and should have been -0.5 %\n";
                failsCount++;
                testValidation = "FAILED";
            }
        }

        timeOfTest = System.nanoTime() - startTestTimer;
        timeOfTest = timeOfTest / 1.0E09;

        appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
        results+="\nThis test took a total of "+timeOfTest+" secs \n";
        results+=resultsSummaryTemplate(testValidation,currentVersion,gitHubWikiName,failsCount,issuesList,issuesListHtml,totalTestsCount);
        return results;
    }

    /**
     * Simulates Speed up key press and verifies it works as expected
     * @return text log of test results
     * @throws Exception
     */
 public String speedUpKey() throws Exception {
     //Testing Speed Up key press
     //1. Set mode to running
     //2. Simulate Speed Up key press
     //3. Validate that Speed went up by 0.1 kph
     //4. Repeat steps 2-3 until max speed reached


     String results = "";
     gitHubWikiName = "Speed-Up-Key-Test";
     testValidation ="PASSED";
     issuesListHtml = "";
     issuesList = "";
     failsCount=0;
     totalTestsCount = 0;
     mAct.filename = "SpeedUp Key.txt";
     double timeOfTest = 0; //how long test took in seconds
     long startTestTimer = System.nanoTime();
     double currentSpeed = 0;
     final double MAX_SPEED = hCmd.getMaxSpeed();
     long elapsedTime = 0;
     double seconds = 0;
     long startime = 0;
     double speed1 = 0;
     double speed2 = 0;
     long time = 500;


     results += "\n\n------------------SPEED UP KEY TEST---------------\n\n";
     results+= Calendar.getInstance().getTime() + "\n\n";
     appendMessage("<br><br>------------------SPEED UP KEY TEST------------------<br><br>");
     appendMessage(Calendar.getInstance().getTime() + "<br><br>");


     results += "setting mode to running...\n";
     appendMessage("setting mode to running...<br>");
     do{
         ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(time);
         appendMessage("Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
         results+="Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
     }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

     results += "sending the Speed Up key command...\n";
     appendMessage("sending the Speed Up key command...<br>");
     Device keyPressTemp = this.MainDevice.getSubDevice(DeviceId.KEY_PRESS);

     if(keyPressTemp != null){
         Command writeKeyPressCmd = keyPressTemp.getCommand(CommandId.SET_TESTING_KEY);
         if(writeKeyPressCmd != null){
             sendKeyCmd = new FecpCommand(writeKeyPressCmd, hCmd);
             ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyCode(KeyCodes.SPEED_UP);
             ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyOverride(true);
             ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setTimeHeld(1000);
             ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setIsSingleClick(true);
         }
     }

     double expected = 1.0;
//Tests range from 1.1 up to MAX_SPEED
     while(currentSpeed < MAX_SPEED) {
        speed1 = hCmd.getSpeed();

         do{
             mSFitSysCntrl.getFitProCntrl().addCmd(sendKeyCmd);
             Thread.sleep(time);
             results += "Status of sending Speed Up key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
             appendMessage("Status of sending Speed Up key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
         }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

         speed2 = hCmd.getSpeed();

        /* This part might be optional. Depends on wheter a command already added exception is thrown or not
         mSFitSysCntrl.getFitProCntrl().removeCmd(sendKeyCmd);
         Thread.sleep(1000); */
         currentSpeed = hCmd.getSpeed(); //TODO: use actual speed once speed is accurate
         appendMessage("Current speed is: " + currentSpeed + " kph<br>");
         results += "Current speed is: " + currentSpeed + " kph\n";
         totalTestsCount++;
         if ((speed2-speed1) == 0.1) {
             appendMessage("<br><font color = #00ff00>* PASS *</font><br><br> Speed incremented by 0.1 <br><br>");

             results += "\n* PASS *\n\nSpeed Up button incremented by 0.1\n";

         } else {
             appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br> Speed did not increment by 0.1 <br><br>");
             results += "\n* FAIL *\n\n Speed did not increment by 0.1\n";
             issuesListHtml+="<br>- Speed did not increment by 0.1 <br>";
             issuesList+="\n Speed did not increment by 0.1 \n";
             failsCount++;
             testValidation = "FAILED";

         }
     }

     do{
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(time);
         appendMessage("<br>Status of settting mode to PAUSE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
         results+="\nStatus of settting mode to PAUSE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
     }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

     do{
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(time);
         appendMessage("<br>Status of settting mode to RESULTS "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
         results+="\nStatus of settting mode to RESULTS "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
     }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again
     do{
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(time);
         appendMessage("<br>Status of settting mode to IDLE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
         results+="\nStatus of settting mode to IDLE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
     }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

     timeOfTest = System.nanoTime() - startTestTimer;
     timeOfTest = timeOfTest / 1.0E09;

     appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
     results+=resultsSummaryTemplate(testValidation,currentVersion,gitHubWikiName,failsCount,issuesList,issuesListHtml,totalTestsCount);
     results+="\nThis test took a total of "+timeOfTest+" secs \n";

     return results;
 }

    /**
     * Simulates Speed down key press and verifies it works as expected
     * @return text log of test results
     * @throws Exception
     */
 public String speedDownKey() throws Exception {
        //Testing Speed Down key press
//        1. Set mode to running
//        2. Set speed to max
//        3. Simulate Speed Down key press
//        4. Validate that Speed went down by 0.1 kph
//        5. Repeat steps 2-4 until min speed reached


        String results = "";
        gitHubWikiName = "Speed-Down-Key-Test";
        testValidation ="PASSED";
        issuesListHtml = "";
        issuesList = "";
        failsCount=0;
        totalTestsCount = 0;
        mAct.filename = "SpeedDown Key.txt";
        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();
        double currentSpeed = 0;
        long elapsedTime = 0;
        double seconds = 0;
        long startime = 0;
        final double MAX_SPEED = hCmd.getMaxSpeed();
        long time = 500;
        double speed1 = 0;
        double speed2 = 0;


        results += "\n\n------------------SPEED DOWN KEY TEST---------------\n\n";
        results+= Calendar.getInstance().getTime() + "\n\n";
        appendMessage("<br><br>------------------SPEED DOWN KEY TEST------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");


        results += "setting mode to running...\n";
        appendMessage("setting mode to running...<br>");

         do{
             ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
             Thread.sleep(time);
             appendMessage("Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
             results+="Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
         }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

        results += "setting speed to max...\n";
        appendMessage("setting speed to max...<br>");

         do{
            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, MAX_SPEED);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("Status of setting speed to max: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
            results+="Status of setting speed to max: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
         }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

       Thread.sleep(20000);// Wait for speed to reach max

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

        results += "sending the Speed Down key command...\n";
        appendMessage("sending the Speed Up key command...<br>");
        Device keyPressTemp = this.MainDevice.getSubDevice(DeviceId.KEY_PRESS);

        if(keyPressTemp != null){
            Command writeKeyPressCmd = keyPressTemp.getCommand(CommandId.SET_TESTING_KEY);
            if(writeKeyPressCmd != null){
                sendKeyCmd = new FecpCommand(writeKeyPressCmd, hCmd);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyCode(KeyCodes.SPEED_UP);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyOverride(true);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setTimeHeld(1000);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setIsSingleClick(true);
            }
        }

     while(currentSpeed > 1) {
         speed1 = hCmd.getSpeed();

         do{
             mSFitSysCntrl.getFitProCntrl().addCmd(sendKeyCmd);
             Thread.sleep(time);
             results += "Status of sending Speed Down key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
             appendMessage("Status of sending Speed Down key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
         }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

         speed2 = hCmd.getSpeed();

        /* This part might be optional. Depends on wheter a command already added exception is thrown or not
         mSFitSysCntrl.getFitProCntrl().removeCmd(sendKeyCmd);
         Thread.sleep(1000); */
         currentSpeed = hCmd.getSpeed(); //TODO: use actual speed once speed is accurate
         appendMessage("Current speed is: " + currentSpeed + " kph<br>");
         results += "Current speed is: " + currentSpeed + " kph\n";
         totalTestsCount++;
         if ((speed2-speed1) == - 0.1) {
             appendMessage("<br><font color = #00ff00>* PASS *</font><br><br> Speed decremented by 0.1 <br><br>");

             results += "\n* PASS *\n\nSpeed decremented by 0.1\n";

         } else {
             appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br> Speed Up button did not increment by 0.1 <br><br>");
             results += "\n* FAIL *\n\n Speed did not decrement by 0.1\n";
             issuesListHtml+="<br>- Speed did not decrement speed by 0.1 <br>";
             issuesList+="\n Speed did not decrement speed by 0.1 \n";
             failsCount++;
             testValidation = "FAILED";

         }
     }

     do{
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(time);
         appendMessage("<br>Status of settting mode to PAUSE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
         results+="\nStatus of settting mode to PAUSE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
     }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

     do{
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(time);
         appendMessage("<br>Status of settting mode to RESULTS "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
         results+="\nStatus of settting mode to RESULTS "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
     }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again
     do{
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(time);
         appendMessage("<br>Status of settting mode to IDLE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
         results+="\nStatus of settting mode to IDLE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
     }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again


     timeOfTest = System.nanoTime() - startTestTimer;
     timeOfTest = timeOfTest / 1.0E09;

     appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
     results+="\nThis test took a total of "+timeOfTest+" secs \n";
     results+=resultsSummaryTemplate(testValidation,currentVersion,gitHubWikiName,failsCount,issuesList,issuesListHtml,totalTestsCount);
     return results;
    }

    /**
     * Simulates Quick Incline key press and verifies it works as expected
     * @return text log of test results
     * @throws Exception
     */
 public String quickInclineKeys() throws Exception{
     /*
     * 1. Read max and min inclines
     * 2. Use max and min incline values to set max and min keycodes
     * 3. Send min incline keycode
     * 4. Check that incline reached set value
     * 5. Send next quick incline keycode
     * 6. Repeat steps 4-5 until max incline is reached
     * */

     String results ="";
     gitHubWikiName = "Quick-Incline-Keys-Test";
     testValidation ="PASSED";
     issuesListHtml = "";
     issuesList = "";
     failsCount=0;
     totalTestsCount = 0;
     mAct.filename = "Quick Incline Keys.txt";
     double timeOfTest = 0; //how long test took in seconds
     long startTestTimer = System.nanoTime();
     double currentActualIncline = 0;
     double MAX_INCLINE =  15; //hCmd.getMaxIncline(); // The motor we are using has max incline of 15%
     double MIN_INCLINE = hCmd.getMinIncline();
     long elapsedTime = 0;
     double seconds = 0;
     long startime = 0;
     long time = 500;
     KeyCodes [] kc = null;
     double []inclines = null;

     /*TODO: Generalize tests for all consoles. For now assume Incline trainer with max incline 40% and min -6%
     *
     * DONE!
     *
     * TODO: Ask Quinn about standarizing incline limits because every machine has different limits
     * */

     switch (deviceId.name())
     {
         case "INCLINE_TRAINER":
             if(MAX_INCLINE == 40) {
                 kc = new KeyCodes[]{KeyCodes.INCLINE_NEG_6, KeyCodes.INCLINE_NEG_4, KeyCodes.INCLINE_NEG_2, KeyCodes.INCLINE_0, KeyCodes.INCLINE_5,
                         KeyCodes.INCLINE_10, KeyCodes.INCLINE_15/*,KeyCodes.INCLINE_20,KeyCodes.INCLINE_25,KeyCodes.INCLINE_30,KeyCodes.INCLINE_35,KeyCodes.INCLINE_40*/};
                 inclines = new double[]{-6, -4, -2, 0, 5, 10, 15/*,20,25,30,35,40*/};
             }
             else if(MAX_INCLINE == 30)
             {
                 kc = new KeyCodes[]{KeyCodes.INCLINE_NEG_6, KeyCodes.INCLINE_NEG_4, KeyCodes.INCLINE_NEG_2, KeyCodes.INCLINE_0, KeyCodes.INCLINE_5,
                         KeyCodes.INCLINE_10, KeyCodes.INCLINE_15/*,KeyCodes.INCLINE_20,KeyCodes.INCLINE_25,KeyCodes.INCLINE_30*/};
                 inclines = new double[]{-6, -4, -2, 0, 5, 10, 15/*,20,25,30*/};
             }
         break;

         case "TREADMILL":
             kc = new KeyCodes[]{KeyCodes.INCLINE_NEG_3, KeyCodes.INCLINE_NEG_2, KeyCodes.INCLINE_NEG_1, KeyCodes.INCLINE_0, KeyCodes.INCLINE_1,
                     KeyCodes.INCLINE_2, KeyCodes.INCLINE_4,KeyCodes.INCLINE_6,KeyCodes.INCLINE_8,KeyCodes.INCLINE_10,KeyCodes.INCLINE_12,KeyCodes.INCLINE_15};
             inclines = new double[]{-3, -2, -1, 0, 1, 2, 4, 6, 8, 10, 12, 15};
         break;
     }



     results += "\n\n------------------QUICK INCLINE KEYS TEST---------------\n\n";
     results+= Calendar.getInstance().getTime() + "\n\n";
     appendMessage("<br><br>------------------QUICK INCLINE KEYS TEST------------------<br><br>");
     appendMessage(Calendar.getInstance().getTime() + "<br><br>");

     Device keyPressTemp = this.MainDevice.getSubDevice(DeviceId.KEY_PRESS);

     if(keyPressTemp != null){
         Command writeKeyPressCmd = keyPressTemp.getCommand(CommandId.SET_TESTING_KEY);
         if(writeKeyPressCmd != null){
             sendKeyCmd = new FecpCommand(writeKeyPressCmd, hCmd);
             ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyOverride(true);
             ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setTimeHeld(1000);
             ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setIsSingleClick(true);
         }
     }

      //Loop Through each incline code
     for(int i=0; i < kc.length; i++)
     {
         appendMessage("Sending quick incline keycode: "+kc[i].name()+"<br>");
         results+="Sending quick incline keycode: "+kc[i].name()+"\n";
         do{
             ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyCode(kc[i]);
             mSFitSysCntrl.getFitProCntrl().addCmd(sendKeyCmd);
             Thread.sleep(time);
             results += "Status of sending  key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
             appendMessage("Status of sending key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
         }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

        /* This part might be optional. Depends on wheter a command already added exception is thrown or not
         mSFitSysCntrl.getFitProCntrl().removeCmd(sendKeyCmd);
         Thread.sleep(1000); */

         startime = System.nanoTime();
         do
         {
             currentActualIncline = hCmd.getActualIncline();
             Thread.sleep(350);
             appendMessage("Current Incline is: " + currentActualIncline+ " goal: " + inclines[i]+" time elapsed: "+seconds+"<br>");
             results+="Current Incline is: " + currentActualIncline+ " goal: " + inclines[i]+" time elapsed: "+seconds+"\n";

             elapsedTime = System.nanoTime() - startime;
             seconds = elapsedTime / 1.0E09;
         } while(inclines[i]!=currentActualIncline && seconds < 60);//Do while the incline hasn't reached its point yet or took more than 1 mins

         totalTestsCount++;
         if(hCmd.getIncline() == currentActualIncline && currentActualIncline == inclines[i] )
         {
             appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
             appendMessage("Quick incline correctly set to "+kc[i].name()+"<br>");
             results+="\n* PASS *\n\n";
             results+="Quick incline correctly set to "+kc[i].name()+"\n";

         }
         else
         {
             appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
             appendMessage("Quick incline failed to be set to "+kc[i].name()+", current incline is set to "+hCmd.getActualIncline()+"<br>");

             results+="\n* FAIL *\n\n";
             results+="Quick incline failed to be set to "+kc[i].name()+", current incline is set to "+hCmd.getActualIncline()+"\n";
             issuesListHtml+="<br>- Quick incline failed to be set to "+kc[i].name()+", current incline is set to "+hCmd.getActualIncline()+"<br>";
             issuesList+="\n- Quick incline failed to be set to "+kc[i].name()+", current incline is set to "+hCmd.getActualIncline()+"\n";
             failsCount++;
             testValidation = "FAILED";
         }
     }

     timeOfTest = System.nanoTime() - startTestTimer;
     timeOfTest = timeOfTest / 1.0E09;

     appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
     results+="\nThis test took a total of "+timeOfTest+" secs \n";
     results+=resultsSummaryTemplate(testValidation,currentVersion,gitHubWikiName,failsCount,issuesList,issuesListHtml,totalTestsCount);
     return results;
 }

    /**
     * Simulates Quick speed key press and verifies it works as expected
     * @return text log of test results
     * @throws Exception
     */
 public String quickSpeedKeys() throws Exception{
     /*
     * 1. Set mode to running
     * 2. Send quickspeed command
     * 3. check that set speed has been reached
     * 4. Repeat steps 4-5 until max speed is reached
     * */

        String results ="";
        gitHubWikiName = "Quick-Speed-Key-Test";
        testValidation ="PASSED";
        issuesListHtml = "";
        issuesList = "";
        failsCount=0;
        totalTestsCount = 0;
        mAct.filename = "Quick Speed Key.txt";
        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();
        double currentSpeed = 0;
        double maxSpeed =  16; //hCmd.getMaxSpee(); // The motor we are using has max speed of 16 kph
        double minSpeed = hCmd.getMinSpeed();
        long elapsedTime = 0;
        double seconds = 0;
        long startime = 0;
        double speedMPH = 0;
        long time = 500;
        String currentMode = "";
        BigDecimal roundedResult;
        KeyCodes [] kc = null;

     /*TODO: Generalize tests for all consoles. For now assume Incline trainer with max speed of 16 kph
     *
     * Ask Quinn if speeds are standardized
     * */
     switch (deviceId.name()) {
         case "INCLINE_TRAINER":
         kc = new KeyCodes[]{KeyCodes.MPH_1, KeyCodes.MPH_2, KeyCodes.MPH_3, KeyCodes.MPH_4,
                 KeyCodes.MPH_5, KeyCodes.MPH_6, KeyCodes.MPH_7, KeyCodes.MPH_8, KeyCodes.MPH_9, KeyCodes.MPH_10};
         break;

         case "TREADMILL":
             kc = new KeyCodes[]{KeyCodes.MPH_1, KeyCodes.MPH_2, KeyCodes.MPH_3, KeyCodes.MPH_4,
                     KeyCodes.MPH_5, KeyCodes.MPH_6, KeyCodes.MPH_7, KeyCodes.MPH_8, KeyCodes.MPH_9, KeyCodes.MPH_10};
             break;
     }

        results += "\n\n------------------QUICK SPEED KEYS TEST---------------\n\n";
        results+= Calendar.getInstance().getTime() + "\n\n";
        appendMessage("<br><br>------------------QUICK SPEED KEYS TEST------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

     currentMode = hCmd.getMode().getDescription();
     results += "Mode currently set to " + currentMode + "\n";
     appendMessage("Mode currently set to " + currentMode + "<br>");
     results += "setting the mode to running...\n";
     appendMessage("setting the mode to running...<br>");

     //set the mode to running
     do{
         ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(time);
         appendMessage("Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
         results+="Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
     }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

     currentMode = hCmd.getMode().getDescription();

     results += "Mode currently set to " + currentMode + "\n";
     appendMessage("Mode currently set to " + currentMode + "<br>");



        Device keyPressTemp = this.MainDevice.getSubDevice(DeviceId.KEY_PRESS);

        if(keyPressTemp != null){
            Command writeKeyPressCmd = keyPressTemp.getCommand(CommandId.SET_TESTING_KEY);
            if(writeKeyPressCmd != null){
                sendKeyCmd = new FecpCommand(writeKeyPressCmd, hCmd);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyOverride(true);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setTimeHeld(1000);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setIsSingleClick(true);
            }
        }

        //Loop Through each quick speed code
        for(int i=0; i < kc.length; i++)
        {
            appendMessage("Sending quick speed keycode: "+kc[i].name()+"<br>");
            results+="Sending quick speed keycode: "+kc[i].name()+"\n";
         do{
            ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyCode(kc[i]);
            mSFitSysCntrl.getFitProCntrl().addCmd(sendKeyCmd);
            Thread.sleep(time);
            results += "Status of sending  key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
            appendMessage("Status of sending key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
        }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again


        /* This part might be optional. Depends on wheter a command already added exception is thrown or not
         mSFitSysCntrl.getFitProCntrl().removeCmd(sendKeyCmd);
         Thread.sleep(1000); */
            currentSpeed = hCmd.getSpeed();
            speedMPH = currentSpeed*0.621371; //Convert speed to mph.

            totalTestsCount++;
            if( Math.abs(speedMPH-(i+1)) < (0.03*(i+1)) ) // Give 3% tolerance to take care of rounding issues
            {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("Quick speed correctly set to "+kc[i].name()+" which is "+speedMPH+" mph ("+hCmd.getSpeed()+" kph)<br>");
                results+="\n* PASS *\n\n";
                results+="Quick incline correctly set to "+kc[i].name()+" which is "+speedMPH+" mph ("+hCmd.getSpeed()+" kph)\n";

            }
            else
            {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("Quick speed failed to be set to "+kc[i].name()+", mph current speed is set to "+speedMPH+" mph ("+hCmd.getSpeed()+" kph)<br>");

                results+="\n* FAIL *\n\n";
                results+="Quick speed failed to be set to "+kc[i].name()+", current speed is set to "+speedMPH+" mph ("+hCmd.getSpeed()+" kph)\n";
                issuesListHtml+="<br> Quick speed failed to be set to "+kc[i].name()+", current speed is set to "+speedMPH+" mph ("+hCmd.getSpeed()+" kph)<br>";;
                issuesList+="\n- Quick speed failed to be set to "+kc[i].name()+", current speed is set to "+speedMPH+" mph ("+hCmd.getSpeed()+" kph)\n";
                failsCount++;
                testValidation = "FAILED";
            }
        }

     do{
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(time);
         appendMessage("<br>Status of settting mode to PAUSE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
         results+="\nStatus of settting mode to PAUSE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
     }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

     do{
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(time);
         appendMessage("<br>Status of settting mode to RESULTS "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
         results+="\nStatus of settting mode to RESULTS "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
     }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again
     do{
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(time);
         appendMessage("<br>Status of settting mode to IDLE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
         results+="\nStatus of settting mode to IDLE "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
     }while(wrCmd.getCommand().getStatus().getStsId()==StatusId.FAILED); // If command failed, send it again

     timeOfTest = System.nanoTime() - startTestTimer;
     timeOfTest = timeOfTest / 1.0E09;

     appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
     results+="\nThis test took a total of "+timeOfTest+" secs \n";
     results+=resultsSummaryTemplate(testValidation,currentVersion,gitHubWikiName,failsCount,issuesList,issuesListHtml,totalTestsCount);
     return results;
    }

    /**
     * Simulates age up key press and verifies it works as expected
     * @return text log of test results
     * @throws Exception
     */

 public String ageUpKey() throws Exception{
        //Testing Age Up key press
        //1. Set age to 18
        //2. Simulate Incline Up key press
        //3. Validate that Incline went up 0.5%
        //4. Repeat steps 2-3 until max incline reached

        String results ="";
        gitHubWikiName = "Age-Up-Key-Test";
        testValidation ="PASSED";
        issuesListHtml = "";
        issuesList = "";
        failsCount=0;
        totalTestsCount = 0;
        mAct.filename = "Age Up Key.txt";
        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();
        double Age1 = 0;
        double Age2 = 0;
        double currentAge = 0;
        long elapsedTime = 0;
        double seconds = 0;
        long startime = 0;
        long time = 500;


        results += "\n\n------------------AGE UP KEY TEST---------------\n\n";
        results+= Calendar.getInstance().getTime() + "\n\n";
        appendMessage("<br><br>------------------AGE UP KEY TEST------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        currentAge = hCmd.getAge();
        appendMessage("Current age is set to: "+currentAge+"<br>");
        results+="Current age is set to: "+currentAge+"\n";

        if(currentAge!=18)
        {
            results += "setting the age to 18...\n";
            appendMessage("setting the mode 18...<br>");

            //set the age to 18
            do {
                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.AGE, 18);
                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                Thread.sleep(time);
                results += "Status of setting age to 18 years old: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
                appendMessage("Status of setting age to 18 years old: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
            }while (true);
        }

        results += "sending the age up key command...\n";
        appendMessage("sending the age up key command...<br>");
        Device keyPressTemp = this.MainDevice.getSubDevice(DeviceId.KEY_PRESS);

        if(keyPressTemp != null){
            Command writeKeyPressCmd = keyPressTemp.getCommand(CommandId.SET_TESTING_KEY);
            if(writeKeyPressCmd != null){
                sendKeyCmd = new FecpCommand(writeKeyPressCmd, hCmd);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyCode(KeyCodes.AGE_UP);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyOverride(true);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setTimeHeld(1000);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setIsSingleClick(true);
            }
        }

        //Increment age by 1 and verify the increment happened. Repeat until max age reached

        for (int i=18; i<=95; i++ )// Go through all valid ages
        {
            Age1 = hCmd.getAge();
            do{
                mSFitSysCntrl.getFitProCntrl().addCmd(sendKeyCmd);
                Thread.sleep(time);
                results += "Status of sending Age Up key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
                appendMessage("Status of sending Age Up key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
            }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again
            Age2 = hCmd.getAge();

        /* This part might be optional. Depends on wheter a command already added exception is thrown or not
         mSFitSysCntrl.getFitProCntrl().removeCmd(sendKeyCmd);
         Thread.sleep(1000); */
            totalTestsCount++;
            if((Age2 - Age1) == 1)
            {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("Previous Age: "+Age1+" current age: "+Age2+". Age correclty incremented by "+(Age2-Age1)+" %<br>");

                results+="\n* PASS *\n\n";
                results+="Previous age: "+Age1+" current age: "+Age2+". Age correclty incremented by "+(Age2-Age1)+" %\n";
            }
            else
            {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("Previous age: "+Age1+" current age: "+Age2+". age increment was "+(Age2-Age1)+" % and should have been 1 %<br>");

                results+="\n* FAIL *\n\n";
                results+="Previous age: "+Age1+" current age: "+Age2+". age increment was "+(Age2-Age1)+" % and should have been 1 %<br>";
                issuesListHtml+="<br>- Previous age: "+Age1+" current age: "+Age2+". age increment was "+(Age2-Age1)+" % and should have been 1 %<br>";
                issuesList+="\n- Previous age: "+Age1+" current age: "+Age2+". age increment was "+(Age2-Age1)+" % and should have been 1 %\n";
                failsCount++;
                testValidation = "FAILED";
            }
        }

        timeOfTest = System.nanoTime() - startTestTimer;
        timeOfTest = timeOfTest / 1.0E09;

        appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
        results+="\nThis test took a total of "+timeOfTest+" secs \n";
        results+=resultsSummaryTemplate(testValidation,currentVersion,gitHubWikiName,failsCount,issuesList,issuesListHtml,totalTestsCount);
        return results;
    }

    /**
     * Simulates age down key press and verifies it works as expected
     * @return text log of test results
     * @throws Exception
     */
 public String ageDownKey() throws Exception{
        //Redmine Support #1171
        //Testing Incline Up key press
        //1. Initialize Incline to min
        //2. Simulate Incline Up key press
        //3. Validate that Incline went up 0.5%
        //4. Repeat steps 2-3 until max incline reached

        String results ="";
        gitHubWikiName = "Age-Down-Key-Test";
        testValidation ="PASSED";
        issuesListHtml = "";
        issuesList = "";
        failsCount=0;
        totalTestsCount = 0;
        mAct.filename = "Age Down Key.txt";
        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();
        double Age1 = 0;
        double Age2 = 0;
        double currentAge = 0;
        long elapsedTime = 0;
        double seconds = 0;
        long startime = 0;
        long time = 500;


        results += "\n\n------------------AGE DOWN KEY TEST---------------\n\n";
        results+= Calendar.getInstance().getTime() + "\n\n";
        appendMessage("<br><br>------------------AGE DOWN KEY TEST------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        currentAge = hCmd.getAge();
        appendMessage("Current age is set to: "+currentAge+"<br>");
        results+="Current age is set to: "+currentAge+"\n";

        if(currentAge!=95)
        {
            results += "setting the age to 95...\n";
            appendMessage("setting the mode 95...<br>");

            //set the age to 95
            do {
                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.AGE, 95);
                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                Thread.sleep(time);
                results += "Status of setting age to 95 years old: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
                appendMessage("Status of setting age to 95 years old: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
            }while (true);
        }

        results += "sending the start key command...\n";
        appendMessage("sending the start key command...<br>");
        Device keyPressTemp = this.MainDevice.getSubDevice(DeviceId.KEY_PRESS);

        if(keyPressTemp != null){
            Command writeKeyPressCmd = keyPressTemp.getCommand(CommandId.SET_TESTING_KEY);
            if(writeKeyPressCmd != null){
                sendKeyCmd = new FecpCommand(writeKeyPressCmd, hCmd);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyCode(KeyCodes.AGE_DOWN);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyOverride(true);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setTimeHeld(1000);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setIsSingleClick(true);
            }
        }

     //Decrement age by 1 and verify the decrement happened. Repeat until min valid age reached
        for (int i=95; i>=18; i--)// Go through all valid ages
        {
            Age1 = hCmd.getAge();
            do{
                mSFitSysCntrl.getFitProCntrl().addCmd(sendKeyCmd);
                Thread.sleep(time);
                results += "Status of sending Age down key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
                appendMessage("Status of sending Age down key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
            }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again
            Age2 = hCmd.getAge();

        /* This part might be optional. Depends on wheter a command already added exception is thrown or not
         mSFitSysCntrl.getFitProCntrl().removeCmd(sendKeyCmd);
         Thread.sleep(1000); */
            totalTestsCount++;
            if((Age2 - Age1) == -1)
            {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("Previous Age: "+Age1+" current age: "+Age2+". Age correclty decremented by "+(Age2-Age1)+" %<br>");

                results+="\n* PASS *\n\n";
                results+="Previous age: "+Age1+" current age: "+Age2+". Age correclty decremented by "+(Age2-Age1)+" %\n";
            }
            else
            {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("Previous age: "+Age1+" current age: "+Age2+". age decrement was "+(Age2-Age1)+" % and should have been 1 %<br>");

                results+="\n* FAIL *\n\n";
                results+="Previous age: "+Age1+" current age: "+Age2+". age decrement was "+(Age2-Age1)+" % and should have been 1 %<br>";
                issuesListHtml+="<br>- Previous age: "+Age1+" current age: "+Age2+". age decrement was "+(Age2-Age1)+" % and should have been -1 %<br>";
                issuesList+="\n- Previous age: "+Age1+" current age: "+Age2+". age decrement was "+(Age2-Age1)+" % and should have been -1 %\n";
                failsCount++;
                testValidation = "FAILED";
            }
        }

     timeOfTest = System.nanoTime() - startTestTimer;
     timeOfTest = timeOfTest / 1.0E09;

     appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
     results+="\nThis test took a total of "+timeOfTest+" secs \n";
     results+=resultsSummaryTemplate(testValidation,currentVersion,gitHubWikiName,failsCount,issuesList,issuesListHtml,totalTestsCount);
     return results;
    }

    /**
     * Runs all Treadmill Key-codes tests
     * @return text log of test results
     * @throws Exception
     */
    @Override
    public String runAll() throws Exception {
        //Redmine Support #925

        String keysResults="";

        keysResults += stopKey();
        keysResults += startKey();
        keysResults += inclineUpKey();
        keysResults += inclineDownKey();
        keysResults += quickInclineKeys();
        keysResults += speedUpKey();
        keysResults += speedDownKey();
        keysResults += quickSpeedKeys();
        keysResults += ageUpKey();
        keysResults += ageDownKey();
        mAct.filename = "All Key Tests.txt";
        return keysResults;
    }
}
