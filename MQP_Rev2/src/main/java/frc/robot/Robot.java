/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2020 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;
import java.util.*;
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
  private final Color testList[][] = {{Color.YELLOW, Color.BLUE, Color.RED, Color.BLACK, Color.BLACK},
                                    {Color.YELLOW, Color.BLUE, Color.RED, Color.BLACK, Color.BLACK},
                                    {Color.YELLOW, Color.BLUE, Color.RED, Color.BLACK, Color.BLACK},
                                    {Color.YELLOW, Color.BLUE, Color.RED, Color.BLACK, Color.BLACK},
                                    {Color.YELLOW, Color.BLUE, Color.RED, Color.BLACK, Color.BLACK}};
  private int statenum = 0;
  private enum RobotState {INIT, Y_Traversal, X_Traversal, paint, iterate_color};
  private RobotState robotState;
  public int wallLength, wallHeight;
  public Color previousColor, nextColor;
  public int currentPosition[] = new int[2];
  public int nextPosition[] = new int[2];
  public Color paintPath[][] = testList;
  public boolean moveY, moveL, readyToPaint;
  public double startTime;
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
    statenum = 0;
    brush.init();
    timer.start();
    yTrav.resetEnc();
    xTrav.resetEnc();
    robotState = RobotState.INIT;
      
  }

  @Override
  public void autonomousPeriodic() {
    /*switch (this.robotState) {
      case INIT:
        wallLength = paintPath[0].length;
        wallHeight = paintPath.length;
        currentPosition[0] = 0;
        currentPosition[1] = 0;
        nextColor = paintPath[currentPosition[0]][currentPosition[1]];
        previousColor = Color.RED; 
        robotState = RobotState.paint;
      break;

      case Y_Traversal:
        System.out.println("case Y Traversal");
        if(timer.get() < startTime + 4){
          yTrav.setSpeed(1);
        }
        else{
          yTrav.setSpeed(0.0);
          readyToPaint = true;
          robotState = RobotState.paint;
        }
      break;

      case X_Traversal:
        System.out.println("case X Traversal");
        if(timer.get() < startTime + 2){
          xTrav.setSpeed(.5);
        }
        else{
          xTrav.setSpeed(0.0);
          readyToPaint = true;
          robotState = RobotState.paint;
        }
      break;

      case paint:
        System.out.println("case paint");
        brush.update(previousColor, nextColor, readyToPaint);
        readyToPaint = false;
        robotState = RobotState.iterate_color;
      break;

      case iterate_color:
        System.out.println("case iterate color");
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
        previousColor = nextColor;
        nextColor = paintPath[currentPosition[0]][currentPosition[1]];
        System.out.println("next color:" + nextColor);
        System.out.println("current position" + currentPosition);
        if(moveY){
          startTime = timer.get();
          robotState = RobotState.Y_Traversal;
        }
        else{
          startTime = timer.get();
          robotState = RobotState.X_Traversal;
        }
      break;


    }*/
  }

  @Override
  public void teleopInit() {
    statenum = 0;
    brush.init();
    timer.start();
    yTrav.resetEnc();
    xTrav.resetEnc();
    robotState = RobotState.INIT;
  }

  @Override
  public void teleopPeriodic() {
    // System.out.println("Y Encoder Distance " + yTrav.getEncPosition());
    //System.out.println("X Encoder Distance " + xTrav.getEncPosition());
    // System.out.println("Y ToF Distance " + yTrav.getToFPosition());
    //  System.out.println("Paint Selector Limit " + brush.getSelectorSwitch());
    //  System.out.println("Paint Trigger Button " + brush.getTriggerBtn());
    //  System.out.println("Current Color " + brush.currentColor);
     
    /*switch (this.robotState) {
      case INIT:
        wallLength = paintPath[0].length;
        wallHeight = paintPath.length;
        currentPosition[0] = 0;
        currentPosition[1] = 0;
        nextColor = paintPath[currentPosition[0]][currentPosition[1]];
        previousColor = Color.RED; 
        robotState = RobotState.paint;
        startTime = timer.get();
      break;

      case Y_Traversal:
        System.out.println("case Y Traversal");
        System.out.println("timer:" + timer.get());
        System.out.println("start time:" + startTime);
        if(timer.get() < (startTime + 4)){
          yTrav.setSpeed(1);
        }
        else{
          yTrav.setSpeed(0.0);
          readyToPaint = true;
          robotState = RobotState.paint;
        }
      break;

      case X_Traversal:
        System.out.println("case X Traversal");
        if(timer.get() < startTime + 2){
          xTrav.setSpeed(.5);
        }
        else{
          xTrav.setSpeed(0.0);
          readyToPaint = true;
          robotState = RobotState.paint;
        }
      break;

      case paint:
        System.out.println("case paint");
        brush.update(previousColor, nextColor, readyToPaint);
        readyToPaint = false;
        robotState = RobotState.iterate_color;
      break;

      case iterate_color:
        System.out.println("case iterate color");
        // if current x is at either end of the wall
        // iterate Y direction 
        if(currentPosition[0] == wallLength || currentPosition[0] == 0){
          currentPosition[1] = (currentPosition[1] + 1);
          moveY = true;
          if(moveL){
          moveL = false;}
          else{
          moveL = true;}
        }
        // iterate X direction 
        else{
          if(!moveL){
            currentPosition[0] = (currentPosition[0] + 1);
          }
          else{
            currentPosition[0] = (currentPosition[0] - 1);
          }
          moveY = false;
        }
        previousColor = nextColor;
        nextColor = paintPath[currentPosition[0]][currentPosition[1]];
        System.out.println("next color:" + nextColor);
        System.out.println("current position" + currentPosition);
        if(moveY){
          startTime = timer.get();
          robotState = RobotState.Y_Traversal;
        }
        else{
          startTime = timer.get();
          robotState = RobotState.X_Traversal;
        }
      break;
    }*/
    readyToPaint = true;
    brush.update(Color.YELLOW, Color.RED, readyToPaint);
    brush.update(Color.RED, Color.BLACK, readyToPaint);
    brush.update(Color.BLACK, Color.BLUE, readyToPaint);
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
