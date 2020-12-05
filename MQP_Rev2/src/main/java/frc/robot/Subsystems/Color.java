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
    ORANGE(1), 
    BLUE(2), 
    WHITE(3),
    BLACK(4), 
    RED(5), 
    YELLOW(6), 
    WHITE2(7), 
    PURPLE(8);

    protected final int colorVal;

    Color(int colorVal){
        this.colorVal = colorVal;
    }

    private Color retColor;

    public Color set(int value){
        switch(value){
            case 1:
               retColor = Color.ORANGE;
            break;

            case 2:
               retColor = Color.BLUE;
            break;

            case 3:
               retColor = Color.WHITE;
            break;

            case 4:
               retColor = Color.BLACK;
            break;

            case 5:
               retColor = Color.RED;
            break;

            case 6:
               retColor = Color.YELLOW;
            break;

            case 7:
               retColor = Color.WHITE2;
            break;

            case 8:
               retColor = Color.PURPLE;
            break;

            default:
                retColor = Color.NONE;
            break;
        }
        return retColor;
    }


}
