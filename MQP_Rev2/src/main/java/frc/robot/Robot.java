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
  private final int[][] testList = {{1, 3, 2, 4, 5},
                                    {1, 2, 1, 2, 1},
                                    {2, 1, 2, 2, 3},
                                    {1, 2, 1, 1, 2},
                                    {2, 2, 3, 4, 1}}
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
  public void autonomousInit(int[][] paintPath) {
    statenum = 0;
    brush.init();
    timer.start();
    yTrav.resetEnc();
    xTrav.resetEnc();
    robotState = robotState.INIT;
    public int currentColor, nextColor, wallLength, wallHeight;
    public int[] currentPosition, nextPosition;
    public boolean moveY, moveL, readyToPaint;
    wallLength = paint_path[0].length;
    wallHeight = paint_path.length;
  }

  @Override
  public void autonomousPeriodic() {
    switch (this.robotState) {
      case Y_Traversal:
        if(timer.get() < startTime + 4){
          yTrav.setSpeed(1);
        }
        else{
          yTrav.setSpeed(0.0);
          readyToPaint = true;
          this.robotState = paint;
        }
      break;

      case X_Traversal:
        if(timer.get() < startTime + 2){
          xTrav.setSpeed(.5);
        }
        else{
          xTrav.setSpeed(0.0);
          readyToPaint = true;
          this.robotState = paint;
        }
      break;

      case paint:
        brush.update(currentColor, readyToPaint);
        readyToPaint = false;
        this.robotState = iterate_color;
      break;

      case iterate_color:
        // if current x is at either end of the wall
        // iterate Y direction 
        if(currentPosition[0] == wallLength || currentPosition[0] == 0){
          currentPosition[1] = currentPosition[1] + 1;
          moveY = true;
          if(moveL){
          moveL = false;}
          else{
          moveL = true;}
        }
        // iterate X direction 
        else{
          if(!moveL){
            currentPosition[0] = currentPosition[0] + 1;
          }
          else{
            currentPosition[0] = currentPosition[0] - 1;
          }
          moveY = false;
        }

        currentColor = paintPath[currentPosition[0]][currentPosition[1]];
        if(moveY){
          startTime = timer.get();
          this.robotState = Y_Traversal;
        }
        else{
          startTime = timer.get();
          this.robotState = X_Traversal;
        }
      break;


    }
  }

  @Override
  public void teleopInit() {
    statenum = 0;
    brush.init();
    timer.start();
    yTrav.resetEnc();
    xTrav.resetEnc();
  }

  @Override
  public void teleopPeriodic() {
    // System.out.println("Y Encoder Distance " + yTrav.getEncPosition());
    System.out.println("X Encoder Distance " + xTrav.getEncPosition());
    // System.out.println("Y ToF Distance " + yTrav.getToFPosition());
    //  System.out.println("Paint Selector Limit " + brush.getSelectorSwitch());
    //  System.out.println("Paint Trigger Button " + brush.getTriggerBtn());
    //  System.out.println("Current Color " + brush.currentColor);
     
    if(timer.get() < 2)
    {
      xTrav.setSpeed(-0.5);
    }
    else
    {
      xTrav.setSpeed(0.0);
    }

    // if(!btn.get())
    // {
    //   yTrav.resetEnc();
    //   xTrav.resetEnc();
    //   timer.reset();
    //   this.statenum = 0;
    // }
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
