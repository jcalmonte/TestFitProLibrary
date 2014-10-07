package com.ifit.sfit.sparky.helperclasses;

import com.ifit.sfit.sparky.testsdrivers.BaseTest;
import com.ifit.sparky.fecp.OnCommandReceivedListener;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.AudioSourceConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.AudioSourceId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.BitfieldDataConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.BoolConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ByteConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.CaloriesConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.GradeConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.KeyObjectConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.LongConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.NameConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ResistanceConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ShortConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.SpeedConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.WeightConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.WorkoutConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.WorkoutId;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.key.KeyObject;
import com.ifit.sparky.fecp.interpreter.status.PortalDeviceSts;
import com.ifit.sparky.fecp.interpreter.status.StatusId;
import com.ifit.sparky.fecp.interpreter.status.WriteReadDataSts;

import java.util.TreeMap;

/**
 * Handles reading Bitfield values from brainboard through a command
 * Created by jc.almonte on 6/27/14.
 */
public class HandleCmd implements OnCommandReceivedListener
{
    private BaseTest mAct;
    private  double mMaxSpeed = 0.0;
    private  double mMinSpeed = 0.0;
    private ModeId mResultMode;

    private  double mSpeed = 0.0;
    private  double mActualSpeed = 0.0;
    private  double mIncline = 0.0;
    private  double mActualIncline = 0.0;
    private  double mMaxIncline = 0.0;
    private  double mMinIncline = 0.0;
    private  double mTransMax = 0;
    private  double mDistance = 0;
    private  double mRunTime = 0;
    private  double mCalories = 0;
    private  double mWeight = 0;
    private  double mAge = 0;
    private  double mFanSpeed = 0;
    private  double mIdleTimeout = 0;
    private  double mPauseTimeout = 0;
    private  double mVolume = 0;
    private  double mPulse = 0;
    private  double mResistance = 0;
    private  double mActualResistance = 0;
    private double mWatts;
    private double mTorque= 0;
    private double mRpm = 0;
    private double mGears = 0;
    private double mBvVolume = 0;
    private double mBvFrequency = 0;
    private double mHeight = 0;
    private double mMaxResitance = 0;
    private double mMaxWeight = 0;
    private double mAveragePulse = 0;
    private double mMaxPulse = 0;
    private double mAverageKph = 0;
    private double mWtMaxKph = 0;
    private double mAverageGrade = 0;
    private double mWtMaxGrade = 0;
    private double mAverageWatts = 0;
    private double mMaxWatts = 0;
    private double mAverageRpm = 0;
    private double mMaxRpm = 0;
    private double mKphGoal = 0;
    private double mGradeGoal = 0;
    private double mResistanceGoal= 0;
    private double mWattGoal= 0;
    private double mTorqueGoal = 0;
    private double mRpmGoal = 0;
    private double mDistanceGoal = 0;
    private double mActualDistance = 0;
    private double mPulseGoal=0;

    private WorkoutId mWorkoutId,mRequestedWorkoutId;
    private AudioSourceId mAudioSourceId;

    private KeyObject mKey;
    private String valueToString="none";
    private boolean mSystemUnits;
    private boolean mGender;

    private String mFirstName = "";
    private String mLasttName = "";
    private String mIfitUserName = "";


    /**
     * Constructor
     * @param act
     */
    public HandleCmd(BaseTest act) {

        this.mAct = act;
        this.valueToString = "No Value!";
    }

    /**
     * Handles the reply from the device
     * @param cmd the command that was sent.
     */
    @Override
    public void onCommandReceived(Command cmd) {
        //check command type
        TreeMap<BitFieldId, BitfieldDataConverter> commandData;

        if(cmd.getStatus().getStsId() == StatusId.FAILED)
        {
            return;
        }
        //if running mode, just join the party
        if(cmd.getCmdId() == CommandId.WRITE_READ_DATA || cmd.getCmdId() == CommandId.PORTAL_DEV_LISTEN)
        {
            commandData = ((WriteReadDataSts)cmd.getStatus()).getResultData();

            if(cmd.getCmdId() == CommandId.PORTAL_DEV_LISTEN)
            {
                commandData = ((PortalDeviceSts)cmd.getStatus()).getmSysDev().getCurrentSystemData();
            }
            else {
                WriteReadDataSts sts = (WriteReadDataSts) cmd.getStatus();
                commandData = sts.getResultData();
            };

            //Read the KPH value off of the Brainboard
            if(commandData.containsKey(BitFieldId.KPH)) {

                try {
                    mSpeed = ((SpeedConverter)commandData.get(BitFieldId.KPH).getData()).getSpeed();
                    this.valueToString = String.valueOf(mSpeed);
                    //System.out.println("Current Speed (TestHandleInfo): " + mSpeed);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //Read the Incline value off of the Brainboard
            if(commandData.containsKey(BitFieldId.GRADE)) {
                try {
                    mIncline = ((GradeConverter)commandData.get(BitFieldId.GRADE).getData()).getIncline();
                    this.valueToString = String.valueOf(mIncline);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //Read the Resistance value off of the Brainboard
            if(commandData.containsKey(BitFieldId.RESISTANCE)) {

                try {
                    mResistance = ((ResistanceConverter)commandData.get(BitFieldId.RESISTANCE).getData()).getResistance();
                    this.valueToString = String.valueOf(mResistance);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //Read the Watts value off of the Brainboard
            if(commandData.containsKey(BitFieldId.WATTS)) {
                try {
                    mWatts = ((ShortConverter) commandData.get(BitFieldId.WATTS).getData()).getValue();
                    this.valueToString = String.valueOf(mWatts);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //Read the Torque value off of the Brainboard
            if(commandData.containsKey(BitFieldId.TORQUE)) {
                try {
                    mTorque = ((ShortConverter) commandData.get(BitFieldId.TORQUE).getData()).getValue();
                    this.valueToString = String.valueOf(mTorque);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(commandData.containsKey(BitFieldId.RPM)) {
                try {
                    mRpm = ((ByteConverter) commandData.get(BitFieldId.RPM).getData()).getValue();
                    this.valueToString = String.valueOf(mRpm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //Read the Distance value off of the Brainboard
            if(commandData.containsKey(BitFieldId.DISTANCE)) {
                try{
                    mDistance = ((LongConverter)commandData.get(BitFieldId.DISTANCE).getData()).getValue();
                    this.valueToString = String.valueOf(mDistance);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
//Read the Object Key value off of the Brainboard
            if(commandData.containsKey(BitFieldId.KEY_OBJECT)) {
                try {
                    mKey = ((KeyObjectConverter) commandData.get(BitFieldId.KEY_OBJECT).getData()).getKeyObject();
                    this.valueToString = String.valueOf(mKey);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            //Read the Fan Speed off of the Brainboard
            if(commandData.containsKey(BitFieldId.FAN_SPEED)) {
                try{
                    mFanSpeed = ((ByteConverter)commandData.get(BitFieldId.FAN_SPEED).getData()).getValue();
                    this.valueToString = String.valueOf(mFanSpeed);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Read the Volume value off of the Brainboard
            if(commandData.containsKey(BitFieldId.VOLUME)) {

                try {
                    mVolume = ((ByteConverter)commandData.get(BitFieldId.VOLUME).getData()).getValue();
                    this.valueToString = String.valueOf(mVolume);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //Read the Actual Resistance value off of the Brainboard
            if(commandData.containsKey(BitFieldId.PULSE)) {

                try {
                    mPulse = ((ByteConverter)commandData.get(BitFieldId.PULSE).getData()).getValue();
                    this.valueToString = String.valueOf(mPulse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //Read the Running Time value off of the Brainboard
            if(commandData.containsKey(BitFieldId.RUNNING_TIME)) {
                try {
                    mRunTime = ((LongConverter) commandData.get(BitFieldId.RUNNING_TIME).getData()).getValue();
                    this.valueToString = String.valueOf(mRunTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //Read the Workout Mode off of the Brainboard
            if(commandData.containsKey(BitFieldId.WORKOUT_MODE)) {

                try {
                    mResultMode = ((ModeConverter)commandData.get(BitFieldId.WORKOUT_MODE)).getMode();
                    this.valueToString = String.valueOf(mResultMode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Read the Calories value off of the Brainboard
            if(commandData.containsKey(BitFieldId.CALORIES)) {
                try {
                    //currently returns the bitfield id of 13 only as of 3/4/2014
                    mCalories = ((CaloriesConverter) commandData.get(BitFieldId.CALORIES).getData()).getCalories();
                    this.valueToString = String.valueOf(mCalories);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.AUDIO_SOURCE)) {
                try {
                    mAudioSourceId = ((AudioSourceConverter) commandData.get(BitFieldId.AUDIO_SOURCE).getData()).getAudioSource();
                    this.valueToString = String.valueOf(mAudioSourceId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //Read the Actual KPH value off of the Brainboard
            if(commandData.containsKey(BitFieldId.ACTUAL_KPH)) {

                try {
                    mActualSpeed = ((SpeedConverter)commandData.get(BitFieldId.ACTUAL_KPH).getData()).getSpeed();
                    this.valueToString = String.valueOf(mActualSpeed);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //Read the Actual Incline value off of the Brainboard
            if(commandData.containsKey(BitFieldId.ACTUAL_INCLINE)) {
                try {
                    mActualIncline = ((GradeConverter)commandData.get(BitFieldId.ACTUAL_INCLINE).getData()).getIncline();
                    this.valueToString = String.valueOf(mActualIncline);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //Read the Actual Resistance value off of the Brainboard
            if(commandData.containsKey(BitFieldId.ACTUAL_RESISTANCE)) {

                try {
                    mActualResistance = ((ResistanceConverter)commandData.get(BitFieldId.ACTUAL_RESISTANCE).getData()).getResistance();
                    this.valueToString = String.valueOf(mActualResistance);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            if(commandData.containsKey(BitFieldId.ACTUAL_DISTANCE)) {
                try {
                    mActualDistance = ((LongConverter) commandData.get(BitFieldId.ACTUAL_DISTANCE).getData()).getValue();
                    this.valueToString = String.valueOf(mActualDistance);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //Read the Workout value off of the Brainboard
            if(commandData.containsKey(BitFieldId.WORKOUT)) {

                try {
                    mWorkoutId = ((WorkoutConverter)commandData.get(BitFieldId.WORKOUT).getData()).getWorkout();
                    this.valueToString = String.valueOf(mWorkoutId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(commandData.containsKey(BitFieldId.REQUESTED_WORKOUT)) {
                try {
                    mRequestedWorkoutId = ((WorkoutConverter) commandData.get(BitFieldId.REQUESTED_WORKOUT).getData()).getWorkout();
                    this.valueToString = String.valueOf(mRequestedWorkoutId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //Read the Age value off of the Brainboard
            if(commandData.containsKey(BitFieldId.AGE)) {
                try {
                    mAge = ((ByteConverter) commandData.get(BitFieldId.AGE).getData()).getValue();
                    this.valueToString = String.valueOf(mAge);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //Read the Weight value off of the Brainboard
            if(commandData.containsKey(BitFieldId.WEIGHT)) {
                try {
                    mWeight = ((WeightConverter) commandData.get(BitFieldId.WEIGHT).getData()).getWeight();
                    this.valueToString = String.valueOf(mWeight);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.GEARS)) {
                try {
                    mGears = ((ByteConverter) commandData.get(BitFieldId.GEARS).getData()).getValue();
                    this.valueToString = String.valueOf(mGears);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //Read the Max Incline value off of the Brainboard
            if(commandData.containsKey(BitFieldId.MAX_GRADE)) {
                try {
                    mMaxIncline = ((GradeConverter)commandData.get(BitFieldId.MAX_GRADE).getData()).getIncline();
                    this.valueToString = String.valueOf(mMaxIncline);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Read the Min Incline value off of the Brainboard
            if(commandData.containsKey(BitFieldId.MIN_GRADE)) {
                try {
                    mMinIncline = ((GradeConverter)commandData.get(BitFieldId.MIN_GRADE).getData()).getIncline();
                    this.valueToString = String.valueOf(mMinIncline);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Read the TransMax value off of the Brainboard
            if(commandData.containsKey(BitFieldId.TRANS_MAX)) {
                try {
                    mTransMax = ((ShortConverter)commandData.get(BitFieldId.TRANS_MAX).getData()).getValue();
                    this.valueToString = String.valueOf(mTransMax);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //Read the Max KPH value off of the Brainboard
            if(commandData.containsKey(BitFieldId.MAX_KPH)) {
                try{
                    //currently gets a null pointer assigned as of 3/3/2014
                    mMaxSpeed = ((SpeedConverter)commandData.get(BitFieldId.MAX_KPH).getData()).getSpeed();
                    this.valueToString = String.valueOf(mMaxSpeed);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Read the Min KPH value off of the Brainboard
            if(commandData.containsKey(BitFieldId.MIN_KPH)) {
                try{
                    //currently gets a null pointer assigned as of 3/3/2014
                    mMinSpeed = ((SpeedConverter)commandData.get(BitFieldId.MIN_KPH).getData()).getSpeed();
                    this.valueToString = String.valueOf(mMinSpeed);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(commandData.containsKey(BitFieldId.BV_VOLUME)) {
                try {
                    mBvVolume = ((ByteConverter) commandData.get(BitFieldId.BV_VOLUME).getData()).getValue();
                    this.valueToString = String.valueOf(mBvVolume);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.BV_FREQUENCY)) {
                try {
                    mBvFrequency = ((ShortConverter) commandData.get(BitFieldId.BV_FREQUENCY).getData()).getValue();
                    this.valueToString = String.valueOf(mBvFrequency);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //Read the Idle Timeout value off of the Brainboard
            if(commandData.containsKey(BitFieldId.IDLE_TIMEOUT)) {
                try {
                    mIdleTimeout = ((ShortConverter) commandData.get(BitFieldId.IDLE_TIMEOUT).getData()).getValue();
                    this.valueToString = String.valueOf(mIdleTimeout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Read the Pause Timeout value off of the Brainboard
            if(commandData.containsKey(BitFieldId.PAUSE_TIMEOUT)) {
                try {
                    mPauseTimeout = ((ShortConverter) commandData.get(BitFieldId.PAUSE_TIMEOUT).getData()).getValue();
                    this.valueToString = String.valueOf(mPauseTimeout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //Read the Pause Timeout value off of the Brainboard
            if(commandData.containsKey(BitFieldId.SYSTEM_UNITS)) {
                try {
                    mSystemUnits = ((BoolConverter) commandData.get(BitFieldId.SYSTEM_UNITS).getData()).getValue();
                    this.valueToString = String.valueOf(mSystemUnits);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.GENDER)) {
                try {
                    mGender = ((BoolConverter) commandData.get(BitFieldId.GENDER).getData()).getValue();
                    this.valueToString = String.valueOf(mGender);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.FIRST_NAME)) {
                try {
                    mFirstName = ((NameConverter) commandData.get(BitFieldId.FIRST_NAME)).getName();
                    this.valueToString = String.valueOf(mFirstName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.LAST_NAME)) {
                try {
                    mLasttName = ((NameConverter) commandData.get(BitFieldId.LAST_NAME)).getName();
                    this.valueToString = String.valueOf(mLasttName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.IFIT_USER_NAME)) {
                try {
                    mIfitUserName = ((NameConverter) commandData.get(BitFieldId.IFIT_USER_NAME)).getName();
                    this.valueToString = String.valueOf(mIfitUserName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.HEIGHT)) {
                try {
                    mHeight = ((ShortConverter) commandData.get(BitFieldId.HEIGHT).getData()).getValue();
                    this.valueToString = String.valueOf(mHeight);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.MAX_RESISTANCE)) {
                try {
                    mMaxResitance = ((ByteConverter) commandData.get(BitFieldId.MAX_RESISTANCE).getData()).getValue();
                    this.valueToString = String.valueOf(mMaxResitance);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(commandData.containsKey(BitFieldId.MAX_WEIGHT)) {
                try {
                    mMaxWeight = ((WeightConverter) commandData.get(BitFieldId.MAX_WEIGHT).getData()).getWeight();
                    this.valueToString = String.valueOf(mMaxWeight);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.AVERAGE_PULSE)) {
                try {
                    mAveragePulse = ((ByteConverter) commandData.get(BitFieldId.AVERAGE_PULSE).getData()).getValue();
                    this.valueToString = String.valueOf(mAveragePulse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.MAX_PULSE)) {
                try {
                    mMaxPulse = ((ByteConverter) commandData.get(BitFieldId.MAX_PULSE).getData()).getValue();
                    this.valueToString = String.valueOf(mMaxPulse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.AVERAGE_KPH)) {
                try {
                    mAverageKph = ((SpeedConverter) commandData.get(BitFieldId.AVERAGE_KPH).getData()).getSpeed();
                    this.valueToString = String.valueOf(mAverageKph);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.WT_MAX_KPH)) {
                try {
                    mWtMaxKph = ((SpeedConverter) commandData.get(BitFieldId.WT_MAX_KPH).getData()).getSpeed();
                    this.valueToString = String.valueOf(mWtMaxKph);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.AVERAGE_GRADE)) {
                try {
                    mAverageGrade = ((GradeConverter) commandData.get(BitFieldId.AVERAGE_GRADE).getData()).getIncline();
                    this.valueToString = String.valueOf(mAverageGrade);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.WT_MAX_GRADE)) {
                try {
                    mWtMaxGrade = ((GradeConverter) commandData.get(BitFieldId.WT_MAX_GRADE).getData()).getIncline();
                    this.valueToString = String.valueOf(mWtMaxGrade);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.AVERAGE_WATTS)) {
                try {
                    mAverageWatts = ((SpeedConverter) commandData.get(BitFieldId.AVERAGE_WATTS).getData()).getSpeed();
                    this.valueToString = String.valueOf(mAverageWatts);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.MAX_WATTS)) {
                try {
                    mMaxWatts = ((SpeedConverter) commandData.get(BitFieldId.MAX_WATTS).getData()).getSpeed();
                    this.valueToString = String.valueOf(mMaxWatts);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.AVERAGE_RPM)) {
                try {
                    mAverageRpm = ((SpeedConverter) commandData.get(BitFieldId.AVERAGE_RPM).getData()).getSpeed();
                    this.valueToString = String.valueOf(mAverageRpm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.MAX_RPM)) {
                try {
                    mMaxRpm = ((SpeedConverter) commandData.get(BitFieldId.MAX_RPM).getData()).getSpeed();
                    this.valueToString = String.valueOf(mMaxRpm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.KPH_GOAL)) {
                try {
                    mKphGoal = ((SpeedConverter) commandData.get(BitFieldId.KPH_GOAL).getData()).getSpeed();
                    this.valueToString = String.valueOf(mKphGoal);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.GRADE_GOAL)) {
                try {
                    mGradeGoal = ((GradeConverter) commandData.get(BitFieldId.GRADE_GOAL).getData()).getIncline();
                    this.valueToString = String.valueOf(mGradeGoal);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.RESISTANCE_GOAL)) {
                try {
                    mResistanceGoal = ((ResistanceConverter) commandData.get(BitFieldId.RESISTANCE_GOAL).getData()).getResistance();
                    this.valueToString = String.valueOf(mResistanceGoal);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.WATT_GOAL)) {
                try {
                    mWattGoal = ((ShortConverter) commandData.get(BitFieldId.WATT_GOAL).getData()).getValue();
                    this.valueToString = String.valueOf(mWattGoal);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.TORQUE_GOAL)) {
                try {
                    mTorqueGoal = ((ShortConverter) commandData.get(BitFieldId.TORQUE_GOAL).getData()).getValue();
                    this.valueToString = String.valueOf(mTorqueGoal);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.RPM_GOAL)) {
                try {
                    mRpmGoal = ((ShortConverter) commandData.get(BitFieldId.RPM_GOAL).getData()).getValue();
                    this.valueToString = String.valueOf(mRpmGoal);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.DISTANCE_GOAL)) {
                try {
                    mDistanceGoal = ((LongConverter) commandData.get(BitFieldId.DISTANCE_GOAL).getData()).getValue();
                    this.valueToString = String.valueOf(mDistanceGoal);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(commandData.containsKey(BitFieldId.PULSE_GOAL)) {
                try {
                    mPulseGoal = ((ByteConverter) commandData.get(BitFieldId.PULSE_GOAL).getData()).getValue();
                    this.valueToString = String.valueOf(mPulseGoal);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *
     * @return the string value of last Bitfield read
     */
    @Override
    public String toString() {
        return this.valueToString;
    }

    /**
     * Gets the value of the bitfield specified by bitFieldName
     * @param bitfieldName the name of the Bitfield
     * @return the value of the Bitfield requested to be read
     */
    public double getValue(String bitfieldName) {
        double value = 0;
        switch (bitfieldName)
        {
            case "KPH":
                value = this.mSpeed;
                break;
            case "GRADE":
                value = this.mIncline;
                break;
            case "RESISTANCE":
                value = this.mResistance;
                break;
            case "WATTS":
                value = this.mWatts;
                break;
            case "TORQUE":
                value = this.mTorque;
                break;
            case "RPM":
                value = this.mRpm;
                break;
            case "DISTANCE":
                value = this.mDistance;
                break;
            case "KEY_OBJECT":
                value = this.mKey.getRawKeyCode();
                break;
            case "FAN_SPEED":
                value = this.mFanSpeed;
                break;
            case "VOLUME":
                value = this.mVolume;
                break;
            case "PULSE":
                value = this.mPulse;
                break;
            case "RUNNING_TIME":
                value = this.mRunTime;
                break;
            case "WORKOUT_MODE":
                value = this.mResultMode.getValue();
                break;
            case "CALORIES":
                value = this.mCalories;
                break;
            case "AUDIO_SOURCE":
                value = this.mAudioSourceId.getValue();
                break;
            case "ACTUAL_KPH":
                value = this.mActualSpeed;
                break;
            case "ACTUAL_INCLINE":
                value = this.mActualIncline;
                break;
            case "ACTUAL_RESISTANCE":
                value = this.mActualResistance;
                break;
            case "ACTUAL_DISTANCE":
                value = this.mActualDistance;
                break;
            case "WORKOUT":
                value = this.mWorkoutId.getValue();
                break;
            case "REQUESTED_WORKOUT":
                value = this.mRequestedWorkoutId.getValue();
                break;
            case "AGE":
                value = this.mAge;
                break;
            case "WEIGHT":
                value = this.mWeight;
                break;
            case "GEARS":
                value = this.mGears;
                break;
            case "MAX_GRADE":
                value = this.mMaxIncline;
                break;
            case "MIN_GRADE":
                value = this.mMinIncline;
                break;
            case "TRANS_MAX":
                value = this.mTransMax;
                break;
            case "MAX_KPH":
                value = this.mMaxSpeed;
                break;
            case "MIN_KPH":
                value = this.mMinSpeed;
                break;
            case "BV_VOLUME":
                value = this.mBvVolume;
                break;
            case "BV_FREQUENCY":
                value = this.mBvFrequency;
                break;
            case "IDLE_TIMEOUT":
                value = this.mIdleTimeout;
                break;
            case "PAUSE_TIMEOUT":
                value = this.mPauseTimeout;
                break;
            case "SYSTEM_UNITS":
                value =  (mSystemUnits) ? 1 : 0; //return 1 if true, 0 if false
                break;
            case "GENDER":
                value =  (mGender) ? 1 : 0; //return 1 if true, 0 if false
                break;
            case "HEIGHT":
                value = this.mHeight;
                break;
            case "MAX_RESISTANCE":
                value = this.mMaxResitance;
                break;
            case "MAX_WEIGHT":
                value = this.mMaxWeight;
                break;
            case "AVERAGE_PULSE":
                value = this.mAveragePulse;
                break;
            case "MAX_PULSE":
                value = this.mMaxPulse;
                break;
            case "AVERAGE_KPH":
                value = this.mAverageKph;
                break;
            case "WT_MAX_KPH":
                value = this.mWtMaxKph;
                break;
            case "AVERAGE_GRADE":
                value = this.mAverageGrade;
                break;
            case "WT_MAX_GRADE":
                value = this.mWtMaxGrade;
                break;
            case "AVERAGE_WATTS":
                value = this.mAverageWatts;
                break;
            case "MAX_WATTS":
                value = this.mMaxWatts;
                break;
            case "AVERAGE_RPM":
                value = this.mAverageRpm;
                break;
            case "MAX_RPM":
                value = this.mMaxRpm;
                break;
            case "KPH_GOAL":
                value = this.mKphGoal;
                break;
            case "GRADE_GOAL":
                value = this.mGradeGoal;
                break;
            case "RESISTANCE_GOAL":
                value = this.mResistanceGoal;
                break;
            case "WATT_GOAL":
                value = this.mWattGoal;
                break;
            case "TORQUE_GOAL":
                value = this.mTorqueGoal;
                break;
            case "RPM_GOAL":
                value = this.mRpmGoal;
                break;
            case "DISTANCE_GOAL":
                value = this.mDistanceGoal;
                break;
            case "PULSE_GOAL":
                value = this.mPulseGoal;
                break;
        }
        this.valueToString = String.valueOf(value);
        return value;
    }

    /**
     * Overloaded function of {@link #getValue(String)}
     * @param bitFieldId the BitfieldId we want to access
     * @return Bitfield value
     */
    public double getValue(BitFieldId bitFieldId){

        return this.getValue(bitFieldId.name());
    }

    /**
     * Gets the value of the bitfield specified by bitFieldName
     * @param bitfieldName the name of the Bitfield
     * @return the String value of the Bitfield requested to be read
     */
    public String getValueStr(String bitfieldName) {
        String value = "";
        switch (bitfieldName)
        {

            case "FIRST_NAME":
                value = mFirstName;
                break;
            case "LAST_NAME":
                value = mLasttName;
                break;
            case "IFIT_USER_NAME":
                value = mIfitUserName;
                break;
        }
        this.valueToString = String.valueOf(value);
        return value;
    }

    /**
     * Gets value of the bitfield specified by bitFieldName
     * @param bitFieldId
     * @return the String value saved on the bitfield
     */
    public String getValueStr(BitFieldId bitFieldId) {

        return this.getValueStr(bitFieldId.name());
    }

    /**
     * Gets value current set speed from brainboard
     * @return Speed
     */
    public double getSpeed(){ return this.mSpeed; }

    /**
     * Gets current set Incline value from brainboard
     * @return (double) Incline
     */
    public double getIncline(){ return this.mIncline; }

    /**
     * Gets the console's set resistance from brainboard
     * @return Resistance
     */
    public double getResistance(){return this.mResistance;}

    /**
     * Gets the console's set watts from brainboard
     * @return Resistance
     */
    public double getWatts(){return this.mWatts;}

    /**
     * Gets the console's set torque from brainboard
     * @return Resistance
     */
    public double getTorque(){return this.mTorque;}

    /**
     * Gets the console's set rpm from brainboard
     * @return Resistance
     */
    public double getRpm(){return this.mRpm;}

    /**
     * Gets Distance value from brainboard
     * @return Distance
     */
    public double getDistance() { return this.mDistance; }

    /**
     * Gets the Key Object from the brainboard. This key object is used to simulate key-presses
     * @return Key Object
     */
    public KeyObject getKey() { return this.mKey;}

    /**
     * Gets the current set speed of the fan from brainbaord
     * @return Fan Speed
     */
    public double getFanSpeed() { return this.mFanSpeed; }

    /**
     * Gets the current set volume for the console's speaker
     * @return Volume
     */

    public double getVolume(){return this.mVolume;}

    /**
     * Gets the Pulse value from brainboard
     * @return
     */
    public double getPulse(){return this.mPulse;}

    /**
     * Gets the current workout running time
     * @return Running Time
     */
    public double getRunTime() { return this.mRunTime; }
    /**
     * Gets the current Workout mode (RUNNING, IDLE, etc...)
     * @return Workout Mode
     */
    public ModeId getMode(){ return this.mResultMode; }

    /**
     * Gets the calories burned on current workout form brainboard
     * @return Calories
     */
    public double getCalories() { return this.mCalories; }

    /**
     * Gets the Audio Source  form brainboard
     * @return Audio Source
     */
    public AudioSourceId getAudioSourceId() {return this.mAudioSourceId;}

    /**
     * Gets actual speed value from brainboard
     * @return  Actual Speed
     */

    public double getActualSpeed(){ return this.mActualSpeed; }

    /**
     * Gets Actual Incline value from brainboard
     * @return Actual Incline
     */
    public double getActualIncline(){ return this.mActualIncline; }

    /**
     * Gets the actual Resistance value from brainboard
     * @return
     */
    public double getActualResistance(){return this.mActualResistance;}

    /**
     * Gets Distance value from brainboard
     * @return Distance
     */
    public double getActualDistance() { return this.mActualDistance; }

    /**
     * Gets the ID of the current workout
     * @return Workout ID
     */
    public WorkoutId getWorkoutId(){return this.mWorkoutId;}

    /**
     * Gets the value of the the requested workout
     * @return Workout ID
     */
    public WorkoutId getRequestedWorkoutId (){return this.mRequestedWorkoutId;}

    /**
     * Gets current user set age from brainboard
     * @return Age
     */
    public double getAge() { return this.mAge; }

    /**
     * Gets the current user set weight from brainboard
     * @return Weight
     */
    public double  getWeight() { return  this.mWeight; }

    /**
     * Gets the gear value from brainboard
     * @return Gears
     */
    public double getGears() {return this.mGears;}
    /**
     * Gets the console's Max Incline value from brainboard
     * @return Max Incline
     */
    public double getMaxIncline(){ return this.mMaxIncline; }

    /**
     * Gets the console's Min Incline value from brainboard
     * @return Min Incline
     */
    public double getMinIncline(){ return this.mMinIncline; }

    /**
     * Gets the console's Trans Max value from brainboard
     * @return Trans Max
     */
    public double getTransMax(){ return this.mTransMax; }

    /**
     * Gets the console's Max speed from brainboard
     * @return  Max Speed
     */
    public double getMaxSpeed() { return  this.mMaxSpeed; }

    /**
     * Gets the console's Min Speed value from brainboard
     * @return Min Speed
     */
    public double getMinSpeed() { return this.mMinSpeed; }

    /**
     * Gets the console's Broadcast Vision Volume  value from brainboard
     * @return Broadcast Vision Volume
     */
    public double getBvVolume(){ return this.mBvVolume; }

    /**
     * Gets the console's Broadcast Vision Frequency  value from brainboard
     * @return Broadcast Vision Frequency
     */
    public double getBvFrequency(){ return this.mBvFrequency; }

    /**
     * Gets from brainbaord the current Timeout it takes to go from Result to Idle
     * @return Idle Timeout
     */
    public double getIdleTimeout() { return this.mIdleTimeout; }

    /**
     * Gets from brainbaord the current Timeout it takes to go from Pause to Results
     * @return Pause Timeout
     */
    public double getPauseTimeout() { return this.mPauseTimeout; }

    /**
     * Used to determine what units to display so the console match the tablet
     * Write only in Maintance Mode (0 is English, 1 is Metric)
     * @return 0 if English, 1 if Metric
     */
    public boolean getSystemUnits(){return this.mSystemUnits;}

    /**
     * Gets user gender
     * @return false(0) for female, true(1) for men
     */
    public boolean getGender(){return this.mGender;}

    /**
     * Gets user's first name
     * @return First Name
     */
    public String getFirstName(){return this.mFirstName;}
    /**
     * Gets user's Last name
     * @return Last Name
     */
    public String getLastName(){return this.mLasttName;}
    /**
     * Gets user's Ifit UserName
     * @return Ifit User Name
     */
    public String getIfitUserName(){return this.mIfitUserName;}

    /**
     * Gets the current user set Height from brainboard
     * @return Height
     */
    public double  getHeight() { return  this.mHeight; }

    /**
     * Gets Max resistance from brainboard
     * @return Max Resistance
     */

    public double getMaxResistance(){return mMaxResitance;}

    /**
     * Gets Max Weight value from brainboard
     * @return Max Weight
     */
    public double getMaxWeight(){return mMaxWeight;}

    /**
     * Gets average pulse value from brainboard
     * @return Average Pulse
     */
    public double getAveragePulse(){return mAveragePulse;}

    /**
     * Gets Max pulse value from brainboard
     * @return Max Pulse
     */
    public double getMaxPulse(){return mMaxPulse;}

    /**
     * Gets average kph value from brainboard
     * @return Average Kph
     */
    public double getAverageKph(){return mAverageKph;}

    /**
     * Gets Watts Max Kph  value from brainboard
     * @return Watts Max Kph
     */
    public double getWtMaxKph(){return mWtMaxKph;}

    /**
     * Gets average incline value from brainboard
     * @return Average grade
     */
    public double getAverageGrade(){return mAverageGrade;}


    /**
     * Gets Watts Max Grade  value from brainboard
     * @return Watts Max Grade
     */
    public double getWtMaxGrade(){return mWtMaxGrade;}

    /**
     * Gets average watts value from brainboard
     * @return Average wattds
     */
    public double getAverageWatts(){return mAverageWatts;}

    /**
     * Gets Max Watts value from brainboard
     * @return Max Watts
     */
    public double getMaxWatts(){return mMaxWatts;}

    /**
     * Gets average Rpm value from brainboard
     * @return Average Rpm
     */
    public double getAverageRpm(){return mAverageRpm;}

    /**
     * Gets Max Rpm value from brainboard
     * @return Max Rpm
     */
    public double getMaxRpm(){return mMaxRpm;}

    /**
     * Gets kph goal value from brainboard
     * @return Kph Goal
     */
    public double getKphGoal(){return mKphGoal;}

    /**
     * Gets grade goal value from brainboard
     * @return Grade Goal
     */
    public double getGradeGoal(){return mGradeGoal;}

    /**
     * Gets Resistance goal value from brainboard
     * @return Resistance Goal
     */
    public double getResistanceGoal(){return mResistanceGoal;}

    /**
     * Gets Watt goal value from brainboard
     * @return Watt Goal
     */
    public double getWattGoal(){return mWattGoal;}

    /**
     * Gets Torque goal value from brainboard
     * @return Torque Goal
     */
    public double getTorqueGoal(){return mTorqueGoal;}

    /**
     * Gets Rpm goal value from brainboard
     * @return Rpm Goal
     */
    public double getRpmGoal(){return mRpmGoal;}

    /**
     * Gets Distance goal value from brainboard
     * @return Distance Goal
     */
    public double getDistanceGoal(){return mDistanceGoal;}

    /**
     * Gets Pulse goal value from brainboard
     * @return Pulse Goal
     */
    public double getPulseGoal(){return mPulseGoal;}

}