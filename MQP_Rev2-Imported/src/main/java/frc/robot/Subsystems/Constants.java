/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.Subsystems;

import static edu.wpi.first.wpilibj.SerialPort.Port.kMXP;

import edu.wpi.first.wpilibj.SerialPort;

//TODO: Fill in these ports when known
public class Constants {

// ----X-Traversal--------------------------------------------------------------
    // SRX ID
    public static final int k_XTraversalPort = 0;
    // X Encoder DIO Pins
    public static final int k_EncXPort1 = 4;
    public static final int k_EncXPort2 = 5;
    // Encoder conversion factor in inches per pulse
    public static final float k_EncXConversion = 0.23f;
    // Encoder min rate in X direction
    public static final double k_EncXMinRate = 0.125;
    // Encoder is reversed or not
    public static final boolean k_EncXReverse = true;
    // Tolerance
    public static final double k_ToleranceX = 2000.0;

    //Tof I2C Address
    public static final int k_ToFAddress = 0x10;

    //PID Constants
    public static final int k_IDX = 0;
    public static final int k_TimeoutMs = 0;

    public static final double k_xP = 1.00;
    public static final double k_xI = 0.000;
    public static final double k_xD = 0.0;
    public static final double k_xF = 0.0; 
    
// ----Y-Traversal--------------------------------------------------------------
    public static final int k_YTraversalPort = 1;
    // Y ToF Sensor
    public static final SerialPort.Port k_YToFSerialPort = kMXP;
    //Y Encoder DIO Pins
    public static final int k_EncYPort1 = 2;
    public static final int k_EncYPort2 = 3;
        //Encoder conversion factor in inches per pulse
        public static final float k_EncYConversion = 0.00045024f; 
        //Encoder min rate in X direction
        public static final double k_EncYMinRate = 0.125; 
        //Encoder is reversed or not
        public static final boolean k_EncYReverse = false;
        //Tolerance
        public static final double k_ToleranceY = 0.1;
    public static final int k_upSwitchPort = 0;

//----PAINT SELECTOR--------------------------------------------------------------
    //Constant int for Spark Channel
    public static final int k_EncPaint1 = 7;
    public static final int k_EncPaint2 = 8;
        //Port for accompanying limit switch
        public static final int k_PaintSelectorPort = 1;
        //Speeds for single turn method
        //Encoder conversion factor in inches per pulse
        public static final float k_EncPaintConversion = .125f; 
        //Encoder min rate in X direction
        public static final double k_EncPaintMinRate = 0.125; 
        //Encoder is reversed or not
        public static final boolean k_EncPaintReverse = false;
        //Tolerance
        public static final double k_TolerancePaint = 0.1;
        public static final double SELECTOR_CW_SPEED = 0.5;
        public static final double SELECTOR_CCW_SPEED = -0.5;

    //Constant int for VexMotor Channel
    //  Controls Paint Trigger
    public static final int k_PaintTriggerPort = 2;
        //Speed for painting 
        public static final double k_PaintTriggerSpeed = 0.25;
        //Static times for paint and reset 
        public static final double k_PaintingTime = 0.24; //In Seconds
        public static final double k_ResetTime = 0.1; //In Seconds
        //Paint Trigger Button
        public static final int k_PaintTriggerBtnPort = 6;

//----VEX BUTTON---------------------------------------------------------------------
    //Button DIO Port
    //public static final int k_VexBtnPort = 0;

//-----------------------------------------------------------------------------------
    public static final double k_CMtoIn = 1/2.54;

    

}