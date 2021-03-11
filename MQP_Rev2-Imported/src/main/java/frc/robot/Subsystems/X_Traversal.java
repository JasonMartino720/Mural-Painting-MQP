/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.Subsystems;

import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj2.command.Subsystem;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.*;

public class X_Traversal {
  public final Encoder EncX = new Encoder(Constants.k_EncXPort1, Constants.k_EncXPort2, Constants.k_EncXReverse, CounterBase.EncodingType.k4X);
  public final TalonSRX m_X = new TalonSRX(Constants.k_XTraversalPort);
  public final I2C ToF = new I2C(I2C.Port.kOnboard, Constants.k_ToFAddress);
  public LidarProxy ToFSerial= new LidarProxy(SerialPort.Port.kMXP);
  // private final PIDController PID_X = new PIDController(Constants.k_xP, Constants.k_xI, Constants.k_xD, Constants.k_xF, EncX, m_X);
  /**
   * Creates a new X_Traversal with necessary motor and encoder configuration
   */
  public X_Traversal() {
    m_X.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    EncX.setDistancePerPulse(Constants.k_EncXConversion);
    // EncX.setMinRate(Constants.k_EncXMinRate);
    EncX.setReverseDirection(Constants.k_EncXReverse);
    this.configPID();
  }
  
  public void init(){
    //Any initialization that may need to be repeated, ie. should not be called in constructor

  }

  //Configures PID constants
  public void configPID(){
    m_X.configNominalOutputForward(0, Constants.k_TimeoutMs);
    m_X.configNominalOutputReverse(0, Constants.k_TimeoutMs);
    m_X.configPeakOutputForward(0.45, Constants.k_TimeoutMs);
    m_X.configPeakOutputReverse(-0.45, Constants.k_TimeoutMs);
    
    //m_X.configAllowableClosedloopError(0, Constants.k_IDX, Constants.k_TimeoutMs);

    m_X.config_kF(Constants.k_IDX, Constants.k_xF, Constants.k_TimeoutMs);
    m_X.config_kP(Constants.k_IDX, Constants.k_xP, Constants.k_TimeoutMs);
    m_X.config_kI(Constants.k_IDX, Constants.k_xI, Constants.k_TimeoutMs);
    m_X.config_kD(Constants.k_IDX, Constants.k_xD, Constants.k_TimeoutMs);

    m_X.configOpenloopRamp(2.0);

    m_X.setSelectedSensorPosition(this.getEncPosition());
  }

  //Begins Closed Loop Control
  public void setPositionClosedLoopSetpoint(final double setpoint) {
    m_X.set(ControlMode.Position, 1000 * setpoint);
    //System.out.println(this.getEncPosition());
  }

  //Updates the sensor value to the value of the X encoder
  public void updatePositionValue(){
    m_X.setSelectedSensorPosition(this.getEncPosition());
  }

  //Open Loop Set Speed
  public void setSpeed(final double speed) {
    m_X.set(ControlMode.PercentOutput, speed); 
  }

  //Set Enc to 0
  public void resetEnc(){
    EncX.reset();
  }

  //Returns encoder position in inches * 1000
  public int getEncPosition() {
    //System.out.println(this.EncX.getRaw() + "raw");
    return (int) (1000 * this.EncX.getDistance());
    //return (int)(1000 * this.EncX.getRaw() / 8.6);
    
  }

  //Returns true if position error is less than tolerance
  public boolean atPosition() {
    return m_X.getClosedLoopError() < Constants.k_ToleranceX;
  }

  //Returns ToF distance reading which is absolute not relative
  public double getAbsPosition() {
    // byte[] buffer = new byte[9];
    // ToF.read(0x01, 9, buffer);
    // System.out.println("Full Buffer " + buffer);
    // double distCM = buffer[2] << 8 + buffer[3];
    double distCM = ToFSerial.get();
    System.out.println("Dist in CM "  + distCM);

    double distIn = distCM * Constants.k_CMtoIn;
    System.out.println("Dist in In " + distIn);

    return distIn; 

  }
  
}
