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
    INIT, IDLE, SET_POSITIONS, WAIT_FOR_ALIGNMENT, UPDATE_BRUSH, WAIT_FOR_TIME
  }

  private static MainState state, nextState;
  private static Color currentColor;

  private int nextColor, wallLength, wallHeight;
  private double startTime;
  public static int currentPosition[] = new int[2];
  public static int nextPosition[] = new int[2];
  private boolean moveY, moveL, xAligned, yAligned, readyToPaint;

  private final int[][] testGrid = { { 1, 3, 2, 4, 5 },
    {1, 2, 1, 2, 1},
    {2, 1, 2, 2, 3},
    {1, 2, 1, 1, 2},
    {2, 2, 3, 4, 1}}; 

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
    brush.init();
    timer.start();
    yTrav.resetEnc();
    xTrav.resetEnc();
    state = MainState.INIT;

    //Not quite sure how you handled this in yours so i just made it up for now
    final int[][] testGrid = { { 1, 3, 2, 4, 5 },
    {1, 2, 1, 2, 1},
    {2, 1, 2, 2, 3},
    {1, 2, 1, 1, 2},
    {2, 2, 3, 4, 1}}; 

    wallLength = testGrid[0].length;
    wallHeight = testGrid.length;
  }

  @Override
  public void teleopPeriodic() {
    // System.out.println("Y Encoder Distance " + yTrav.getEncPosition());
    System.out.println("X Encoder Distance " + xTrav.getEncPosition());
    // System.out.println("Y ToF Distance " + yTrav.getToFPosition());
    //  System.out.println("Paint Selector Limit " + brush.getSelectorSwitch());
    //  System.out.println("Paint Trigger Button " + brush.getTriggerBtn());
    //  System.out.println("Current Color " + brush.currentColor);
     
    switch(Robot.state){
      case INIT:
      state = MainState.IDLE;
      break;

      case IDLE:
        // if current x is at either end of the wall
        // iterate Y direction 
        if(Robot.currentPosition[0] == wallLength || Robot.currentPosition[0] == 0){
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
            Robot.nextPosition[0] = Robot.nextPosition[0] + 1;
          }
          else{
            Robot.nextPosition[0] = Robot.nextPosition[0] - 1;
          }
          moveY = false;
        }

        currentColor = currentColor.set(this.testGrid[currentPosition[0]][currentPosition[1]]);
        Robot.state = MainState.SET_POSITIONS;
      break;

      case SET_POSITIONS:
      //This one will need to be changed based on how were counting the distance, currently the encoder gets distance in inches
        xTrav.setPositionClosedLoopSetpoint(Robot.nextPosition[0] * 1500);
        startTime = timer.get();
        if(moveY){
          yTrav.setSpeed(1.0);
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
          readyToPaint = true;
          nextState = MainState.IDLE;
          state = MainState.UPDATE_BRUSH;
          Robot.currentPosition = Robot.nextPosition;
        }
        else{
          readyToPaint = false;
          nextState = MainState.WAIT_FOR_ALIGNMENT;
          state = MainState.UPDATE_BRUSH;
        }
        
      break;

      case UPDATE_BRUSH:
        brush.update(currentColor, readyToPaint);
        if(readyToPaint && !Brush.finishedPainting){
          state = MainState.UPDATE_BRUSH;
        }
        else{ 
          state = nextState;
        }
        
      break;

      case WAIT_FOR_TIME:
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
