/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.Subsystems;

import javax.lang.model.util.ElementScanner6;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.ControlMode;
import java.lang.Math.*;
public class Brush extends SubsystemBase {
  Timer brushTimer = new Timer();
  Spark m_paintSelector = new Spark(Constants.k_PaintSelectorPort);
  VictorSP m_paintTrigger = new VictorSP(Constants.k_PaintTriggerPort);
  //DigitalInput paintSelctorLimit = new DigitalInput(Constants.k_PaintSelectorLimitPort);
  DigitalInput paintTriggerBtn = new DigitalInput(Constants.k_PaintTriggerBtnPort);
  public final Encoder EncPaint = new Encoder(Constants.k_EncPaint1, Constants.k_EncPaint2);
  /**
   * Creates a new Brush.
   */
  public Brush() {
    brushState = BrushState.INIT;
    EncPaint.setDistancePerPulse(Constants.k_EncPaintConversion);
    EncPaint.setReverseDirection(true);
  }

  public int currentColor, nextColor;
  private double paintStartTime, lastTime, resetStartTime, waitStartTime, waitTime;
  private boolean isDoneSelecting, countUp, lastSwitchState;
  public static boolean finishedPainting = false;

  private enum BrushState {
    INIT, IDLE, PAINTING, SELECTING_COLOR, WAIT_FOR_COLOR, WAIT_FOR_PAINT, WAIT_FOR_RESET, WAIT_FOR_TIME
  }

  private BrushState brushState, nextBrushState;

  public void init(){
    //Any initialization that may need to be repeated, ie. should not be called in constructor
    brushState = BrushState.INIT;
  }

  // Spin Paint Selector CW at Constant Speed
  public void spinSelectorCW() {
    m_paintSelector.set(Constants.SELECTOR_CW_SPEED);
  }

  // Spin Paint Selector CCW at Constant Speed
  public void spinSelectorCCW() {
    m_paintSelector.set(Constants.SELECTOR_CCW_SPEED);
  }

  // Spin Paint Selector CCW at Constant Speed
  public void spinSelectorOff() {
    m_paintSelector.set(0.0);
  }

  // Checks Limit Switch to see if Geneva Mechanism is aligned
  /*public boolean getSelectorSwitch() {
    return !paintSelctorLimit.get();
  }*/

  // Checks Limit Switch to see if trigger is l;ined up correctly
  public boolean getTriggerBtn() {
    return !paintTriggerBtn.get();
  }

  //Run Paint Sprayer
  public void paintForTime() {
    m_paintTrigger.set(Constants.k_PaintTriggerSpeed);
  }

  //Retract Paint Actuator by runnning in reverse
  public void triggerReset() {
    m_paintTrigger.set(-Constants.k_PaintTriggerSpeed);
  }

  //Turn off paint actuator
  public void stopPainting() {
    m_paintTrigger.set(0.0);
  }

  //Returns encoder value
  public double getEncPosition(){
    return this.EncPaint.getDistance();
  }

  //Resets Encoder to 0
  public void resetEnc(){
    EncPaint.reset();
  }

  //Return true if robot position error is within tolerance
  public boolean atPosition(int position){
    if(Math.abs(position - getEncPosition()) < Constants.k_TolerancePaint){
      return true;
    }
    else{
      return false;
    }
  }

  //State Machine, must be updated constantly in robot.java
  public void update(Color previousColor, Color color, boolean readyToPaint) {
    //System.out.println("previous color" + previousColor + "current color: " + color);
    switch (this.brushState) {
    
    //Init Case to be run once
    //Starts timer and initializes color and switch
    case INIT:
      brushTimer.start();
      if(this.currentColor != color.colorVal){
        this.currentColor = previousColor.colorVal;
      }
      System.out.println("init");
      brushState = BrushState.IDLE;
      this.lastSwitchState = true;
    break;

    //IDLE Case. Used to guide state machine based on input form main loop in robot.java
    //Handles loop flow based on desired/actual color and readiness to paint a dot
    case IDLE:

      if(Brush.finishedPainting){
        Brush.finishedPainting = false;
      }

      if (color.colorVal == this.currentColor && readyToPaint){
        brushState = BrushState.WAIT_FOR_TIME;
        this.waitStartTime = brushTimer.get();
        this.waitTime = 0.25;
        nextBrushState = BrushState.PAINTING;
      }
      else if (color.colorVal != this.currentColor && color.colorVal != 0){
        brushState = BrushState.SELECTING_COLOR;
      }    
      else
        brushState = BrushState.IDLE;
        // if(color.colorVal == this.currentColor){
        //   System.out.println("Correct Color, Waiting for X/Y");
        // }

        // if(readyToPaint){
        //   System.out.println("Correct Pos, Waiting for Selector");
        // }

    break;

    //Triggers the paint can and records the start time
    //Sets state to WAIT_FOR_PAINT when finished   
    case PAINTING:
      Brush.finishedPainting = false;    
      paintStartTime = brushTimer.get();
      this.paintForTime();
      brushState = BrushState.WAIT_FOR_PAINT;
      //System.out.println("BEGIN PAINTING PROCEDURE");
    break;
    
    //Starts the color selector moving the ideal direction
    //Sets state to WAIT_FOR_COLOR when finished 
    case SELECTING_COLOR:
      //Optimization to decide whether to turn right or left, pretty cool how simple it is
      //System.out.println("current color: " + this.currentColor + " desired color: " + color.colorVal);

      if(this.currentColor <= 4){
        if(color.colorVal <= (this.currentColor + 4) && color.colorVal > this.currentColor){
          this.spinSelectorCCW();
          //System.out.println("ccw");
          countUp = true; //If turning counter-clockwise then increment downwards at UPDATE
          brushState = BrushState.WAIT_FOR_COLOR;
        }
        else{
          this.spinSelectorCW();
          //System.out.println("cw");
          countUp = false; //If turning clockwise then increment upwards at UPDATE
          brushState = BrushState.WAIT_FOR_COLOR;
        }
      }
      else{
        if(this.currentColor > color.colorVal && (this.currentColor - 3) <= color.colorVal){
          this.spinSelectorCW();
          //System.out.println("cw");
          countUp = false; //If turning clockwise then increment upwards at UPDATE
          brushState = BrushState.WAIT_FOR_COLOR;
        }
        else{
          this.spinSelectorCCW();
          //System.out.println("ccw");
          countUp = true; //If turning counter-clockwise then increment downwards at UPDATE
          brushState = BrushState.WAIT_FOR_COLOR;
        }
      }
    break;


    //Waits for Paint Selector to reach desired color
    case WAIT_FOR_COLOR:
      // System.out.println("current color wait for color" + this.currentColor);
      System.out.println("encoder position" + this.getEncPosition());
      if(Math.abs(this.getEncPosition()) > 0.975 && Math.abs(this.getEncPosition()) < 1.025){
        this.resetEnc();
        //Positive
        if(countUp){
          if(this.currentColor + 1 > 8){
            this.currentColor = 1;
          }
          else{
            this.currentColor += 1;
          }
        }
        //Negative
        else{
          if(this.currentColor - 1 < 1){
            this.currentColor = 8;
          }
          else{
            this.currentColor -= 1;
          }
        }
        //System.out.println("Last Switch State " + this.lastSwitchState);
      }

      if(this.currentColor == color.colorVal){
        this.spinSelectorOff();
        brushState = BrushState.IDLE;
      }
    break;

    //Waits designated amount of time for paint application to finish,
    //stops paint trigger motor, and resets the active color to none.
    //Returns to IDLE when finished
    case WAIT_FOR_PAINT:
     // System.out.println("WAITING FOR PAINTING TO FINISH");
      if(brushTimer.get() - paintStartTime >= Constants.k_PaintingTime){ 
        this.triggerReset();
        //TODO: this.currentColor = Color.NONE.colorVal;
        brushState = BrushState.WAIT_FOR_RESET;
      }
    break;

    //Waits for the paint sprayer to retract before moving back to IDLE
    case WAIT_FOR_RESET:
      //System.out.println("WAITING FOR RESET TO FINISH");
      //System.out.println(getTriggerBtn());
      if(getTriggerBtn()){
        this.stopPainting();

        //TODO: this.currentColor = Color.NONE.colorVal;
        brushState = BrushState.IDLE;
        Brush.finishedPainting = true;
      }
    break;

    //Generic Wait for Time using 3rd var nextBrushState
    case WAIT_FOR_TIME:
      if(brushTimer.get() - waitStartTime > waitTime)
      {
        brushState = nextBrushState;
      }
    break;  
    }
  }
}
