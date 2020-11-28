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
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Brush extends SubsystemBase {
  Timer brushTimer = new Timer();
  Spark m_paintSelector = new Spark(Constants.k_PaintSelectorPort);
  Talon m_paintTrigger = new Talon(Constants.k_PaintTriggerPort);
  DigitalInput paintSelctorLimit = new DigitalInput(Constants.k_PaintSelectorLimitPort);
  DigitalInput paintTriggerBtn = new DigitalInput(Constants.k_PaintTriggerBtnPort);

  /**
   * Creates a new Brush.
   */
  public Brush() {
    brushState = BrushState.INIT;
  }

  public int currentColor, nextColor;
  private double paintStartTime, lastTime;
  private boolean isDoneSelecting, countUp, lastSwitchState;

  private enum BrushState {
    INIT, IDLE, PAINTING, SELECTING_COLOR, WAIT_FOR_COLOR, WAIT_FOR_PAINT, UPDATE
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
  public boolean getSelectorSwitch() {
    return !paintSelctorLimit.get();
  }

  // Checks Limit Switch to see if trigger is l;ined up correctly
  public boolean getTriggerBtn() {
    return !paintTriggerBtn.get();
  }

  public void paintForTime(double speed) {
    m_paintTrigger.set(speed);
  }

  public void stopPainting() {
    m_paintTrigger.set(0.0);
  }

  public void update(Color color, boolean readyToPaint) {

    switch (this.brushState) {

    case INIT:
      brushTimer.start();
      this.currentColor = 1;
      brushState = BrushState.IDLE;
    break;
  
    case IDLE:
      if (color.colorVal == this.currentColor && readyToPaint){
        brushState = BrushState.PAINTING;
      }
      else if (color.colorVal != this.currentColor){
        brushState = BrushState.SELECTING_COLOR;
      }    
      else
        brushState = BrushState.IDLE;
        if(color.colorVal == this.currentColor){
          System.out.println("Correct Color, Waiting for X/Y");
        }

        if(readyToPaint){
          System.out.println("Correct Pos, Waiting for Selector");
        }

    break;

    //Triggers the paint can and records the start time
    //Sets state to WAIT_FOR_PAINT when finished   
    case PAINTING:
      paintStartTime = brushTimer.get();
      // this.paintForTime();
      brushState = BrushState.WAIT_FOR_PAINT;
    break;
    
    //Starts the color selector moving the ideal direction
    //Sets state to WAIT_FOR_COLOR when finished 
    case SELECTING_COLOR:
      //Optimization to decide whether to turn right or left, pretty cool how simple it is
      if(this.currentColor + 4 > color.colorVal) {
        this.spinSelectorCW();
        countUp = true; //If turning clockwise then increment upwards at UPDATE
        brushState = BrushState.WAIT_FOR_COLOR;
      }
      else{ 
        this.spinSelectorCCW();
        countUp = false; //If turning counter-clockwise then increment downwards at UPDATE
        brushState = BrushState.WAIT_FOR_COLOR;
      }
    break;


    //Waits for Paint Selector to reach desired color
    case WAIT_FOR_COLOR:
      if(this.getSelectorSwitch() && !this.lastSwitchState){
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
        System.out.println("Last Switch State " + this.lastSwitchState);
      }

      if(this.currentColor == color.colorVal){
        this.spinSelectorOff();
        brushState = BrushState.IDLE;
      }

      this.lastSwitchState = this.getSelectorSwitch();
    break;

    //Waits designated amount of time for paint application to finish,
    //stops paint trigger motor, and resets the active color to none.
    //Returns to IDLE when finished
    case WAIT_FOR_PAINT:
      if(this.getTriggerBtn())
        this.stopPainting();
        //TODO: this.currentColor = Color.NONE.colorVal;
        brushState = BrushState.IDLE;
    break;

    //Update keeps the currentColor variable updated as we cycle through colors approaching the goal
    case UPDATE:
      if(this.countUp && this.currentColor == 8)//Wrap 8 back to 1
        this.currentColor = 1;
      else if(this.countUp) 
        this.currentColor += 1;
      else if(!this.countUp && this.currentColor == 1)//Wrap 1 to 8 
        this.currentColor = 8;
      else
        this.currentColor -= 1;

      if(this.isDoneSelecting)
      {
        this.spinSelectorOff();
        brushState = BrushState.IDLE;
      }
      else 
      {
        brushState = BrushState.WAIT_FOR_COLOR;
      }  
    break;
    }
  }
}
