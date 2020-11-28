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
    public static final float k_EncXConversion = 0.14113f;
    // Encoder min rate in X direction
    public static final double k_EncXMinRate = 0.125;
    // Encoder is reversed or not
    public static final boolean k_EncXReverse = true;
    // ----Y-Traversal--------------------------------------------------------------
    public static final int k_YTraversalPort = 1;
    // Y ToF Sensor
    public static final SerialPort.Port k_YToFSerialPort = kMXP;
    //Y Encoder DIO Pins
    public static final int k_EncYPort1 = 2;
    public static final int k_EncYPort2 = 3;
        //Encoder conversion factor in inches per pulse
        public static final float k_EncYConversion = 0.00041024f; 
        //Encoder min rate in X direction
        public static final double k_EncYMinRate = 0.125; 
        //Encoder is reversed or not
        public static final boolean k_EncYReverse = false;

//----PAINT SELECTOR--------------------------------------------------------------
    //Constant int for Spark Channel
    public static final int k_PaintSelectorPort = 1;
        //Port for accompanying limit switch
        public static final int k_PaintSelectorLimitPort = 1;
        //Speeds for single turn method
        public static final double SELECTOR_CW_SPEED = 0.5;
        public static final double SELECTOR_CCW_SPEED = -0.5;

    //Constant int for VexMotor Channel
    //  Controls Paint Trigger
    public static final int k_PaintTriggerPort = 0;
        //Speed for painting 
        public static final double k_PaintTriggerSpeed = 1.0;
        //Time to paint for
        public static final double k_PaintingTime = 0.25; //In Seconds
        //Paint Trigger Button
        public static final int k_PaintTriggerBtnPort = 6;

//----VEX BUTTON---------------------------------------------------------------------
    //Button DIO Port
    public static final int k_VexBtnPort = 0;
    

    

}