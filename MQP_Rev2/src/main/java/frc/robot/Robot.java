/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2020 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.Subsystems.*;
import edu.wpi.first.wpilibj.Timer;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  //Instantiate Subsystems
  private final X_Traversal xTrav = new X_Traversal();
  private final Y_Traversal yTrav = new Y_Traversal();
  private final Brush brush = new Brush();
  private final DigitalInput btn = new DigitalInput(Constants.k_VexBtnPort);
  private final Timer timer = new Timer();

  private int statenum = 0;

  @Override
  public void robotInit() {
    brush.init();
    yTrav.init();
    xTrav.init();
  }

  @Override
  public void robotPeriodic() {
  }

  @Override
  public void autonomousInit() {
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
    statenum = 0;
    timer.start();
    yTrav.resetEnc();
    xTrav.resetEnc();
  }

  @Override
  public void teleopPeriodic() {
    // System.out.println("Y Encoder Distance " + yTrav.getEncPosition());
    // System.out.println("X Encoder Distance " + xTrav.getEncPosition());
    // System.out.println("Y ToF Distance " + yTrav.getToFPosition());
     System.out.println("Paint Selector Limit " + brush.getSelectorSwitch());
     System.out.println("Current Color " + brush.currentColor);

    brush.update(Color.BLACK, false);
    
    if(!btn.get())
    {
      yTrav.resetEnc();
      xTrav.resetEnc();
      timer.reset();
      this.statenum = 0;
    }
  }

  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
    xTrav.setSpeed(0.0);
    yTrav.setSpeed(0.0);
    brush.spinSelectorOff();
    brush.stopPainting();
  }

  @Override
  public void testInit() {
  }

  @Override
  public void testPeriodic() {
  }

}
