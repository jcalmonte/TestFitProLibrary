/**
 * Handles all the device SuperClass items.
 * @author Levi.Balling
 * @date 12/9/2013
 * @version 1
 * Release Date
 * @date 12/10/13
 * This class will handle the device items, all the available commands and status connected
 * to the commands.
 */
package com.ifit.sparky.fecp.interpreter.device;

import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Device {

    private Map<CommandId, Command> mCommandMap;//list of all the available commands.
    private DeviceId    mDevId;
    private ArrayList<Device> mSubDevArrayList;//list of all the subDevices.
    //todo add the databitfields

    /**
     * Default constructor for devices.
     */
    public Device()
    {
        this.mCommandMap = new LinkedHashMap<CommandId, Command>();
        this.mSubDevArrayList = new ArrayList<Device>();
        this.mDevId = DeviceId.NONE;
    }

    /**
     * constructor for single device.
     */
    public Device(int idVal) throws Exception
    {
        this.mCommandMap = new LinkedHashMap<CommandId, Command>();
        this.mSubDevArrayList = new ArrayList<Device>();
        this.mDevId = DeviceId.getDeviceId(idVal);
    }

    /**
     * constructor for single device.
     */
    public Device(DeviceId id)
    {
        this.mCommandMap = new LinkedHashMap<CommandId, Command>();
        this.mSubDevArrayList = new ArrayList<Device>();
        this.mDevId = id;
    }

    /**
     * Constructor for adding lists of commands and sub-devices.
     * @param commands List of commands to add
     * @param devices the List of Sub devices
     * @param id The Device Id
     */
    public Device(Collection<Command> commands, Collection<Device> devices, DeviceId id) throws Exception
    {
        this.mCommandMap = new LinkedHashMap<CommandId, Command>();
        this.mSubDevArrayList = new ArrayList<Device>();

        this.addCommands(commands);
        this.mSubDevArrayList.addAll(devices);
        this.mDevId = id;
    }

    /**
     * Constructor for adding lists of commands and sub-devices, with int device Id value
     * @param commands List of commands to add
     * @param devices the List of Sub devices
     * @param idVal The Device Id value
     */
    public Device(Collection<Command> commands, Collection<Device> devices, int idVal) throws Exception
    {
        this.mCommandMap = new LinkedHashMap<CommandId, Command>();
        this.mSubDevArrayList = new ArrayList<Device>();

        this.addCommands(commands);
        this.mSubDevArrayList.addAll(devices);
        this.mDevId = DeviceId.getDeviceId(idVal);
    }

    /*******************************
     * GETTERS
     ******************************/

    /**
     * gets the list of subdevices
     * @return
     */
    public ArrayList<Device> getSubDeviceList()
    {
        return this.mSubDevArrayList; /* list of subdevices */
    }

    /**
     * gets the device from the list of subdevices, based on the id value.
     * @return
     */
    public Device getSubDevice(int idVal)
    {
        for(Device dev : this.mSubDevArrayList)
        {
            if(dev.getDevId().getVal() == idVal)
            {
                return dev;/* returns the Device that matches */
            }
        }

        return null; /* no device exists with that id value */
    }

    /**
     * gets the device from the list of subdevices, based on the id.
     * @return
     */
    public Device getSubDevice(DeviceId id)
    {
        for(Device dev : this.mSubDevArrayList)
        {
            if(dev.getDevId() == id)
            {
                return dev;/* returns the Device that matches */
            }
        }

        return null; /* no device exists with that id value */
    }

    /**
     * gets the list of supported Commands
     * @return
     */
    public Map<CommandId, Command> getCommandSet()
    {
        return this.mCommandMap; /* list of commands */
    }

    /**
     * gets the Command from the list of commands, based on the id value.
     * @return
     */
    public Command getCommand(int idVal) throws Exception
    {
        if(this.mCommandMap.containsKey(CommandId.getCommandId(idVal)))
        {
            return this.mCommandMap.get(CommandId.getCommandId(idVal));//returns the Command
            // that matches
        }

        return null; /* no device exists with that id value */
    }

    /**
     * gets the Command from the list of Commands, based on the id.
     * @return
     */
    public Command getCommand(CommandId id)
    {
        if(this.mCommandMap.containsKey(id))
        {
            return this.mCommandMap.get(id);//returns the Command
            // that matches
        }
        return null; /* no device exists with that id */
    }

    /**
     * gets the Device Id.
     * @return
     */
    public DeviceId getDevId()
    {
        return this.mDevId; /* The Device Id */
    }

    /**
     * Sets the device Id.
     * @param id the Device ID
     */
    public void setDevId(DeviceId id)
    {
        this.mDevId = id;
    }

    /**
     * Sets the device Id, by the id Value.
     * @param idVal the Device Id Value
     */
    public void setDevId(int idVal) throws Exception
    {
        this.mDevId = DeviceId.getDeviceId(idVal);
    }

    /**
     * Added a command to the list of commands
     * @param cmd command to add to the devices available commands
     * @throws Exception if the command is already in the list you can't add it again.
     */
    public void addCommand(Command cmd) throws Exception
    {
        if(this.mCommandMap.containsKey(cmd.getCmdId()))
        {
            throw new Exception("Can't add command("+ cmd.getCmdId().name()
                    + ") It already is in the list.");

        }

        this.mCommandMap.put(cmd.getCmdId(), cmd);
    }

    /**
     * Adds the list of commands to the device.
     * @param cmds set of commands to add to the device.
     * @throws Exception if there already is a command Error.
     */
    public void addCommands (Collection<Command> cmds) throws Exception
    {
        for(Command tempCmd : cmds)
        {
            this.addCommand(tempCmd);
        }
    }

    /**
     * Added a device to the list of subdevices
     * @param dev the sub device
     */
    public void addSubDevice(Device dev)
    {
        this.mSubDevArrayList.add(dev);
    }

    /**
     * Adds a collection of Sub devices to the device.
     * @param devices to add as subdevices.
     */
    public void addAllSubDevice(Collection<Device> devices)
    {
        for(Device dev : devices)
        {
            this.addSubDevice(dev);
        }
    }


}
