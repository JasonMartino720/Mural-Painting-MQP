/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.Subsystems;

/**
 * Add your docs here.
 */
public enum Color{
    NONE(69),
    RED(1), 
    BLUE(2), 
    GREEN(3),
    BLACK(4), 
    WHITE(5), 
    YELLOW(6), 
    ORANGE(7), 
    PURPLE(8);

    protected final int colorVal;

    Color(int colorVal){
        this.colorVal = colorVal;
    }

}
