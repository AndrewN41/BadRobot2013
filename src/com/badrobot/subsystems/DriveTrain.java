package com.badrobot.subsystems;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.badrobot.BadRobotMap;
import com.badrobot.OI;
import com.badrobot.commands.DriveStraightForward;
import com.badrobot.commands.DriveWithController;
import com.badrobot.commands.Turn;
import com.badrobot.subsystems.interfaces.IDriveTrain;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Subsystem for the drivetrain.
 *
 * @author Isaac
 */
public class DriveTrain extends BadSubsystem implements IDriveTrain {

    SpeedController frontLeft, frontRight, backLeft, backRight;
    RobotDrive train;
    Ultrasonic ultrasonic;
    Gyro gyro;
    boolean PIDEnabled = false;
    PIDController leftFrontController, rightFrontController, leftBackController, rightBackController;
    EasyPID leftEncoderPID, rightEncoderPID;
    Encoder leftEncoder;
    Encoder rightEncoder;
    private static double MAX_SPEED = 1600;
    public static DriveTrain instance;

    public static DriveTrain getInstance() {
        if (instance == null) {
            instance = new DriveTrain();
        }
        return instance;
    }

    private DriveTrain() {
    }
    //variable for tolerance of PID controllers
    int tolerance = 15;

    public void initialize() {
        if (!BadRobotMap.isPrototype) {
            leftEncoder = new Encoder(BadRobotMap.leftSideEncoderIn, BadRobotMap.leftSideEncoderOut, true);
            leftEncoder.setDistancePerPulse(1);
            leftEncoder.setPIDSourceParameter(Encoder.PIDSourceParameter.kRate);
            leftEncoder.start();

            rightEncoder = new Encoder(BadRobotMap.rightSideEncoderIn, BadRobotMap.rightSideEncoderOut, true);
            rightEncoder.setDistancePerPulse(1);
            rightEncoder.setPIDSourceParameter(Encoder.PIDSourceParameter.kRate);
            rightEncoder.start();
        }

        gyro = new Gyro(BadRobotMap.driveTrainGyro);
        gyro.reset();

        ultrasonic = new Ultrasonic(BadRobotMap.driveTrainUltrasonicPing,
                BadRobotMap.driveTrainUltrasonicEcho, Ultrasonic.Unit.kInches);
        ultrasonic.setEnabled(true);
        ultrasonic.setAutomaticMode(false);

        if (!BadRobotMap.isPrototype) {
            frontLeft = new Talon(BadRobotMap.frontLeftSpeedController);
            frontRight = new Talon(BadRobotMap.frontRightSpeedController);
            backLeft = new Talon(BadRobotMap.backLeftSpeedController);
            backRight = new Talon(BadRobotMap.backRightSpeedController);


            if (PIDEnabled) {
            }


            /*leftFrontController = new PIDController(.0005, 0, 0, 0.0, leftEncoder, frontLeft);
             rightFrontController = new PIDController(.0005, 0, 0, 0.0, rightEncoder, frontRight);
             leftBackController = new PIDController(.0005, 0, 0, 0.0, leftEncoder, backLeft);
             rightBackController = new PIDController(.0005, 0, 0, 0.0, rightEncoder, backRight);
        
             /*SmartDashboard.putData("left front PID",leftFrontController);
             SmartDashboard.putData("right front PID", rightFrontController);
             SmartDashboard.putNumber("Max encoder speed", MAX_SPEED);
        
             leftFrontController.setInputRange(-MAX_SPEED, MAX_SPEED);
             rightFrontController.setInputRange(-MAX_SPEED, MAX_SPEED);
             leftBackController.setInputRange(-MAX_SPEED, MAX_SPEED);
             rightBackController.setInputRange(-MAX_SPEED, MAX_SPEED);
        
             leftFrontController.setOutputRange(-1,1);
             rightFrontController.setOutputRange(-1,1);
             leftBackController.setOutputRange(-1,1);
             rightBackController.setOutputRange(-1,1);
        
             leftFrontController.setPercentTolerance(tolerance);
             rightFrontController.setPercentTolerance(tolerance);
             leftBackController.setPercentTolerance(tolerance);
             rightBackController.setPercentTolerance(tolerance);*/
        } else {
            frontLeft = new Talon(BadRobotMap.frontLeftSpeedController);
            frontRight = new Talon(BadRobotMap.frontRightSpeedController);
            backLeft = new Talon(BadRobotMap.backLeftSpeedController);
            backRight = new Talon(BadRobotMap.backRightSpeedController);
        }

        /*leftFrontController.enable();
         rightFrontController.enable();
         leftBackController.enable();
         rightBackController.enable();*/


        train = new RobotDrive(frontLeft, backLeft, frontRight, backRight);

        /*train.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
         train.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
         train.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
         train.setInvertedM00otor(RobotDrive.MotorType.kRearRight, true);
         */

        train.setSafetyEnabled(false);
    }

    /**
     * Event handler for when the value of a variable put in the network has
     * changed. Changes the local value of the variable.
     *
     * @param key the name of the variable in the NetworkTable
     * @param value the value that the variable has been changed to
     */
    public void valueChanged(ITable itable, String key, Object value, boolean bln) {
    }

    protected void addNetworkTableValues(ITable table) {
    }

    public String getConsoleIdentity() {
        return "ImpDriveTrain";
    }

    protected void initDefaultCommand() {
        this.setDefaultCommand(new DriveWithController());
    }

    boolean firstRun = false;
    /**
     * Drives the robot in tank drive--two sticks represent the left and right
     * sides of the robot; pushing forward on the left stick moves the left side
     * forward, pushing backwards on the right stick moves the right side of the
     * robot backwards.
     *
     * @param left the left side joystick value (-1 to 1)
     * @param right the right joystick value (-1 to 1)
     */
    public void tankDrive(double left, double right) {
        
        if(leftEncoderPID == null && !firstRun && PIDEnabled)
        {
                leftEncoderPID = new EasyPID(.001, 0, 0, 0.2, "Left PID", leftEncoder);
                rightEncoderPID = new EasyPID(.001, 0, 0, 0.2, "Right PID", rightEncoder);
                leftEncoderPID.controller.setInputRange(-MAX_SPEED, MAX_SPEED);
                rightEncoderPID.controller.setInputRange(-MAX_SPEED, MAX_SPEED);

                leftEncoderPID.controller.setPercentTolerance(tolerance);
                rightEncoderPID.controller.setPercentTolerance(tolerance);

                leftEncoderPID.controller.enable();
                rightEncoderPID.controller.enable();
                
                firstRun = true;
        }

        if (leftEncoderPID != null && PIDEnabled && rightEncoderPID != null) {
            
            if (left > 0)
            {
                leftEncoderPID.controller.setOutputRange(0, 1);
            }
            else if (left < 0)
            {
                leftEncoderPID.controller.setOutputRange(-1, 0);
            }
            
            if (right > 0)
            {
                rightEncoderPID.controller.setOutputRange(0, 1);
            }
            else if (right< 0)
            {
                rightEncoderPID.controller.setOutputRange(-1, 0);
            }
            leftEncoderPID.setSetpoint(left * MAX_SPEED);
            rightEncoderPID.setSetpoint(right * MAX_SPEED);
            train.tankDrive(leftEncoderPID.getValue(), rightEncoderPID.getValue());

        } else {
            //log("Left Encoder PID = null");
            train.tankDrive(left, right);
        }


        //SmartDashboard.putNumber("left input", left);
        //SmartDashboard.putNumber("left speed", leftEncoder.getRate());
        //SmartDashboard.putNumber("PID value", leftEncoderPID.getValue());

        /*log("PIDController output: " + leftFrontController.get() + 
         "  PIDController output: " + rightFrontController.get());
         MAX_SPEED = SmartDashboard.getNumber("Max encoder speed");
         log(left + " left " + right + " right");*/
        /*leftFrontController.setSetpoint(left*MAX_SPEED);
         leftBackController.setSetpoint(left*MAX_SPEED);
         rightFrontController.setSetpoint(right*MAX_SPEED);
         rightBackController.setSetpoint(right*MAX_SPEED);*/
    }

    public void arcadeDrive(double Y, double X) {
        train.arcadeDrive(Y, X);
    }

    public Gyro getGyro() {
        return gyro;
    }

    public RobotDrive getTrain() {
        return train;
    }

    /**
     * @return Returns distance to wall in inches.
     */
    public double getDistanceToWall() {
        ultrasonic.ping();
        try {
            Thread.sleep(30);
        } catch (Exception ex) {
        }
        return ultrasonic.getRangeInches();
    }

    public void registerPreferencesValues() {
    }
}
