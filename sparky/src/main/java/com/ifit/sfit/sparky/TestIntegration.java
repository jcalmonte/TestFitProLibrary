package com.ifit.sfit.sparky;

import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeId;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.device.Device;
import com.ifit.sparky.fecp.interpreter.status.StatusId;

import java.nio.ByteBuffer;
import java.util.Calendar;

/**
 * Created by jc.almonte on 7/2/14.
 */
public class TestIntegration {
    private FecpController mFecpController;
    private TestApp mAct;
    private HandleCmd hCmd;
    private SFitSysCntrl mSFitSysCntrl;
    private SystemDevice MainDevice;
    private  FecpCommand wrCmd;
    private  FecpCommand rdCmd;

    public TestIntegration(FecpController fecpController, TestApp act, SFitSysCntrl ctrl) {
        //Get controller sent from the main activity (TestApp)
        try {
            this.mFecpController = fecpController;
            this.mAct = act;
            this.mSFitSysCntrl = ctrl;
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
                this.wrCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);
                this.rdCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd,0,100);
                ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.MAX_GRADE);
                ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.MIN_GRADE);
                ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.ACTUAL_INCLINE);
                ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.GRADE);
               // ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.AGE);
                //((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.WEIGHT);
                ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
                ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.PAUSE_TIMEOUT);
                ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.IDLE_TIMEOUT);
                ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.RUNNING_TIME);

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
    //
    //Testing Age
    //
    //--------------------------------------------//
    /*
    TODO: Future test can include input invalid ages. Valid age range is 5-95 years, validate default age
    TODO: Also check that test still work when changing units from Metric to English\
    TODO: Is minimum age 18?
    */
    public String testAge() throws Exception {
        //Redmine Support #937
        //Read the default Age
        //Set the Age
        //Read the Age
        //Validate the Age
        //Repeat 6 times with values in increments of 5 years
        String ageResults;

        ageResults = "\n\n------------------------AGE TEST RESULTS------------------------\n\n";
        ageResults += Calendar.getInstance().getTime() + "\n\n";

        double age;
        double prevAge;


        age = hCmd.getAge();
        ageResults += "The default age is set to " + age + " years old\n";

        //Set age to min=18 and increment by 1 up to maxage= 95

        int failureCounter = 0;
        long elapsedTime = 0;
        int i;
        long startTime;
        for(i = 18; i <=95; i+=1) {
            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.AGE, i);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            age = hCmd.getAge();
            startTime = System.currentTimeMillis();
            //Keep reading the value until is the on you set it too or until it has try for long enough (25ms) that
            // we can conclude the reading has failed
            while(age!=i && elapsedTime < 25){
                age = hCmd.getAge();
                elapsedTime = System.currentTimeMillis() - startTime;
                }
            System.out.println(elapsedTime);

            ageResults += "Status of setting the Age to " + i + ": " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";

           /*
            if(age == 80)
            {
                long elapsedTime = System.currentTimeMillis() - startTime;
                System.out.println(elapsedTime);
            }
    */
            if(age == i){
//            if(age == 13){
                ageResults += "\n* PASS *\n\n";
                ageResults += "Current Age is set to: " + age + " years old (age should really be " + i + ")\n";
                failureCounter++;
            }
            else{
                ageResults += "\n* FAIL *\n\n";
                ageResults += "Current Age is set to: " + age + " years old, but should be set to: " + i + " years old\n";
            }
            mSFitSysCntrl.getFitProCntrl().removeCmd(wrCmd);
        }
        return ageResults;
    }
    //--------------------------------------------//
    //
    //Testing Weight
    //
    //--------------------------------------------//

    /*
    TODO: Future test can include testing invalid weights (MAX_WEIGHT< weight < MIN_WEIGHT) and validating default weight
    TODO: Also check for conversions when changing untis from Metric to English
    TODO: Put tolerance range to avoid rounding issues --- DONE!
    */
    public String testWeight() throws Exception {
        //Weight is implemented in kilograms with a default weight of 185 lbs =84 kg
        //Redmine Support #942
        //Read the default Weight
        //Set a Weight
        //Read the Weight
        //Validate the Weight
        //Repeat 6 times with different values
        String weightResults;

        weightResults = "\n\n------------------------WEIGHT TEST RESULTS------------------------\n\n";
        weightResults += Calendar.getInstance().getTime() + "\n\n";

        double weight;


        weight = hCmd.getWeight();
        weightResults += "The default weight is set to " + weight + " kilograms\n";
        double diff;

        //Set weight to 50 kg and increment by 10 up to 175 kg (max is 400lbs = 181 kg)
        for(double i = 45.35; i <=175; i+=10) {

            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WEIGHT, i);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            //need more time for weight controller
            Thread.sleep(1000);

            weightResults += "\nStatus of setting the Weight to " + i + ": " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";

            weight = hCmd.getWeight();
            diff = Math.abs(weight - i);

          if(diff < i*.01) // if values are within 1% of each other
          {
                weightResults += "\n* PASS *\n\n";
                weightResults += "Current Weight is set to: " + weight + " kilograms should be set to: " + i + " kilograms\n";
                weightResults+="set and read values have a difference of "+diff+" which is within 1% tolerance\n";
            }
            else{
                weightResults += "\n* FAIL *\n\n";
                weightResults += "Current Weight is set to: " + weight + " kilograms, but should be set to: " + i + " kilograms\n";
                weightResults+="set and read values have a difference of "+diff+" which is outside the 1% tolerance\n";

          }
        }
        mSFitSysCntrl.getFitProCntrl().removeCmd(wrCmd);
        return weightResults;
    }

    //--------------------------------------------//
    //
    //Testing System Configuration
    //
    //--------------------------------------------//
    public String testSystemConfiguration(String inputString) throws Exception{
        //outline for code support #951
        //read System Config data from Brainboard
        //try to output all values from System Device and Device Info
        String titleString;
        String systemString = null;

        titleString = "\n----------------------SYSTEM CONFIGURATION TEST----------------------\n\n";
        titleString += Calendar.getInstance().getTime() + "\n\n";

        double maxIncline;
        double minIncline;
        double maxSpeed;
        double minSpeed;

        maxIncline = hCmd.getMaxIncline();
        minIncline = hCmd.getMinIncline();

        maxSpeed = hCmd.getMaxSpeed();
        minSpeed = hCmd.getMinSpeed();
        Thread.sleep(1000);

        //Need a new Device object for some of the device info
        Device device = new Device();
        String brainboardLines[] = new String[11];
        //Split the user's input string into separate strings to be compared, line by line ("\n" is the delimiter)
        String inputLines[] = inputString.split("\r?\n|\r");

        brainboardLines[0] = "Console Name: \"" + MainDevice.getSysDevInfo().getConsoleName() + "\"";
        brainboardLines[1] = "Model Number: \"" + MainDevice.getSysDevInfo().getModel() + "\"";
        brainboardLines[2] = "Part Number: \"" + MainDevice.getSysDevInfo().getPartNumber() + "\"";
        brainboardLines[3] = "Software Version: \"" + device.getInfo().getSWVersion() + "\"";
        brainboardLines[4] = "Hardware Version: \"" + device.getInfo().getHWVersion() + "\"";
        brainboardLines[5] = "Serial Number: \"" + device.getInfo().getSerialNumber() + "\"";
        brainboardLines[6] = "Manufacturing Number: \"" + device.getInfo().getManufactureNumber() + "\"";
        brainboardLines[7] = "Max Incline: \"" + maxIncline + "\"";
        brainboardLines[8] = "Min. Incline: \"" + minIncline + "\"";
        brainboardLines[9] = "Max Speed: \"" + maxSpeed + "\"";
        brainboardLines[10] = "Min Speed: \"" + minSpeed + "\"";

        //Comparing the User-entered configuration values (from PDM?) with what is stored on the Brainboard
        for(int i = 0; i < brainboardLines.length; i++)
        {
            if(brainboardLines[i].equals(inputLines[i]))
            {
                systemString += "\nBrainboard " + brainboardLines[i] + "\nKeyboard Input " + inputLines[i] + "\n\n* PASS *\n\n";
            }
            else
            {
                systemString += "\nBrainboard " + brainboardLines[i] + "\n Keyboard Input " + inputLines[i] + "\n\n* FAIL *\n\n";
            }
        }

        systemString = titleString + systemString;

        return systemString;
    }
    //--------------------------------------------//
    //
    //Testing Pause Timeout
    //
    //--------------------------------------------//
    public String testPauseTimeout() throws Exception{
        //part of redmine #930
        //Set mode to Pause
        //Delay for 60 seconds
        //Verify Pause timeout by reading the mode and ensuring it is in Results mode
        String pauseResults;
        double timeout = 0;
        pauseResults = "\n\n------------------------PAUSE TIMEOUT TEST RESULTS------------------------\n\n";
        pauseResults += Calendar.getInstance().getTime() + "\n\n";

        System.out.println("Default pause timeout is: "+hCmd.getPauseTimeout());
        System.out.println("Current Mode: "+hCmd.getMode());

        timeout = hCmd.getPauseTimeout();
        System.out.println("pause timeout is set to : "+timeout);

        //Set mode to Running
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
       mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        pauseResults += "Status of setting the Mode to Running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        System.out.println("Current Mode: "+hCmd.getMode());

        //Set mode to Pause
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
       mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        pauseResults += "Status of setting the Mode to Pause: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        System.out.println("Current Mode: "+hCmd.getMode());

        //Check each second to see if mode has changed from Pause mode. Also prevents from waiting for longer than 1 minute
        for(long totalTime = 0; totalTime < 70; totalTime++){
            Thread.sleep(1000);
            System.out.println("after " + totalTime +" sec(s)the mode is  " + hCmd.getMode().getDescription());
            if(hCmd.getMode().getDescription() != "Pause Mode"){
                pauseResults += "The mode changed to " + hCmd.getMode().getDescription() + " after " + totalTime + " seconds\n";
                break;
            }
        }

        if(hCmd.getMode().getDescription() != "Pause Mode"){
            pauseResults += "\n* PASS *\n\n";
            pauseResults += "Pause Mode timed out to " + hCmd.getMode().getDescription() + "\n";
        }
        else{
            pauseResults += "\n* FAIL *\n\n";
            pauseResults += "Pause Mode did not time out after 60 seconds\n";
        }

        return pauseResults;
    }
    public String testIdleTimeout() throws Exception{
        //part of redmine #930
        //Set mode to Idle
        //change incline
        //Delay for the time set for Idle timeout seconds
        //Verify Idle timeout by reading the mode and ensuring the incline reset to zero
        String pauseResults;
        double timeout = 0;
        double runtime = 0;
        pauseResults = "\n\n------------------------IDLE TIMEOUT TEST RESULTS------------------------\n\n";
        pauseResults += Calendar.getInstance().getTime() + "\n\n";

        timeout = hCmd.getIdleTimeout();
        System.out.println("Default idle timeout is: " + timeout);
        System.out.println("Current Mode: "+hCmd.getMode());
        System.out.println("Current RunningTime: "+hCmd.getRunTime());

//       //Set incline to a 5 and check actual value until it has reached 5
//      ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, 5);
//       mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
//       Thread.sleep(1000);

//       while(hCmd.getActualIncline() != hCmd.getIncline()){
//           System.out.println("Current Actual incline : "+hCmd.getActualIncline()+" Goal " + hCmd.getIncline());
//           Thread.sleep(1000);
//       }
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(10000);

        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);


        //Check each second to see if mode has changed from Pause mode. Also prevents from waiting for longer than 1 minute
        for(long totalTime = 0; totalTime < timeout; totalTime++){
            Thread.sleep(1000);
            System.out.println("after " + totalTime +" sec(s)the running time is  " + hCmd.getRunTime());
//            if(hCmd.getActualIncline() != hCmd.getIncline()){
//                pauseResults += "The incline changed to " + hCmd.getActualIncline()+ " after " + totalTime + " seconds\n";
//                break;
//            }
        }

        if(hCmd.getRunTime() == 0){
            pauseResults += "\n* PASS *\n\n";
            pauseResults += "Idle Mode timed out and reset running time to " +hCmd.getRunTime()+"\n"; ;
        }
        else{
            pauseResults += "\n* FAIL *\n\n";
            pauseResults += "Idle Mode did not time out after "+timeout+" seconds\n";
        }

        return pauseResults;

    }

    //--------------------------------------------//
    //
    //Testing Running Time
    //
    //--------------------------------------------//
    public String testRunningTime() throws Exception{
        //outline for code support #930 in redmine
        String resultString;
        long runtime = 60; //time for running test (in secs)
        long pauseruntime = 23; //time for running test with pause

        resultString = "\n\n------------------------RUNNING TIME TEST RESULTS------------------------\n\n";
        resultString += Calendar.getInstance().getTime() + "\n\n";

        System.out.println("Default pause timeout is: "+hCmd.getPauseTimeout());
        System.out.println("Current Mode: "+hCmd.getMode());

        //set the pause timeout to 60
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.PAUSE_TIMEOUT,60);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        resultString+="Status of setting pause timeout to 60 secs: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        System.out.println("New pause timeout is: "+hCmd.getPauseTimeout());

        //set the mode to running
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(runtime*1000);
        //wait 1 minute
//        long elapsedTime = 0;
//        double seconds = 0;
//        long startime = System.nanoTime();
//        //Check the time elasped constanly until one minute has passed
//        for(double i = 0; i < 59; i=seconds)
//        {
//            elapsedTime = System.nanoTime() - startime;
//            seconds = elapsedTime / 1.0E09;
//        }
        //read the running time

        double timeOfRunningTest = hCmd.getRunTime();

        //Test whether the running time is within +/- 2 second of 60 seconds (allow for 1 sec read time)
        if(timeOfRunningTest >= runtime-2 && timeOfRunningTest <= runtime+2) {
            resultString += "\n\n* PASS *\n\n";
        }
        else {
            resultString += "\n\n* FAIL *\n\n";
        }

        resultString += "The running time for this "+runtime+" second test was " + timeOfRunningTest + " seconds\n";

        //set mode back to Pause to stop the test
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        //set mode back to Results
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        //set mode back to IDLE to reset running time
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        resultString += "\nPause Test: ";
        //start pause test
        //start running
        //set the mode to running
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE,ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(pauseruntime*1000);
        //wait 30 seconds
//        startime = System.nanoTime();
//        //Check the time elasped constanly until 30 secs have passed
//        for(double i = 0; i < 29; i=seconds)
//        {
//            elapsedTime = System.nanoTime() - startime;
//            seconds = elapsedTime / 1.0E09;
//        }
        //pause the test
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        //wait 30 seconds
        Thread.sleep(30000);
        //go from pause to running
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(pauseruntime*1000);
        //wait another 30 seconds for a total of 1 minute expected running time
//        startime = System.nanoTime();
//        //Check the time elasped constanly until 30 secs have passed
//        for(double i = 0; i < 29; i=seconds)
//        {
//            elapsedTime = System.nanoTime() - startime;
//            seconds = elapsedTime / 1.0E09;
//        }


        double timeOfPauseTest = hCmd.getRunTime();
        pauseruntime*=2; // This time is used twice
        //Test whether the running time is within +/- 2 seconds of 55 seconds
        if(timeOfPauseTest >= pauseruntime-2 && timeOfPauseTest <= pauseruntime+2){
            resultString += "\n\n* PASS *\n\n";
            resultString += "The total time for this "+pauseruntime+ " sec test with 30 sec pause correctly ran for " + timeOfPauseTest + " secs\n\n";
        }
        else {
            resultString += "\n\n* FAIL *\n\n";
            resultString += "The total time for this "+pauseruntime+" sec test with 30 sec pause actually ran for " + timeOfPauseTest +"\n\n";
        }

        //set mode back to idle to stop the test
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        //end the recurring callback
        mSFitSysCntrl.getFitProCntrl().removeCmd(wrCmd);

        return resultString;
    }

    //--------------------------------------------//
    //                                            //
    //              Testing Max Speed             //
    //                                            //
    //--------------------------------------------//
    //the testMaxSpeedTime is planned to automate #59 of the software
    //checklist to time the amount of time it takes to go from 0 to max speed
    public String testMaxSpeedTime() throws Exception{
        //outline for code #1051 in redmine
        //look up max speed for device (currently is not implemented - so just going to use 20kph)
        //send basic start command to start motor at on position
        //start stopwatch timer
        //send command to change speed to max speed
        //read current speed until actual is the same as target
        //stop stopwatch and return/display/record the value of the stopwatch
        String maxSpeedResults;
        double maxSpeed;
        double currentActualSpeed = 0;

        maxSpeedResults = "\n--------------------------MAX SPEED TEST--------------------------\n\n";
        maxSpeedResults += Calendar.getInstance().getTime() + "\n\n";

        //TODO: Once Max Speed command is implemented, just change the constant MAX_SPEED to the maxSpeed variable (which reads the value off of the Brainboard)
        FecpCommand readMaxSpeedCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), hCmd, 100, 100);
        ((WriteReadDataCmd)readMaxSpeedCommand.getCommand()).addReadBitField(BitFieldId.ACTUAL_KPH);
        ((WriteReadDataCmd)readMaxSpeedCommand.getCommand()).addReadBitField(BitFieldId.MAX_KPH);
        mSFitSysCntrl.getFitProCntrl().addCmd(readMaxSpeedCommand);
        maxSpeed = hCmd.getMaxSpeed();
        Thread.sleep(1000);
        System.out.println("The max speed is " + maxSpeed);

        FecpCommand modeCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);
        //set mode to idle to make sure the motor has stopped
        ((WriteReadDataCmd) modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        Thread.sleep(1000);
        //start timer
        //set mode to running
        ((WriteReadDataCmd)modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        //set to max speed currently hardcoded to 20kph as that is not implemented
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        Thread.sleep(1000);
        ((WriteReadDataCmd)modeCommand.getCommand()).addWriteData(BitFieldId.KPH, 16.0);//replace literal by maxSpeed later on
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        Thread.sleep(1000);
        long elapsedTime = 0;
        double seconds = 0;
        long startime = System.nanoTime();
        //Read the actual speed and count elsaped time. Do this until speed has reached MAX
        while(currentActualSpeed < 16)
        {
            currentActualSpeed = hCmd.getActualSpeed();
            elapsedTime = System.nanoTime() - startime;
            seconds = elapsedTime / 1.0E09;
            Thread.sleep(1000);
            System.out.println("actual speed "+currentActualSpeed+" elapsed time " + seconds +" seconds");
        }

        //test is over set back to idle to end the test
        ((WriteReadDataCmd) modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);

        maxSpeedResults += "The max speed is " + maxSpeed + "\n";
        maxSpeedResults += "The motor took " + seconds + " seconds to go to max speed\n";

        if(maxSpeed < 10){
            maxSpeedResults += "\n* FAIL * \n\nThe Max Speed was not properly read from the brainboard (Max Speed: "+maxSpeed+" kph)\n";
        }

        //%5 pass standard with a 23 sec spec from #59
        if((seconds <= 22) || (seconds >= 24)) {
            maxSpeedResults += "\n* FAIL * \n\nThe motor was off by " + (seconds - 23) + " seconds\n";
        }

        else {
            maxSpeedResults += "\n* PASS *\n\n";
            maxSpeedResults += "The Max Speed was correctly read off of the brainboard and the speed up to Max Speed took "+
                    seconds+" seconds, which is within the 5% tolerance\n";
        }

        return maxSpeedResults;
    }


}
