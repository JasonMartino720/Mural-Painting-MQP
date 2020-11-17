/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.Subsystems;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.can.*;
import com.ctre.phoenix.motorcontrol.*;


public class Y_Traversal extends SubsystemBase {
  private final Encoder EncY = new Encoder(Constants.k_EncYPort1, Constants.k_EncYPort2);
  private final TalonSRX m_Y = new TalonSRX(Constants.k_YTraversalPort);
  private final LidarProxy Y_ToF = new LidarProxy(Constants.k_YToFSerialPort);
  /**
   * Creates a new X_Traversal.
   */
  public Y_Traversal() {
    m_Y.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    EncY.setDistancePerPulse(Constants.k_EncYConversion);
    EncY.setMinRate(Constants.k_EncYMinRate);
    EncY.setReverseDirection(Constants.k_EncYReverse);
  }

  public void init(){
    //Any initialization that may need to be repeated, ie. should not be called in constructor
  }
  
  public void setPositionClosedLoopSetpoint(double setpoint){
    m_Y.set(ControlMode.Position, setpoint);
  }

  public void setSpeed(double speed){
    m_Y.set(ControlMode.PercentOutput, speed);
  }

  public double getEncPosition() {
    return this.EncY.getDistance();
  }

  public void resetEnc(){
    EncY.reset();
  }

  public double getToFPosition(){
    return Y_ToF.get();
  }


}
