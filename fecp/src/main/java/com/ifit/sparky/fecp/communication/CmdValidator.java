/**
 * This class will handle whether the command is valid or not.
 * @author Levi.Balling
 * @date 6/11/2014
 * @version 1
 * This .
 */
package com.ifit.sparky.fecp.communication;

import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.interpreter.device.Device;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import java.util.ArrayList;
import java.util.List;

public class CmdValidator {

    public static boolean ValidateDevice(SystemDevice dev, DeviceId id)
    {
        //recursively check all of the devices in the system device. and check if there are any sub devices
        List<DeviceId> allDevIds = getAllSubDevices(dev);
        return allDevIds.contains(id);
    }

    private static List<DeviceId> getAllSubDevices(Device dev)
    {
        ArrayList<DeviceId> idList = new ArrayList<DeviceId>();

        for (Device device : dev.getSubDeviceList()) {
            idList.addAll(getAllSubDevices(device));
        }

        idList.add(dev.getInfo().getDevId());
        return idList;
    }



}
