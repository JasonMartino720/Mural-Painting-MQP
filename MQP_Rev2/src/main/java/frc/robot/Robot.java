/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2020 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import javax.lang.model.util.ElementScanner6;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.RobotController;
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

  //Enums for main state machine
  private enum MainState {
    INIT, IDLE, SET_POSITIONS, WAIT_FOR_ALIGNMENT, UPDATE_BRUSH, WAIT_FOR_TIME, END
  }

  private static MainState state, nextState, postWaitState;
  private static Color previousColor, currentColor;

  private int nextColor, wallLength, wallHeight, wallEnd;
  private double startTime, delayTime, waitStartTime, waitTime;
  public static int currentPosition[] = new int[2];
  public static int nextPosition[] = new int[2];
  private boolean moveY, moveL, xAligned, yAligned, readyToPaint;

  private final int[][] testGrid = { { 5, 3, 1, 7, 5 },
                                      {3, 1, 7, 3, 5},
                                      {3, 1, 7, 3, 5},
                                      {1, 2, 1, 1, 2},
                                      {2, 2, 3, 4, 1}}; 
  
  private int _loops = 0;

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

    brush.init();
    timer.start();
    yTrav.resetEnc();
    xTrav.resetEnc();
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
    xTrav.updatePositionValue();
    brush.init();
    timer.start();
    yTrav.resetEnc();
    xTrav.resetEnc();
    state = MainState.INIT;
    currentColor = Color.RED;

    wallLength = testGrid[0].length;
    wallHeight = testGrid.length;
    if (wallLength % 2 == 1){
      wallEnd = wallLength - 1;
    }
    else{
      wallEnd = 0;
    }
  }

  @Override
  public void teleopPeriodic() {
     /*if (++_loops >= 10) {
			_loops = 0;
      System.out.println("X Encoder Distance " + xTrav.getEncPosition());
      System.out.println("X Encoder Raw " + xTrav.EncX.getRaw());
      System.out.println("X PID Error " + xTrav.m_X.getClosedLoopError());
      System.out.println("Y Encoder Distance " + yTrav.getEncPosition());
      // System.out.println("Y ToF Distance " + yTrav.getToFPosition());
      //  System.out.println("Paint Selector Limit " + brush.getSelectorSwitch());
      //  System.out.println("Paint Trigger Button " + brush.getTriggerBtn());
      System.out.println("Current Color " + currentColor);
     }*/
     // System.out.println("Current state " + Robot.state + " next state: " + nextState + " ready to paint: " + readyToPaint + " finished painting: " + Brush.finishedPainting);
    xTrav.updatePositionValue();
    //System.out.println("state: " + Robot.state + " next state: " + Robot.nextState);
    switch(Robot.state){
      case INIT:
        System.out.println("main init");
        moveL = false;
        moveY = true;
        readyToPaint = true;
        previousColor = Color.RED;
        nextState = MainState.IDLE;
        currentColor = currentColor.set(this.testGrid[Robot.nextPosition[1]][Robot.nextPosition[0]]);
        Robot.state = MainState.UPDATE_BRUSH;
      break;

      case IDLE:
        // if current x is at either end of the wall
        // iterate Y direction 
        System.out.println("main init");
        System.out.println("current position" + "x" + Robot.currentPosition[0] + " y " + Robot.currentPosition[1]);
        
        System.out.println("current color: " + currentColor);
        // make sure it goes to the side to start
        if (Robot.currentPosition[1] == wallHeight && Robot.currentPosition[0] == wallEnd){
          Robot.state = MainState.END;
          break;
        }
        if((Robot.currentPosition[0] == (wallLength - 1) || Robot.currentPosition[0] == 0) && !moveY){
          Robot.nextPosition[1] = Robot.currentPosition[1] + 1;
          moveY = true;
          
          if(moveL){
            moveL = false;
          }
          else{
            moveL = true;
          }
        }
        // iterate X direction 
        else{
          if(!moveL){
            Robot.nextPosition[0] = Robot.currentPosition[0] + 1;
          }
          else{
            Robot.nextPosition[0] = Robot.currentPosition[0] - 1;
          }
          moveY = false;
        }
        System.out.println("nextposition" + "x" + Robot.nextPosition[0] + " y " + Robot.nextPosition[1]);
        previousColor = currentColor;
        currentColor = currentColor.set(this.testGrid[Robot.nextPosition[1]][Robot.nextPosition[0]]);

        Robot.state = MainState.SET_POSITIONS;
      break;

      case SET_POSITIONS:
      //This one will need to be changed based on how were counting the distance, currently the encoder gets distance in inches
        
        xTrav.setPositionClosedLoopSetpoint(Robot.nextPosition[0] * 1.5);
        startTime = timer.get();
        if(moveY){
          yTrav.setSpeed(-1.0);
        }
        state = MainState.WAIT_FOR_ALIGNMENT;
      break;

      case WAIT_FOR_ALIGNMENT:
        if(yTrav.atPosition()){
          yTrav.setSpeed(0.0);
          yAligned = true;
        }
        else {
          yAligned = false;
        }

        if(xTrav.atPosition()){
          xAligned = true;
        }
        else {
          xAligned = false;
        }

        if(yAligned && xAligned){
          
          nextState = MainState.IDLE;
          postWaitState = MainState.UPDATE_BRUSH;
          state = MainState.WAIT_FOR_TIME;
          Robot.currentPosition = Robot.nextPosition;
          waitStartTime = timer.get();
          waitTime = 2.0;
        }
        else{
          readyToPaint = false;
          nextState = MainState.WAIT_FOR_ALIGNMENT;
          state = MainState.UPDATE_BRUSH;
        }
        
      break;

      case UPDATE_BRUSH:
        brush.update(previousColor, currentColor, readyToPaint);
        // System.out.println(previousColor + "  " + currentColor);
        if(readyToPaint && !Brush.finishedPainting){
          state = MainState.UPDATE_BRUSH;
        }
        else{ 
          state = nextState;
        }
        
      break;

      case WAIT_FOR_TIME:
        System.out.println("wait");
        if(timer.get() - waitStartTime > waitTime)
        {
          state = postWaitState;
        }
      break;

      case END:
        if (++_loops >= 10) {
          _loops = 0;
          System.out.println("Done");
        }
        state = MainState.END;
      break;
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
