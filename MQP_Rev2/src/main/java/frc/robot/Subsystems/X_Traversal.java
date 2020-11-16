/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.Subsystems;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.*;

public class X_Traversal extends SubsystemBase {
  private final Encoder EncX = new Encoder(Constants.k_EncXPort1, Constants.k_EncXPort2);
  private final TalonSRX m_X = new TalonSRX(Constants.k_YTraversalPort);
  /**
   * Creates a new X_Traversal.
   */
  public X_Traversal() {
    m_X.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    EncX.setDistancePerPulse(Constants.k_EncXConversion);
    EncX.setMinRate(Constants.k_EncXMinRate);
    EncX.setReverseDirection(Constants.k_EncXReverse);
  }

  public void init(){
    //Any initialization that may need to be repeated, ie. should not be called in constructor
  }

  public void setPositionClosedLoopSetpoint(double setpoint){
    m_X.set(ControlMode.Position, setpoint);

  }

  public void setSpeed(double speed){
    m_X.set(ControlMode.PercentOutput, speed);
  }

  public void resetEnc(){
    EncX.reset();
  }

  public double getEncPosition() {
    return this.EncX.getDistance();
  }

}
