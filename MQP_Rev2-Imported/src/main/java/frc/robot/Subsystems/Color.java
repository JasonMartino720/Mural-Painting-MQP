/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.Subsystems;

/**
 * Color class for relating color to a spot on the paint switching mechanism
 */
public enum Color{
    NONE(0),
    BLACK(1), 
    BLUE(6), 
    GREEN(3),
    WHITE(4), 
    RED(5), 
    PURPLE(2), 
    YELLOW(7), 
    BROWN(8);

    protected final int colorVal;

    Color(int colorVal){
        this.colorVal = colorVal;
    }

    private Color retColor;

    public Color set(String value){
        switch(value){
            case "PURPLE":
               retColor = Color.PURPLE;
            break;

            case "BLUE":
               retColor = Color.BLUE;
            break;

            case "GREEN":
               retColor = Color.GREEN;
            break;
            
            case "WHITE":
               retColor = Color.WHITE;
            break;

            case "BLACK":
               retColor = Color.BLACK;
            break;

            case "RED":
               retColor = Color.RED;
            break;

            case "YELLOW":
               retColor = Color.YELLOW;
            break;

            case "BROWN":
               retColor = Color.BROWN;
            break;

            default:
                retColor = Color.NONE;
            break;
        }
        return retColor;
    }


}
