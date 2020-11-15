/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.Subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SerialPort;

public class LidarProxy {
    private double lastReadDistance;
    private LidarListener _listener;
    private Thread _thread;
    private boolean _initializedProperly;

    public LidarProxy(SerialPort.Port port) {
        setup(port);
    }

    private void setup(SerialPort.Port port) {
        try {
            _listener = new LidarListener(this, port);
            _thread = new Thread(_listener);
            _thread.start();
            _initializedProperly = true;
        } catch (Exception e) {
            _initializedProperly = false;
           
        }
        //SmartDashboard.putBoolean("Lidar/initializedProperly", _initializedProperly);
    }

    public double get() {
        return lastReadDistance;
    }

    protected class LidarListener implements Runnable {
        private SerialPort _port;
        private LidarProxy _proxy;

        protected LidarListener(LidarProxy proxy, SerialPort.Port port) {
            _proxy = proxy;
            _port = new SerialPort(115200, port);
            //_port.setReadBufferSize(9);
        }

        public void run() {
            while (true) {
                try {
                    //SmartDashboard.putNumber("Lidar/_port.getBytesReceived()", _port.getBytesReceived());
                    byte[] read = _port.read(9);
                    //SmartDashboard.putNumber("Lidar/readLength", read.length);
                    //SmartDashboard.putNumber("Lidar/bytes/3", new Integer(read[2] & 0xFF));
                    _proxy.lastReadDistance = read[2] & 0xFF;
                } catch (Exception e) {
                    DriverStation.reportError("LidarListener exception: " + e.toString(), false);
                }
            }
        }
    }
}

