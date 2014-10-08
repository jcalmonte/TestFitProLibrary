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
import com.ifit.sparky.fecp.interpreter.status.StatusId;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Calendar;


/**
 * Created by jc.almonte on 6/25/14.
 * Class for motor type tests
 * Used sean's TestMotor code and adapted it to work with SparkyAndroidLib 0.0.9
 */
public class TestMotor extends CommonFeatures {

    private String testValidation = "", currentVersion="", gitHubWikiName="", issuesListHtml="", issuesList="";
    private int failsCount = 0, totalTestsCount = 0;

    private double currentSpeed; // Current motor speed
    private String emailAddress;

    //To hold time lapsed
    private long stopTimer = 0;
    private long startTimer = 0;


    //TestMotor constructor. Receive needed parameters from main activity(TestApp) to initialize controller
    public TestMotor(FecpController fecpController, BaseTest act, SFitSysCntrl ctrl) {
        //Get controller sent from the main activity (TestApp)
        try {
            this.mFecpController = fecpController;
            this.mAct = act;
            this.mSFitSysCntrl = ctrl;
            this.emailAddress = "jc.almonte@iconfitness.com";
            this.hCmd = new HandleCmd(this.mAct);// Init handlers

            ByteBuffer secretKey = ByteBuffer.allocate(32);
            for (int i = 0; i < 32; i++) {
                secretKey.put((byte) i);
            }
            try {
                //unlock the system
                this.mSFitSysCntrl.getFitProCntrl().unlockSystem(secretKey);
                Thread.sleep(1000);
                //Get current system device
                MainDevice = this.mFecpController.getSysDev();
                this.currentVersion = "SAL v"+ String.valueOf(mFecpController.getVersion());
                this.wrCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), hCmd);
                this.rdCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), hCmd, 0, 100);
               ((WriteReadDataCmd) rdCmd.getCommand()).addReadBitField(BitFieldId.ACTUAL_KPH);
                ((WriteReadDataCmd) rdCmd.getCommand()).addReadBitField(BitFieldId.MAX_KPH);
                ((WriteReadDataCmd) rdCmd.getCommand()).addReadBitField(BitFieldId.GRADE);
                ((WriteReadDataCmd) rdCmd.getCommand()).addReadBitField(BitFieldId.ACTUAL_INCLINE);
                ((WriteReadDataCmd) rdCmd.getCommand()).addReadBitField(BitFieldId.DISTANCE);
                ((WriteReadDataCmd) rdCmd.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
                ((WriteReadDataCmd) rdCmd.getCommand()).addReadBitField(BitFieldId.WEIGHT);
                ((WriteReadDataCmd) rdCmd.getCommand()).addReadBitField(BitFieldId.KPH);
                ((WriteReadDataCmd) rdCmd.getCommand()).addReadBitField(BitFieldId.CALORIES);
                mSFitSysCntrl.getFitProCntrl().addCmd(rdCmd);
                Thread.sleep(1000);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //--------------------------------------------//
    //                                            //
    //             Testing Start Speed            //
    //                                            //
    //--------------------------------------------//
    //The startSpeed is planned to automate #16 of the software
    //checklist to make sure that the machine starts at 1.0mph or 2.0kph

    /**
     * Makes sure machine starts at a speed 1.0mph/2.0kph (software checklist #16)
     * @return text log of test results
     * @throws Exception
     */

    public String startSpeed() throws Exception {

//        //outline for code support #958 **first task to automate**
//        //send basic start command to start motor at on position
//        //request actual speed from device to make sure it is connected and moving
//        //read speed received into this code which should be target speed
//        //check against constant variable of 1.0 mph
//        //make sure formatting is right for verification for english or metric units

        String results ="";
        gitHubWikiName = "Start-Speed";
        testValidation ="PASSED";
        issuesListHtml = "";
        issuesList = "";
        failsCount=0;
        totalTestsCount = 0;
        mAct.filename = "Start Speed.txt";
        double actualspeed = 0;
        double startSpeed = 0;
        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();
        long time = 500;

        //If metric start speed = 2.0 kph
        if(MainDevice.getSysDevInfo().isDefaultUnitMetric())
        {
            startSpeed = 2.0;
        }
        else // if english, start speed = 1.61 kph
        {
            startSpeed = 1.61;
        }
        appendMessage("<br><br>----------------------START SPEED TEST RESULTS----------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        results+="\n\n----------------------START SPEED TEST RESULTS----------------------\n\n";
        results+=Calendar.getInstance().getTime() + "\n\n";

        //print out the current mode
        appendMessage("The current mode is " + hCmd.getMode() + "<br>");

        results+="The current mode is " + hCmd.getMode() + "\n";


        currentSpeed = hCmd.getSpeed();
        appendMessage("The current speed is: " + currentSpeed + "<br>");

        results+="The current speed is: " + currentSpeed + "\n";

        appendMessage("Now setting mode to RUNNING...<br>");

        results+="Now setting mode to RUNNING...\n";

        //Set Mode to Running
        do{
            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
            results+="Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

        appendMessage("<br>The current mode is " + hCmd.getMode() + "<br>");
        results+="\nThe current mode is " + hCmd.getMode() + "\n";

        actualspeed = hCmd.getActualSpeed();
        appendMessage("The actual speed is: " + actualspeed + "<br>");
        appendMessage("Wait 5 secs.. for motor to reach speed<br>");

        results+="The actual speed is: " + actualspeed + "\n";
        results+="Wait 5 secs.. for motor to reach speed\n";
        Thread.sleep(5000); // Give the motor 5 secs to reach the desired speed
// TODO: Uncomment this part once Actual Speed is accurate
// double actualSpeed = 0;
//        while(true)
//        {
//            currentSpeed = hCmd.getSpeed();
//            actualSpeed = hCmd.getActualSpeed();
//            appendMessage("Current Speed is "+currentSpeed+" actual speed is "+actualSpeed+" Max speed is "+ hCmd.getMaxSpeed()+"<br>");
//            appendMessage("Wait 5 secs... and clear screen<br>");
//            Thread.sleep(5000);
//            res = "";
//            Thread.sleep(2000);
//            if(hCmd.getIncline() == 0)
//                break;
//
//        }

        currentSpeed = hCmd.getSpeed();
        actualspeed = hCmd.getActualSpeed();


        appendMessage("The current speed after 5 seconds running is: " + currentSpeed + "<br>");
        appendMessage("The ACTUAL speed after 5 seconds running is: " + actualspeed + "<br>");

        results+="The current speed after 5 seconds running is: " + currentSpeed + "\n";
        results+="The ACTUAL speed after 5 seconds running is: " + actualspeed + "\n";
        totalTestsCount++;
        if (currentSpeed == startSpeed) {
            appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
            appendMessage("Speed correctly started at "+startSpeed+" kph<br>");

            results+="\n\n* PASS *\n\n";
            results+="Speed correctly started at 2.0 kph\n";
        } else {
            appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
            appendMessage("Speed should have started at "+startSpeed+" kph but is actually at " + currentSpeed + " kph<br>");

            results+="\n\n* FAIL *\n\n";
            results+="Speed should have started at "+startSpeed+" kph but is actually at " + currentSpeed + " kph\n";
            issuesListHtml+="<br>- "+"Speed should have started at "+startSpeed+" kph but is actually at " + currentSpeed + " kph<br>";
            issuesList+="\n- Speed should have started at "+startSpeed+" kph but is actually at " + currentSpeed + " kph\n";
            failsCount++;
            testValidation = "FAILED";

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

        appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
        results+="\nThis test took a total of "+timeOfTest+" secs \n";

        appendMessage("<br>This test took a total of"+timeOfTest+"secs <br>");
        results+=resultsSummaryTemplate(testValidation,currentVersion,gitHubWikiName,failsCount,issuesList,issuesListHtml,totalTestsCount);
        return results;

    }

    /**
     * Runs a workout and verifies the distance recorded is accurate
     * @return text log of test results
     * @throws Exception
     */

    public String distance() throws Exception {

        //outline for code support #929 in redmine
        //start timer stopwatch
        //send a speed of 10 kph for a 1.5 min/km pace
        //wait 1.5 minutes
        //read distance value
        //verify distance is 250 meters

        String results="";
        gitHubWikiName = "Distance";
        testValidation ="PASSED";
        issuesListHtml = "";
        issuesList = "";
        failsCount=0;
        totalTestsCount = 0;
        mAct.filename = "Distance.txt";

        double distance = 0;//resulting distance
        double [] setSpeed={10,5};// speeds to use in the test in KPH
        double expectedDistance;// calcualted expected distance
        double totalDistance = 0; // initialize it with Total distance bitfield here
        double expectedTotalDistance = 0; // total distance expected after running the workout
        BigDecimal expectedDistanceRounded;
        BigDecimal resultDistanceRounded;
        long [] runTime ={90,30}; // runTime to run test in seconds
        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();
        long time = 500;

        appendMessage("<br>----------------------------DISTANCE TEST---------------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        results+="\n----------------------------DISTANCE TEST---------------------------\n\n";
        results+=Calendar.getInstance().getTime() + "\n\n";

        for(int i = 0; i<setSpeed.length;i++)
        {

       // there is 1 added second because of code ops delays
        expectedDistance = setSpeed[i]*0.277778*(runTime[i]+1); //D = S * T --> S in m/s, and T in seconds
        expectedDistanceRounded = new BigDecimal(expectedDistance);
        expectedDistanceRounded = expectedDistanceRounded.setScale(0, BigDecimal.ROUND_UP);

        appendMessage("Test runs for " +runTime[i]+ " seconds at a speed of "+setSpeed[i]+" KPH. Expected distance is: "+expectedDistanceRounded+" meters<br><br>");
        results+="Test runs for " +runTime[i]+ " seconds at a speed of "+setSpeed[i]+" KPH. Expected distance is: "+expectedDistance+" meters\n\n";

        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");
        results+="Current Mode is: " + hCmd.getMode() + "\n";

        //set mode to running
            do{
                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                Thread.sleep(time);
                appendMessage("Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
                results+="Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
            }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");
        results+="Current Mode is: " + hCmd.getMode() + "\n";

        //Set the motor speed to 10 KPH
           do{
                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, setSpeed[i]);
                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                Thread.sleep(time);
               appendMessage("The status of setting speed to "+setSpeed[i]+" is: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
               results+="The status of setting speed to "+setSpeed[i]+" is: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
           }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");
        results+="Current Mode is: " + hCmd.getMode() + "\n";

        appendMessage("Now wait " + runTime[i] + " seconds...<br>");
        results+="Now wait "+runTime[i]+" seconds...\n";

        //wait runTime seconds
        Thread.sleep(runTime[i]*1000);

            do{
                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                Thread.sleep(time);
                appendMessage("Status of setting mode tu PAUSE: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
                results+="Status of setting mode tu PAUSE: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
            }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");
        results+="Current Mode is: " + hCmd.getMode() + "\n";

        distance = hCmd.getDistance();
        resultDistanceRounded = new BigDecimal(expectedDistance);
        resultDistanceRounded = resultDistanceRounded.setScale(0, BigDecimal.ROUND_UP);

        expectedTotalDistance = totalDistance + distance;
        //totalDistance = hCmd.getTotalDistance();    TODO: Uncomment this line once total distance bitfield is added


        appendMessage("The distance is " + distance + "<br>");
        results+="The distance is " + distance + "\n";
        appendMessage("Total distance is " + totalDistance + "<br>");
        results+="Total distance is " + totalDistance + "\n";

            do{
                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                Thread.sleep(time);
                appendMessage("Status of setting mode to RESULTS: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
                results+="Status of setting mode to RESULTS: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
            }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

            do{
                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                Thread.sleep(time);
                appendMessage("Status of setting mode to IDLE: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
                results+="Status of setting mode to IDLE: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
            }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

         totalTestsCount++;
        if (Math.abs(expectedDistance-distance) > 3) {
            appendMessage("<br><font color = #ff0000>* FAIL *</font><br>");
            appendMessage("<br> Expected distance: "+expectedDistanceRounded + " read distance: "+distance+", The distance was off by " + (expectedDistance-distance) + "meters <br><br>");

            results+="\n* FAIL *\n\nThe distance was off by " + (expectedDistance-distance) + "meters \n\n";
            results+="\n Expected distance: "+expectedDistanceRounded + " read distance: "+distance+", The distance was off by " + (expectedDistance-distance) + "meters \n";
            issuesListHtml+="<br>- Expected distance: "+expectedDistanceRounded + " read distance: "+distance+", The distance was off by " + (expectedDistance-distance) + "meters <br>";
            issuesList+="\n- Expected distance: "+expectedDistanceRounded + " read distance: "+distance+", The distance was off by " + (expectedDistance-distance) + "meters\n";
            failsCount++;
            testValidation = "FAILED";
        } else {
            appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>The distance should be " + expectedDistanceRounded + "  meters and is " + distance + " meters which is within +/- 3 tolerance<br><br>");

            results+="\n* PASS *\n\nThe distance should be "+expectedDistanceRounded+"  meters and is " + distance + " meters which is within +/- 3 tolerance\n\n";

        }
            appendMessage("<br>----------------------------TOTAL DISTANCE---------------------------<br><br>");

            results+="\n----------------------------TOTAL DISTANCE---------------------------\n\n";

            if (Math.abs(expectedTotalDistance-totalDistance) > 3) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br>");
                results+="\n* FAIL *\n\n";

                appendMessage("<br> Expected total distance: "+expectedTotalDistance + " read total distance: "+totalDistance+", total distance was off by " + (expectedTotalDistance-totalDistance) + "meters <br><br>");
                results+="\n Expected total distance: "+expectedTotalDistance + " read total distance: "+totalDistance+", total distance was off by " + (expectedTotalDistance-totalDistance) + "meters \n\n";

                issuesListHtml+="<br>- Expected total distance: "+expectedTotalDistance + " read total distance: "+totalDistance+", total distance was off by " + (expectedTotalDistance-totalDistance) + "meters <br>";
                issuesList+="\n- Expected total distance: "+expectedTotalDistance + " read total distance: "+totalDistance+", total distance was off by " + (expectedTotalDistance-totalDistance) + "meters \n";
                failsCount++;
                testValidation = "FAILED";
            } else {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>The distance should be " + expectedTotalDistance + "  meters and is " + totalDistance + " meters which is within +/- 3 tolerance<br><br>");

                results+="\n* PASS *\n\nThe distance should be "+expectedTotalDistance+"  meters and is " + totalDistance + " meters which is within +/- 3 tolerance\n\n";
            }

        }
        //Remove all commands from the device that have a command ID = "WRITE_READ_DATA"
        mSFitSysCntrl.getFitProCntrl().removeCmd(wrCmd);
        Thread.sleep(1000);

        timeOfTest = System.nanoTime() - startTestTimer;
        timeOfTest = timeOfTest / 1.0E09;

        appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
        results+="\nThis test took a total of "+timeOfTest+" secs \n";
        results+=resultsSummaryTemplate(testValidation,currentVersion,gitHubWikiName,failsCount,issuesList,issuesListHtml,totalTestsCount);
        return results;
    }


    /**
     * Makes sure machine starts at a speed 1.0mph/2.0kph after a pause (redmine support #954)
     * @return text log of test results
     * @throws Exception
     */


     public String pauseResume() throws Exception {

        //Support #954 in Redmine
        //Turn mode to Running (mimics Start button press)
        //Set speed to 5 kph
        //set mode to Pause
        //Set mode to Running
        //Verify actual speed is 1.0 mph/2.0 kph (as of 3/12/14, the resume speed is 1.0 kph)
        String results="";
         gitHubWikiName = "Pause-Resume-Speed";
         testValidation ="PASSED";
         issuesListHtml = "";
         issuesList = "";
         failsCount=0;
         totalTestsCount = 0;
         mAct.filename = "Pause Resume Speed.txt";
        double startSpeed = 0;
        double actualspeed = 0;
        double setSpeed = 5;//speed to be used in this test
        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();
        long time = 500;
         //If metric start speed = 2.0 kph
         if(MainDevice.getSysDevInfo().isDefaultUnitMetric())
         {
             startSpeed = 2.0;
         }
         else // if english, start speed = 1.61 kph
         {
             startSpeed = 1.61;
         }
         appendMessage("<br><br>----------------------PAUSE/RESUME SPEED TEST RESULTS----------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        results+="\n\n----------------------PAUSE/RESUME SPEED TEST RESULTS----------------------\n\n";
        results+=Calendar.getInstance().getTime() + "\n\n";

        //Set Mode to Running
         do{
             ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
             Thread.sleep(time);
             appendMessage("Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
             results+="Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
         }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

        currentSpeed = hCmd.getSpeed(); //read current speed
        actualspeed = hCmd.getActualSpeed();

        appendMessage("The current mode is " + hCmd.getMode() + "<br>");
        results+="The current mode is " + hCmd.getMode() + "\n";

        appendMessage("Before setting speed to 5kph speed is currently set at: " + currentSpeed + " kph and the actual speed is " + actualspeed + "kph <br>");
        results+="Before setting speed to 5kph speed is currently set at: " + currentSpeed + " kph and the actual speed is " + actualspeed + "kph \n";

        do{
            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, setSpeed);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("Status of changing speed to 5 KPH: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
            results+="Status of changing speed to 5 KPH: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
         }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

        appendMessage("Wait 10 secs for motor to reach speed...<br>");
        results+="Wait 10 secs for motor to reach speed...\n";
       
        Thread.sleep(10000);// Give it time to motor to reach speed of 5 KPH

        //Set command to read speed and verify its at 5 kph

        currentSpeed = hCmd.getSpeed(); //read current speed
        actualspeed = hCmd.getActualSpeed();
        appendMessage("The speed is currently set at: " + currentSpeed + " kph and the actual speed is " + actualspeed + "kph (After 10 secs)<br>");
        results+="The speed is currently set at: " + currentSpeed + " kph and the actual speed is " + actualspeed + "kph (After 10 secs)\n";

         do{
             ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
             Thread.sleep(time);
             appendMessage("Status of setting mode tu PAUSE: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
             results+="Status of setting mode tu PAUSE: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
         }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

        appendMessage("give 5 secs for motor to speed down...<br>");
        results+="give 5 secs for motor to speed down...\n";
        Thread.sleep(5000); // Give time for motor to stop seconds

        //print out the current mode
        appendMessage("The current mode is " + hCmd.getMode() + "<br>");
        results+="The current mode is " + hCmd.getMode() + "\n";

        currentSpeed = hCmd.getSpeed(); //read current speed
        actualspeed = hCmd.getActualSpeed();
        appendMessage("The speed during PAUSE is: " + currentSpeed + " kph and the actual speed is " + actualspeed + " kph<br>");
        results+="The speed during PAUSE is: " + currentSpeed + " kph and the actual speed is " + actualspeed + " kph\n";

        //Set Mode to Running
         do{
             ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
             Thread.sleep(time);
             appendMessage("Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
             results+="Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
         }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

        appendMessage("wait 5 secs...<br>");
        results+="wait 5 secs...\n";
        
        Thread.sleep(5000); // Run for 5 seconds

        //print out the current mode
        appendMessage("The current mode is " + hCmd.getMode() + "<br>");
        results+="The current mode is " + hCmd.getMode() + "\n";

        currentSpeed = hCmd.getSpeed();// Read speed again
        totalTestsCount++;
         if (currentSpeed == startSpeed) {
             appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
             appendMessage("Speed correctly started at "+startSpeed+" kph<br>");

             results+="\n\n* PASS *\n\n";
             results+="Speed correctly started at "+startSpeed+" kph<br>";
         } else {
             appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
             appendMessage("Speed should have started at "+startSpeed+" kph but is actually at " + currentSpeed + " kph<br>");

             results+="\n\n* FAIL *\n\n";
             results+="Speed should have started at "+startSpeed+" kph but is actually at " + currentSpeed + " kph\n";
             issuesListHtml+="<br>- "+"Speed should have started at "+startSpeed+" kph but is actually at " + currentSpeed + " kph<br>";
             issuesList+="\n- Speed should have started at "+startSpeed+" kph but is actually at " + currentSpeed + " kph\n";
             failsCount++;
             testValidation = "FAILED";

         }

        //Set Mode to Pause
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

        appendMessage("<br>The current mode is " + hCmd.getMode() + "<br>");
        results+="\nThe current mode is " + hCmd.getMode() + "\n";

        mSFitSysCntrl.getFitProCntrl().removeCmd(wrCmd);
        Thread.sleep(1000);
         timeOfTest = System.nanoTime() - startTestTimer;
         timeOfTest = timeOfTest / 1.0E09;

         appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
         results+="\nThis test took a total of "+timeOfTest+" secs \n";
         results+=resultsSummaryTemplate(testValidation,currentVersion,gitHubWikiName,failsCount,issuesList,issuesListHtml,totalTestsCount);
         return results;
    }

    /**
     * Verify all speed values can be set properly and speed changes accordingly
     * @return text log of test results
     * @throws Exception
     */
    public String speedController() throws Exception {
        final double MAX_SPEED = hCmd.getMaxSpeed();
        //outline for code support #927 in redmine
        //run test for treadmill & incline trainers
        //send speed command
        //validate speed was sent
        //read speed (not actual speed)
        //validate speed response is what you sent originally
        //go through entire speed range 1-15mph, for example
        String results="";
        gitHubWikiName = "Speed-Controller";
        testValidation ="PASSED";
        issuesListHtml = "";
        issuesList = "";
        failsCount=0;
        totalTestsCount = 0;
        mAct.filename = "Speed Controller.txt";

        String currentWorkoutMode;
        String currentMode;
        final int NUM_TESTS = 1;
        double roundedJ;
        long elapsedTime = 0;
        double seconds = 0;
        long startime = 0;
        double actualSpeed = 0;
        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();
        double currentActualIncline;
        long time = 500;
        appendMessage("<br>--------------------------SPEED TEST RESULTS--------------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        results+="\n--------------------------SPEED TEST RESULTS--------------------------\n\n";
        results+=Calendar.getInstance().getTime() + "\n\n";

        currentMode = "Current Mode is: " + hCmd.getMode() + "<br>";
        appendMessage(currentMode);

        results+="Current Mode is: " + hCmd.getMode() + "\n";

        do{
            //Set Incline to zero to avoid speed/incline limits
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, 0);
            mFecpController.addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("Status of setting incline to zero: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
            results+="Status of setting incline to zero: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

        results+="Checking incline will reach set value...\n";
        appendMessage("Checking incline will reach set value...<br>");

        //check actual incline until value reaches minIncline
        startime = System.nanoTime();
        do
        {
            currentActualIncline = hCmd.getActualIncline();
            Thread.sleep(300);
            appendMessage("Current Incline is: " + currentActualIncline+ " goal: 0 time elapsed: "+seconds+"<br>");

            results+="Current Incline is: " + currentActualIncline+ " goal: 0 time elapsed: "+seconds+"\n";

            elapsedTime = System.nanoTime() - startime;
            seconds = elapsedTime / 1.0E09;
        } while(0!=currentActualIncline && seconds < 90);//Do while the incline hasn't reached its point yet or took more than 1.5 mins


        do{
            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
            results+="Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");
        results+="Current Mode is: " + hCmd.getMode() + "\n";


        //TODO: read the min speed from the Brainboard (not implemented yet)

        //TODO: read the max speed from the Brainboard (not implemented yet)

        //Set NUM_TESTS to the number of times you want to run the test
            for (double j = MAX_SPEED; j >= 0.5; j -= 0.1)      //Set MAX_SPEED TO the maximum speed
            {
                roundedJ = ((double) Math.round(j * 10) / 10);
                appendMessage("Sending a command for speed " + roundedJ + " to the FecpController<br>");
                
                results+="Sending a command for speed " + roundedJ + " to the FecpController\n";


                //Set value for the speed
               do{
                    ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, roundedJ);
                    mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                    Thread.sleep(time);
                    appendMessage("Status of sending speed " + roundedJ + ": " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
                    results+="Status of sending speed " + roundedJ + ": " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
                }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again


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
             // TODO: THIS CODE SNIPPET WILL BE DELETED ONCE ACTUAL SPEED IS ACCURATE
                if (roundedJ == 0) {
                    Thread.sleep(3000);
                } else if (roundedJ < MAX_SPEED && roundedJ > 0) {
                    Thread.sleep(2000);
                } else if (roundedJ == MAX_SPEED) {
                    Thread.sleep((long) (j) * 1000 * 2); //Typecast j (speed) to a long to delay time for double the time based on the speed
                    //Ex: delay for 12 kph should be 24 seconds (24000 milliseconds)
                }
            //END OF CODE SNIPPET TO BE DELETED

                //Check status of the command to send the speed
                appendMessage("current mode of speed " + roundedJ + " is " + hCmd.getMode() + "<br>");
                results+="Workout mode of speed " + roundedJ + " is " + hCmd.getMode() + "\n";

                currentSpeed = hCmd.getSpeed();
                appendMessage("Current speed is: " + currentSpeed + " and actual speed is " + hCmd.getActualSpeed() + "<br>");
                results+="Current speed is: " + currentSpeed + " and actual speed is " + hCmd.getActualSpeed() + "\n";

                appendMessage("<br>For Speed " + roundedJ + ":<br>");

                results+="\nFor Speed " + roundedJ + ":\n";
                totalTestsCount++;
                //with double values the rounding does not always work with 0.1 values
                if ((Math.abs(roundedJ - currentSpeed) < 0.2)) {
                    appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>The speed was minimally off by " + (roundedJ - currentSpeed) + "<br><br>");
                   
                    results+="\n* PASS *\n\nThe speed was minimally off by " + (roundedJ - currentSpeed) + "\n\n";

                } else {
                    appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>The speed is greatly off by " + (roundedJ - currentSpeed) + "<br><br>");
                    results+="\n* FAIL *\n\nThe speed is greatly off by " + (roundedJ - currentSpeed) + "\n\n";
                    issuesListHtml+="<br>- The speed is greatly off by " + (roundedJ - currentSpeed) + "<br>";
                    issuesList+="\n- The speed is greatly off by " + (roundedJ - currentSpeed) + "\n";
                    failsCount++;
                    testValidation = "FAILED";

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

        appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
        results+="\nThis test took a total of "+timeOfTest+" secs \n";
        return results;
    }

    /**
     * Runs trough a series of workouts with different weight,incline and speeds
     * and verifies calories values are as expected
     * @return text log of test results
     * @throws Exception
     */

    public String calories() throws Exception {
        /*
        * Calories Formula
        *
        *   Parameters:
        *       M = Mass (kg)
        *       S = Speed (m/s)
        *       G = Percent grade (m/m)
        *       T = time spent at that pace and grade (sec)
        *       VO2 = Volume of oxygeb consumed (ml/kg/sec)
        *       n1 = Constant
        *       n2 = Constant
        *                                               | 0.1, S < 1.8 m/s              | 1.8, S < 1.8 m/s
        *   VO2 = n1*S + n2*S*G + 0.583333   where n1 = |                      &   n2 = |
        *                                               | 0.2, S >= 1.8 m/s             | 0.9, S >= 1.8 m/s
        *
        *   kcal burned = (VO2 * M/1000) * 5T
        *
        *   kcal burned = ( (n1*S + n2*S*G + 0.583333) * M/1000) * 5T
        *
        * */


        String results="";
        gitHubWikiName = "Calorie";
        testValidation ="PASSED";
        issuesListHtml = "";
        issuesList = "";
        failsCount=0;
        totalTestsCount = 0;
        mAct.filename = "Calorie.txt";
        //Parameters for the calories tests
        long runTime = 60;           //Time to be used for the test (in secs)
        double rCalories; //Calories read as result of the test
        BigDecimal  resultCalories; // rCalories formatted to 2 digits
        double caloriesPersec = 0;
        double  eCalories;  // Calories expected for the test (calculated with formula)
        BigDecimal expectedCalories;

        double [] incline ={0,5,10,15};      // Incline to be used for the test (in % grade)
                                                      //0,5,10,15 % grade respectively

        double [] weight= {52.16,83.91,181.44}; //weight to be used for the test (kgs)
                                                //115,185 and 400 kgs respectively

        double [] speed ={1.6,3.21,4.82,6.43,8.04,9.65};   // Speeds to be used for the test (in kph) ---> 1 kph = 0.28 m/s
                                                              //0.45,0.90,1.35,1.80,2.25,2.70 m/s respectively or 1,2,3,4,5,6 mph respectively
        double speedMPH = 0; // To convert to mph
        double currentSpeed = 0;
        double currentWeight = 0;
        double currentWeightLbs = 0;
        BigDecimal currentWeightLbsRounded;
        double currentActualIncline = 0;

        double n1,n2; // Constants
        n1=n2=0;
        int wLen = weight.length;
        int iLen = incline.length;
        int sLen = speed.length;
        double [] expectedCalsArray = new double[wLen*iLen*sLen];
        double [] resultsCalsArray = new double[wLen*iLen*sLen];
        int calsIndex = 0; // Index for expectedCals results
        long elapsedTime = 0;
        double seconds = 0;
        long startime = 0;
        long time = 500;


        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();


        System.out.println("NOW RUNNING CALORIES TEST<br>");
        appendMessage("<br><br>----------------------------CALORIE TEST----------------------------<br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        results+="\n\n----------------------------CALORIE TEST----------------------------\n";
        results+=Calendar.getInstance().getTime() + "\n\n";

        // For each weight test every incline and for each incline test every speed

        for(int w = 0; w < wLen; w++)  // Weight Loop
        {
            currentWeight = hCmd.getWeight();
            currentWeightLbs = weight[w] * 2.20462; // convert weight from kgs to lbs
            currentWeightLbsRounded = new BigDecimal(currentWeightLbs);
            currentWeightLbsRounded = currentWeightLbsRounded.setScale(0,BigDecimal.ROUND_HALF_UP);

            appendMessage("<br><br>--------------WEIGHT = "+weight[w]+" kgs ("+currentWeightLbsRounded+" lbs)--------------<br>");
            results+="\n\n--------------WEIGHT = "+weight[w]+" kgs ("+currentWeightLbsRounded+" lbs)--------------\n";

            currentWeightLbs = currentWeight * 2.20462; // convert weight from kgs to lbs
            currentWeightLbsRounded = new BigDecimal(currentWeightLbs);
            currentWeightLbsRounded = currentWeightLbsRounded.setScale(0,BigDecimal.ROUND_HALF_UP);

            appendMessage("Weight is currently set to "+currentWeight+" kgs ("+currentWeightLbsRounded+" lbs)<br>");
            results+="Weight is currently set to "+currentWeight+" kgs ("+currentWeightLbsRounded+" lbs)\n";


            currentWeightLbs = weight[w] * 2.20462; // convert weight from kgs to lbs
            currentWeightLbsRounded = new BigDecimal(currentWeightLbs);
            currentWeightLbsRounded = currentWeightLbsRounded.setScale(0,BigDecimal.ROUND_HALF_UP);

            //if(currentWeight!= weight[w]) {
                appendMessage("Setting weight to "+weight[w]+" kgs ("+currentWeightLbsRounded+" lbs)<br>");
                results+="Setting weight to "+weight[w]+" kgs ("+currentWeightLbsRounded+" lbs)\n";
            do{
                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WEIGHT, weight[w]);
                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                Thread.sleep(time);
                appendMessage("The status of setting weight is: " + wrCmd.getCommand().getStatus().getStsId().getDescription()+"<br>");
                results+="The status of setting weight is: " + wrCmd.getCommand().getStatus().getStsId().getDescription()+"\n";
            }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again


                currentWeight = hCmd.getWeight();
                currentWeightLbs = currentWeight * 2.20462; // convert weight from kgs to lbs
                currentWeightLbsRounded = new BigDecimal(currentWeightLbs);
                currentWeightLbsRounded = currentWeightLbsRounded.setScale(0,BigDecimal.ROUND_HALF_UP);

                appendMessage("Weight is currently set to "+currentWeight+" kgs ("+currentWeightLbsRounded+" lbs)<br>");
                results+="Weight is currently set to "+currentWeight+" kgs ("+currentWeightLbsRounded+" lbs)\n";
          //  }

                    for(int i = 0; i < iLen; i++) //Incline loop
                    {
                        currentActualIncline = hCmd.getActualIncline();
                        appendMessage("Incline is currently set to "+currentActualIncline+" %<br>");
                        results+="Incline is currently set to "+currentActualIncline+" %\n";

                        if(currentActualIncline != incline[i]) {
                            appendMessage("Setting incline to " + incline[i] + " %<br>");
                            results += "Setting incline to " + incline[i] + " %\n";

                       do{
                            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, incline[i]);
                            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                            Thread.sleep(time);
                            appendMessage("The status of setting incline to: "+ incline[i]+ " is " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
                            results += "The status of setting incline is: "+ incline[i]+ " is " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
                        }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

                            appendMessage("Waiting for incline to reach set value...<br>");
                            results += "Waiting for incline to reach set value...\n";
                            startime = System.nanoTime();
                            do {
                                currentActualIncline = hCmd.getActualIncline();
                                Thread.sleep(350);
                                appendMessage("Current Incline is: " + currentActualIncline + " goal: " + incline[i] + " time elapsed: " + seconds + "<br>");
                                results += "Current Incline is: " + currentActualIncline + " goal: " + incline[i] + " time elapsed: " + seconds + "\n";
                                elapsedTime = System.nanoTime() - startime;
                                seconds = elapsedTime / 1.0E09;
                            }
                            while (incline[i] != currentActualIncline && seconds < 90);//Do while the incline hasn't reached its point yet or took more than 1.5 mins
                        }

                        appendMessage("<br><br>--------------INCLINE = "+incline[i]+" %--------------<br>");
                        results+="\n\n--------------INCLINE = "+incline[i]+" %--------------\n";

                        for(int s = 0; s < sLen; s++) // Speed Loop
                        {

                            // Console has to be in running mode to be able to set speed
                            do{
                                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
                                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                                Thread.sleep(time);
                                appendMessage("Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
                                results+="Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
                            }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

                            appendMessage("<br><br>--------------SPEED = "+speed[s]+" kph--------------<br>");
                            results+="\n\n--------------SPEED = "+speed[s]+" kph--------------\n";
                            currentSpeed = hCmd.getSpeed();
                            appendMessage("Speed is currently set to "+currentSpeed+" kph<br>");
                            results+="Speed is currently set to "+currentSpeed+" kph\n";

                            if(currentSpeed != speed[s]) {
                                appendMessage("Setting speed to "+speed[s]+" kph<br>");
                                results+="Setting speed to "+speed[s]+" kph\n";

                            do{
                                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, speed[s]);
                                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                                Thread.sleep(time);
                                appendMessage("The status of setting speed is: " + wrCmd.getCommand().getStatus().getStsId().getDescription()+"<br>");
                                results+="The status of setting speed is: " + wrCmd.getCommand().getStatus().getStsId().getDescription()+"\n";
                            }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

                                currentSpeed = hCmd.getSpeed();
                                appendMessage("Speed is currently set to "+currentSpeed+" kph<br>");
                                results+="Speed is currently set to "+currentSpeed+" kph\n";

                                speedMPH = currentSpeed*0.625; // In MPH

                                n1 = (speedMPH<4) ? 1:2;
                                n2 = (speedMPH<4) ? 18:9;

                                //                THIS PART WILL BE UNCOMMENTED ONCE ACTUAL SPEED IS ACCURATE
//                startime= System.nanoTime();
//                do
//                {
//                    actualSpeed = hCmd.getActualSpeed();
//                    Thread.sleep(350);
//                    appendMessage("Current Speed is: " + actualSpeed+ " kph goal: " + s+" kph time elapsed: "+seconds+"<br>");
//                    results+="Current Speed is: " + actualSpeed+ " kph goal: " + s+" kph time elapsed: "+seconds+"\n";
//                    elapsedTime = System.nanoTime() - startime;
//                    seconds = elapsedTime / 1.0E09;
//                } while(j!=actualSpeed && seconds < 20);//Do while the incline hasn't reached its point yet or took more than 20 secs

                                appendMessage("Now wait "+runTime+" secs...<br>");
                                results+="Now wait "+runTime+" secs...\n";
                                Thread.sleep(runTime*1000);
//                            appendMessage("Speed is currently set to "+hCmd.getSpeed()+" kgs<br>");
//                            results+="Speed is currently set to "+hCmd.getSpeed()+" kgs\n";
//  //set mode back to idle to stop the test
                                do{
                                    ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
                                    mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                                    Thread.sleep(time);
                                    appendMessage("Status of setting mode tu PAUSE: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
                                    results+="Status of setting mode tu PAUSE: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
                                }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

                                currentActualIncline = hCmd.getActualIncline();
                                currentWeight = hCmd.getWeight();
                                currentWeightLbs = currentWeight * 2.20462; // convert weight from kgs to lbs

                                caloriesPersec=(currentWeightLbs * (n1 * speedMPH * 270 + 350 + (currentActualIncline / 100 * speedMPH * 270 * n2))) / 2640000;
                                eCalories = caloriesPersec*runTime;
                                expectedCalories = new BigDecimal(eCalories);
                                expectedCalories = expectedCalories.setScale(2, BigDecimal.ROUND_FLOOR);

                                rCalories = hCmd.getCalories();
                                resultCalories = new BigDecimal(rCalories);
                                resultCalories = resultCalories.setScale(2, BigDecimal.ROUND_FLOOR);

                                //These values are stored for data comparison (just in case we want to do it)
                                expectedCalsArray[calsIndex] = eCalories;
                                resultsCalsArray[calsIndex] = rCalories;
                                calsIndex++;

                                totalTestsCount++;
                                if( Math.abs(eCalories-rCalories)< eCalories*0.05) // If calories are within 5% of expected, PASS
                                {
                                    appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                                    results+="\n* PASS *\n\n";

                                    appendMessage("Read calories value is: "+resultCalories+" which is within 5% tolerance of expected value "+expectedCalories+"<br>");
                                    appendMessage("Weight-->"+currentWeightLbsRounded+" lbs, speed-->"+currentSpeed+" kph, incline-->"+currentActualIncline+" %, time-->"+runTime+" secs<br>");
                                    results+="Read calories value is: "+resultCalories+" which is within 5% tolerance of expected value "+expectedCalories+"\n";
                                    results+="Weight-->"+currentWeightLbsRounded+" lbs, speed-->"+currentSpeed+" kph, incline-->"+currentActualIncline+" %, time-->"+runTime+" secs\n";
                                }
                                else
                                {
                                    appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>Calories value is:"+resultCalories+" and it should have been "+expectedCalories+" Calories were off by " + (eCalories - rCalories) + "<br><br>");
                                    results+="\n* FAIL *\n\nCalories value is:"+resultCalories+" and it should have been "+expectedCalories+" Calories were off by " + (eCalories - rCalories) + "\n\n";
                                    appendMessage("Weight-->"+currentWeightLbsRounded+" lbs, speed-->"+currentSpeed+" kph, incline-->"+currentActualIncline+" %, time-->"+runTime+" secs<br>");
                                    results+="Weight-->"+currentWeightLbsRounded+" lbs, speed-->"+currentSpeed+" kph, incline-->"+currentActualIncline+" %, time-->"+runTime+" secs\n";

                                    issuesListHtml+="<br>- Calories value is: "+resultCalories+" and it should have been "+expectedCalories+" Calories were off by " + (eCalories - rCalories)+ "<br>"
                                            +"Weight-->"+currentWeightLbsRounded+" lbs, speed-->"+currentSpeed+" kph, incline-->"+currentActualIncline+" %, time-->"+runTime+" secs<br>";
                                    issuesList+="\n- Calories value is: "+resultCalories+" and it should have been "+expectedCalories+" Calories were off by " + (eCalories - rCalories)+ "\n"
                                            +"Weight-->"+currentWeightLbsRounded+" lbs, speed-->"+currentSpeed+" kph, incline-->"+currentActualIncline+" %, time-->"+runTime+" secs\n";
                                    failsCount++;
                                    testValidation = "FAILED";
                                }

                                //To reset calories value, system must be stopped and set to IDLE

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

                            }
                    }

            }

        }



       // appendMessage(expectedCalories+" calories are expected at speed of "+setSpeed+ ", incline of "+incline+", time of "+time+" seconds and weight of "+weight+"<br><br>");
       // results+=expectedCalories+" calories are expected at speed of "+setSpeed+ ", incline of "+incline+", time of "+time+" seconds and weight of "+weight+"\n\n";
        results+=resultsSummaryTemplate(testValidation,currentVersion,gitHubWikiName,failsCount,issuesList,issuesListHtml,totalTestsCount);
        return results;
    }

    /**
     * Verifies speed on belt motor doesn't shoot up when motor has been running at a high speed,
     * stop is pressed and then start is pressed right away
     * @return text log of test results
     * @throws Exception
     */
    public String pwmOvershoot() throws Exception {

        //RedMine Support #956
        //Checklist item #39
        //Set Speed to Max Speed
        //Simulate Stop button press (Pause Mode)
        //Simulate Start button press (Running Mode)
        //Read Actual Speed to verify does not speed up
        //Set to running mode with max speed
        //Pause, then send a requesto to set max speed (while in pause mode)
        //It should ignore this request and continue to slow down til it stops
        String results="";
        gitHubWikiName = "PWM-Overshoot";
        testValidation ="PASSED";
        issuesListHtml = "";
        issuesList = "";
        failsCount=0;
        totalTestsCount = 0;
        mAct.filename = "PWM Overshoot.txt";
        final double MAX_SPEED = hCmd.getMaxSpeed();
        double[] actualSpeeds = new double[4];
        boolean alwaysLess = true;
        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();
        long time = 500;

        appendMessage("<br><br>----------------------PWM OVERSHOOT TEST RESULTS----------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        results+="\n\n----------------------PWM OVERSHOOT TEST RESULTS----------------------\n\n";
        results+=Calendar.getInstance().getTime() + "\n\n";

        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");

        results+="Current Mode is: " + hCmd.getMode() + "\n";

        //Set Mode to Running
        do{
            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
            results+="Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");
        results+="Current Mode is: " + hCmd.getMode() + "\n";

        appendMessage("About to set speed to MAX... Current speed is: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "<br>");
        results+="About to set speed to MAX.. Current speed is: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "\n";

        //Set speed to Max Speed
        do{
            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, MAX_SPEED);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("Status of changing speed to max " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
            results+="Status of changing speed to max " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

        appendMessage("current speed after setting it to max and before waiting 22 secs is: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "<br>");
        results+="current speed after setting it to max and before waiting 22 secs is: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "\n";

        appendMessage("now waiting 22 secs...<br> ");
        results+="now waiting 22 secs...\n";

        Thread.sleep(22000);    //Wait for Max Speed

        actualSpeeds[0] = hCmd.getActualSpeed(); // Speed here should be Max
        appendMessage("current speed after 22 secs and before going into pause mode is: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "<br>");
        results+="current speed after 22 secs and before going into pause mode is: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "\n";

        //Set Mode to PAUSE(Simulate Stop Key)
        do{
            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("Status of setting mode tu PAUSE: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
            results+="Status of setting mode tu PAUSE: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again


        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");
        results+="Current Mode is: " + hCmd.getMode() + "\n";

        Thread.sleep(5000);
        actualSpeeds[1] = hCmd.getActualSpeed(); // Speed here should be Max
        appendMessage("After aprox 5 secs in pause mode Current Speed is: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "<br>");
        results+="After aprox 5 secs in pause mode Current Speed is: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "\n";

        appendMessage("About to try to set speed to Max... This action SHOULD NOT CHANGE the speed!<br>");
        results+="About to try to set speed to Max... This action SHOULD NOT CHANGE the speed!\n";

        do{
            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, MAX_SPEED);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("Status of changing speed to max " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
            results+="Status of changing speed to max " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

        appendMessage("Command Status after trying to set speed to max: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
        appendMessage("Current Speed after attempting to set max speed during pause mode: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "<br>");

        Thread.sleep(4000);
        actualSpeeds[2] = hCmd.getActualSpeed(); // Speed here should be decreasing still

        results+="Command Status after trying to set speed to max: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
        results+="Current Speed after attempting to set max speed during pause mode: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "\n";


        //Set Mode to RUNNING (Simulate Start Key)
        do{
            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
            results+="Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
        }while(wrCmd.getCommand().getStatus().getStsId()== StatusId.FAILED); // If command failed, send it again

        Thread.sleep(4000);
        actualSpeeds[3] = hCmd.getActualSpeed(); // Speed here should be decreasing still

        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");
        results+="Current Mode is: " + hCmd.getMode() + "\n";

        appendMessage("Current Speed " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "<br>");
        results+="Current Speed " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "\n";


        //Check that the speed was always decreasing after setting mode to pause and then running
        for (int i = 0; i < actualSpeeds.length - 2; i++) {
            if (actualSpeeds[i] >= actualSpeeds[i + 1]) {
                alwaysLess = true;
            } else {
                alwaysLess = false;
                appendMessage("The current speed is more than the previous " + actualSpeeds[i + 1] + " with " + actualSpeeds[i] + "<br>");

                results+="The current speed is more than the previous " + actualSpeeds[i + 1] + " with " + actualSpeeds[i] + "\n";

                break;
            }
        }
        totalTestsCount++;
        if (alwaysLess) {
            appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
            appendMessage("The Speed did not increase once the Start button was pressed<br>");

            results+="\n* PASS *\n\n";
            results+="The Speed did not increase once the Start button was pressed\n";
        } else {
            appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
            appendMessage("The Speed increased or the Speed never changed from zero<br>");

            results+="\n* FAIL *\n\n";
            results+="The Speed increased or the Speed never changed from zero\n";
            issuesListHtml+="<br>- The Speed increased or the Speed never changed from zero<br>";
            issuesList+="\n- The Speed increased or the Speed never changed from zero\n";
            failsCount++;
            testValidation = "FAILED";
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

        appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
        results+="\nThis test took a total of"+timeOfTest+"secs \n";
        results+=resultsSummaryTemplate(testValidation,currentVersion,gitHubWikiName,failsCount,issuesList,issuesListHtml,totalTestsCount);
        return results;
    }

    /**
     * Runs all Motor tests
     * @return text log of test results
     * @throws Exception
     */
    @Override
    public String runAll() {
        String results="";
        try {
            results+=this.startSpeed();
            results+=this.pauseResume();
            results+=this.pwmOvershoot();
            results+=this.distance();
            results+=this.speedController();
            results+= this.calories();
            mAct.filename = "All Motor Tests.txt";

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return results;
    }

}
