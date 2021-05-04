/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2020 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import javax.lang.model.util.ElementScanner6;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.opencsv.*;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.Subsystems.*;
//import frc.robot.murals.*;
import edu.wpi.first.wpilibj.Timer;
import java.util.ArrayList;
//import com.opencsv.*; 
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
  //private final DigitalInput btn = new DigitalInput(Constants.k_VexBtnPort);
  private final Timer timer = new Timer();
  private static final String CSV_FILE_PATH = "/home/lvuser/deploy/logo.csv";
  //Enums for main state machine
  private enum MainState {
    INIT, IDLE, SET_POSITIONS, WAIT_FOR_ALIGNMENT, UPDATE_BRUSH, PAINT_DELAY, END
  }
  DigitalInput upSwitch = new DigitalInput(Constants.k_upSwitchPort);
  private int button = 0;
  private static MainState state, nextState, postWaitState;
  private static Color previousColor, currentColor;

  private int nextColor, wallLength, wallHeight, wallEnd;
  private double startTime, delayTime, waitStartTime, waitTime;
  public static int currentPosition[] = new int[2];
  public static int nextPosition[] = new int[2];
  private boolean moveY, moveL, xAligned, yAligned, readyToPaint, pressed;
  private String[][] teleopGrid;
  private double ySpeed;
  private int xCount = 0;
  public static Joystick joy = new Joystick(0);
  private double lidarStart;
                        
  
  private int _loops = 0;
  public static void useOpenCSV(String file) {
    try{
      Reader reader = Files.newBufferedReader(Paths.get(file));
      CSVReader csvReader = new CSVReader(reader);
     
      List<String[]> allData = csvReader.readAll();
      System.out.println("Printing All Data From CSV " + allData);
    }
    catch(Exception e){
      System.out.println("Caught an Error in useOpenCSV");
     
      e.printStackTrace();
    }

  }

  //Trying to import the file with file reader, read line by line with buffered reader
  public static int[][] importCSV(String file)
  {
    String line = "";
    String splitBy = ",";
    int[][] returnArray = {};

    try{
      BufferedReader bReader = new BufferedReader(new FileReader(file));
      ////This for loop iterates the row and stops when its out of rows to read
      for(int rowNum=0; ((line = bReader.readLine()) != null); rowNum++)
      {
          //This splits by the commas and returns an array of the number of items it found commma seperated
          String[] rowString = (line.split(splitBy));
          
          //This loop goes through the String Array for each line and parses the int out and hopefully places it in the right spot in the return Array
          for(int i=0; i != rowString.length; i++){
            int currInt = Integer.parseInt(rowString[i]);
            //I don't really think you can append to int[][] in the general sense but I think this will work as a substitute
            returnArray[rowNum][i] = currInt;
          }  
      }
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
    //Return array once buffered reader picks up null pretty much
    return returnArray;
  }

   
  // Java code to illustrate reading a 
// CSV file line by line 
  // Java code to illustrate reading a 
// all data at once 
  public  void readAllDataAtOnce(String file) 
  { 
    try { 
      // Create an object of file reader 
      // class with CSV file as a parameter. 
      FileReader filereader = new FileReader(file); 

      // create csvReader object and skip first Line 
      CSVReader csvReader = new CSVReaderBuilder(filereader) 
                  .withSkipLines(0) 
                  .build(); 
      List<String[]> allData = csvReader.readAll(); 

      // print Data 
      for (String[] row : allData) { 
        for (String cell : row) { 
          //System.out.print(cell + "\t"); 
        } 
        //System.out.println(); 
      } 
      
      String[][] retData = new String[allData.size()][];
      retData = allData.toArray(retData);
      
      //Print Final Data 
      for (String[] row : retData) { 
        for (String cell : row) { 
          //System.out.print(cell + "\t"); 
        } 
        //System.out.println(); 
      } 

      this.teleopGrid = retData;
    } 
    catch (Exception e) { 
      e.printStackTrace(); 

    } 
    
  } 



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
    System.out.println("deploy" + Filesystem.getDeployDirectory());
    xTrav.updatePositionValue();
    brush.init();
    timer.start();
    yTrav.resetEnc();
    xTrav.resetEnc();
    xTrav.getStartPosition();
    readAllDataAtOnce(CSV_FILE_PATH);
    //Path pathToFile = Paths.get("\\small_mural.csv");
    //System.out.println(pathToFile.toAbsolutePath());
    
    //System.out.println("teleopGrid" + teleopGrid);
    //teleopGrid = botRoss;
    Robot.state = MainState.INIT;
    currentColor = Color.BLACK;
    previousColor = currentColor;
    wallLength = teleopGrid[0].length;
    wallHeight = teleopGrid.length - 1;
    if (wallLength % 2 != 1){
      wallEnd = 0;
    }
    else{
      wallEnd = wallLength - 1;
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
      System.out.println("Paint Trigger Button " + brush.getTriggerBtn());
     }*/

    //System.out.println("Paint Selector Btn " + brush.getSelectorSwitch());
    //System.out.println("Desired Color " + currentColor);
    //ystem.out.println("Robot's Current Color " + brush.currentColor);

     // System.out.println("Current state " + Robot.state + " next state: " + nextState + " ready to paint: " + readyToPaint + " finished painting: " + Brush.finishedPainting);
    xTrav.updatePositionValue();
    brush.update(previousColor, currentColor, readyToPaint);
    //System.out.println(upSwitch.get());
    //System.out.println("enc" + brush.getEncPosition());
    //System.out.println("state: " + Robot.state + " next state: " + Robot.nextState);
    switch(Robot.state){

      //Init case for setting robot/mural configuration
      case INIT:
        System.out.println("INIT");
        moveL = false;
        readyToPaint = true;
        if (upSwitch.get()){
          moveY = true;
          ySpeed = -1.0;    
        }
        else{
          moveY = false;
          ySpeed = 1.0;
        }
        nextState = MainState.IDLE;
        currentColor = currentColor.set(this.teleopGrid[Robot.nextPosition[1]][Robot.nextPosition[0]]);
        System.out.println(currentColor);
        if(currentColor == Color.NONE){
          Robot.state = MainState.IDLE;
          //System.out.println("none");
        }
        else{
          //System.out.println("i don't know why its here 3");
          Robot.state = MainState.UPDATE_BRUSH;
          //System.out.println("update brush");
        } 
      break;
      
      //Idle Case, returns here after every paint. Handles when the robot reaches the end of the mural.
      //Check mural for necessary color
      case IDLE:
        // if current x is at either end of the wall
        // iterate Y direction 
        System.out.println("IDLE");
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
            //System.out.println("next position" + Robot.nextPosition);
          }
          else{
            Robot.nextPosition[0] = Robot.currentPosition[0] - 1;
            //System.out.println("next position" + Robot.nextPosition);
          }
          moveY = false;
        }
        System.out.println("nextposition" + "x" + Robot.nextPosition[0] + " y " + Robot.nextPosition[1]);
        previousColor = currentColor;
        System.out.println(this.teleopGrid[Robot.nextPosition[1]][Robot.nextPosition[0]]);
        currentColor = currentColor.set(this.teleopGrid[Robot.nextPosition[1]][Robot.nextPosition[0]]);
        if(currentColor == Color.NONE && moveY){
          Robot.state = MainState.SET_POSITIONS;          
          Robot.currentPosition = Robot.nextPosition;
        }
        else if(currentColor == Color.NONE){
          System.out.println("none");
          Robot.state = MainState.IDLE;
          Robot.currentPosition = Robot.nextPosition;
        } 
        else{
          Robot.state = MainState.SET_POSITIONS;
        }
      break;

      //Set next position for robot and start timer
      case SET_POSITIONS:
        //System.out.println("SET_POSITIONS");
        
        xTrav.setPositionClosedLoopSetpoint(Robot.nextPosition[0] * 1.5);
        
        startTime = timer.get();
        if(!yTrav.atPosition()){
          yTrav.setSpeed(ySpeed);
        }
        if(currentColor == Color.NONE){
          //System.out.println("color is none");
          state = MainState.WAIT_FOR_ALIGNMENT;
        }
        else{
          //System.out.println("color isn't none");
          //System.out.println("i don't know why its here 2");
          state = MainState.UPDATE_BRUSH;
          nextState = MainState.WAIT_FOR_ALIGNMENT;
        }
        
      break;

      //Waits until total alignment to start timeout for allowing robot to settle
      case WAIT_FOR_ALIGNMENT:
       if (++_loops >= 0) {
         _loops = 0;
         //System.out.println("WAIT_FOR_ALIGNMENT");
       }
        if(yTrav.atPosition()){
          yTrav.setSpeed(0.0);
          yAligned = true;
        }
        else {
          yAligned = false;
        }

        if(xTrav.atPosition()){
          //System.out.println("x true");
          xAligned = true;
        }
        else {
          //System.out.println("x false");
          xAligned = false;
        }

        //Done when fully aligned and there is an active color
        if(yAligned && xAligned && currentColor != Color.NONE){
          nextState = MainState.IDLE;
          System.out.println("i don't know why its here 1");
          postWaitState = MainState.UPDATE_BRUSH;
          Robot.currentPosition = Robot.nextPosition;
          waitStartTime = timer.get();
          System.out.println("Wait Started at time " + timer.get());
          waitTime = 4.0;
          state = MainState.PAINT_DELAY;
        }
        else if( yAligned && xAligned && currentColor == Color.NONE){
          state = MainState.IDLE;
        }
        else if (currentColor != Color.NONE){
          readyToPaint = false;
          //System.out.println("i don't know why its here");
          nextState = MainState.WAIT_FOR_ALIGNMENT;
          state = MainState.UPDATE_BRUSH;
        
        }
        else{
          readyToPaint = false;
          state = MainState.WAIT_FOR_ALIGNMENT;
        }
        
      break;

      //Runs the update cycle on the brush so paint changer is up to date and painting happens when ready
      case UPDATE_BRUSH:
        //System.out.println("UPDATE_BRUSH");
         if (++_loops >= 0) {
           _loops = 0;
           
         }

        if(readyToPaint && !Brush.finishedPainting){
          state = MainState.UPDATE_BRUSH;
        }
        else{ 
          state = nextState;
          readyToPaint = false;
        }
        
      break;

      //Delay while roobt paints and resets painting mechanism
      case PAINT_DELAY:
       if (++_loops >= 10) {
         _loops = 0;
         //System.out.println("PAINT_DELAY");
       }
        if(timer.get() - waitStartTime > waitTime)
        {
          System.out.println("Delay Completed with time " + timer.get());
          state = postWaitState;
          readyToPaint = true;
        }
        else{
          //System.out.println("Waiting for robot to settle, " + ((waitStartTime + waitTime) - timer.get()) + "remaining");
          readyToPaint = false;
        }
        
      break;

      //End Case - Never Leaves
      case END:
         if (++_loops >= 10) {
           _loops = 0;
           System.out.println("END");
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
    timer.start();
    startTime = timer.get();
    xTrav.resetEnc();
    xTrav.getStartPosition();
  }

  @Override
  public void testPeriodic() {
    boolean oneButton = false;
    boolean twoButton = false;
    if (timer.get() > startTime + 1){
      startTime = timer.get();
      xTrav.getFusedPosition();
    }
    //FOR X Movement with Joy
    if(Math.abs(joy.getRawAxis(0)) > 0.1){
      xTrav.setSpeed(joy.getRawAxis(0) * 0.75);  
    }
    else{
      xTrav.setSpeed(0);
    }
    if(joy.getRawButton(1) && !oneButton){
      oneButton = true;
      System.out.println("xcount" + xCount);
      xCount++;
      xTrav.setPositionClosedLoopSetpoint(xCount * 1.5);
    }
    if(joy.getRawButton(2) && !twoButton){
      twoButton = true;  
      System.out.println("xcount" + xCount);
      xCount--;
      xTrav.setPositionClosedLoopSetpoint(xCount * -1.5);
    }
    if(!joy.getRawButton(1)){
      oneButton = false;
    }
    if(!joy.getRawButton(2)){
      twoButton = false;
    }
    //FOR Y Movement with Joy
    if(Math.abs(joy.getRawAxis(1)) > 0.1){
      yTrav.setSpeed(joy.getRawAxis(1));  
    }
    else{
      yTrav.setSpeed(0);
    }

  }

}
