/*
    Main robot teleop program
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

import org.opencv.optflow.DISOpticalFlow;

import java.util.ArrayList;

@TeleOp(name = "TeleOpMain", group = "Main")
//@Disabled
public class TeleOpMain extends OpMode {

    BotHardware robot = new BotHardware();

    boolean lastLeftBumperState = false;
    boolean lastRightBumperState = false;
    boolean lastAButtonState = false;
    boolean isShooting = false;
    boolean isBacon = false;
    double leftPusherState = -1.0;
    double rightPusherState = -1.0;

    AutoLib.Sequence mShoot;
    AutoLib.Sequence mBeacon;

    AutoLib.Timer mEncoderMeasure = new AutoLib.Timer(1.0);
    int lastEncode = 0;
    double avgEncode = 0;

    private void initShoot(){
        mShoot = new AutoLib.LinearSequence();

        mShoot.add(new AutoLib.EncoderMotorStep(robot.launcherMotor, 1.0,  1500, true, this));
        mShoot.add(new AutoLib.TimedServoStep(robot.ballServo, 0.5, 0.4, true));
        //mShoot.add(new AutoLib.LogTimeStep(this, "WAIT", 0.2));
    }

    private void initBeacon() {
        final float Kp = 0.20f;
        final float Ki = 0.00f;
        final float Kd = 0.00f;
        final float KiCut = 3.0f;

        // parameters of the PID controller for the ultrasonic sensor driving
        final float Kp4 = 0.025f;
        final float Ki4 = 0.00f;
        final float Kd4 = 0;
        final float Ki4Cutoff = 0.00f;

        SensorLib.PID mHeadingPid = new SensorLib.PID(Kp, Ki, Kd, KiCut);
        SensorLib.PID mUltraPid = new SensorLib.PID(Kp4, Ki4, Kd4, Ki4Cutoff);

        mBeacon = new AutoLib.LinearSequence();

        mBeacon.add(new AutoLib.GyroTurnStep(this, 0, new UltraHeading(robot.distSensorLeft, robot.distSensorRight), robot.getMotorArray(), 1.0f, 5.0f, true));
        mBeacon.add(new LineDrive.UltraSquirrleyAzimuthFinDriveStep(this, 0, 0, new UltraHeading(robot.distSensorLeft, robot.distSensorRight), new LineDrive.UltraCorrectedDisplacement(this, robot.distSensorLeft, 15), mHeadingPid, mUltraPid,
            robot.getMotorArray(), 1.0f, new LineDrive.UltraSensors(robot.distSensorLeft, 15, 3.0f), true));

        AutoLib.Sequence mPush = new AutoLib.ConcurrentSequence();

        mPush.add(new AutoLib.TimedServoStep(robot.leftServo, 1.0, 0.2, false));
        mPush.add(new AutoLib.TimedServoStep(robot.rightServo, 1.0, 0.2, false));

        mBeacon.add(mPush);
    }

    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");

        // hardware maps
        robot.init(this, false);

        robot.leftServo.setPosition(leftPusherState);
        robot.rightServo.setPosition(rightPusherState);

        initShoot();
    }

    @Override
    public void init_loop(){
        telemetry.addData("NavX Ready", robot.startNavX());
    }

    @Override
    public void start() {
        mEncoderMeasure.start();
        lastEncode = robot.backLeftMotor.getCurrentPosition();
    }

    @Override
    public void loop() {
        /*
        if(mEncoderMeasure.done()) {
            double encode = robot.frontRightMotor.getCurrentPosition() - lastEncode;
            avgEncode = (avgEncode + encode)/2.0;
            telemetry.addData("Encoder Current", robot.frontRightMotor.getCurrentPosition());
            telemetry.addData("Encoders Per Second Current", encode);
            telemetry.addData("Encoders Per Second Average", avgEncode);

            lastEncode = robot.frontRightMotor.getCurrentPosition();
            mEncoderMeasure.start();
        }

        if (gamepad2.a) {
            //automation
            if(gamepad2.a && !isShooting){
                initShoot();
            }
            if(gamepad2.a || isShooting){
                robot.sweeperMotor.setPower(-1.0);
                if(mShoot.loop()){
                    isShooting = false;
                    initShoot();
                }
                else isShooting = true;
            }
        }
        else if (gamepad1.x){
            //automation
            if(gamepad1.x && !isBacon){
                initBeacon();
            }
            if(gamepad1.x || isBacon){
                if(mBeacon.loop()){
                    isBacon = false;
                    initBeacon();
                }
                else isBacon = true;
            }
        }

        else{
        */
            isShooting = false;
            /*

                             1,1,1,1
                                |
                     0,0,1,1    |    1,1,0,0
                                |
                -1,-1,1,1 ------------- 1,1,-1,-1
                                |
                   -1,-1,0,0    |    0,0,-1,-1
                                |
                           -1,-1,-1,-1

            float x = gamepad1.left_stick_x;
            float y = -1 * gamepad1.left_stick_y;

            float ySign = (y >= 0) ? 1 : -1;

            float frontPower = (ySign * x > 0) ? ySign : (ySign * Math.abs(x) * -2 + ySign);
            float backPower = (ySign * x < 0) ? ySign : (ySign * Math.abs(x) * -2 + ySign);

            */

            // run drivetrain motors
            // dpad steering
            if(gamepad1.dpad_up && gamepad1.dpad_left) {

                robot.setFrontPower(0.0);
                robot.setBackPower(1.0);
            }
            else if(gamepad1.dpad_up && gamepad1.dpad_right) {
                robot.setFrontPower(1.0);
                robot.setBackPower(0.0);
            }
            else if(gamepad1.dpad_down && gamepad1.dpad_left) {
                robot.setFrontPower(-1.0);
                robot.setBackPower(0.0);
            }
            else if(gamepad1.dpad_down && gamepad1.dpad_right) {
                robot.setFrontPower(0.0);
                robot.setBackPower(-1.0);
            }
            else if(gamepad1.dpad_up) {
                robot.setFrontPower(1.0);
                robot.setBackPower(1.0);
            }
            else if(gamepad1.dpad_left) {
                robot.setFrontPower(-1.0);
                robot.setBackPower(1.0);
            }
            else if(gamepad1.dpad_right) {
                robot.setFrontPower(1.0);
                robot.setBackPower(-1.0);
            }
            else if(gamepad1.dpad_down) {
                robot.setFrontPower(-1.0);
                robot.setBackPower(-1.0);
            }
            else {
                // joystick tank steering
                robot.frontLeftMotor.setPower(-gamepad1.left_stick_y);
                robot.frontRightMotor.setPower(-gamepad1.right_stick_y);
                robot.backLeftMotor.setPower(-gamepad1.left_stick_y);
                robot.backRightMotor.setPower(-gamepad1.right_stick_y);
            }

            // run lifter motor
            if(gamepad1.left_trigger > 0.2 || gamepad2.left_trigger > 0.2) {
                robot.sweeperMotor.setPower(-1.0);
            }
            else if(gamepad1.right_trigger > 0.2 || gamepad2.right_trigger > 0.2) {
                robot.sweeperMotor.setPower(1.0);
            }
            else {
                robot.sweeperMotor.setPower(0.0);
            }

            // run launcher motor

            if(gamepad2.x) {
                robot.launcherMotor.setPower(1.0);
            }
            else {
                robot.launcherMotor.setPower(0.0);
            }

            if(gamepad2.b) {
                robot.ballServo.setPosition(0.6);
            }
            else {
                robot.ballServo.setPosition(0.0);
            }

            // toggle button pushers
            if(!lastLeftBumperState && gamepad1.left_bumper) {
                leftPusherState *= -1.0;
            }
            if(!lastRightBumperState && gamepad1.right_bumper) {
                rightPusherState *= -1.0;
            }

            robot.leftServo.setPosition(leftPusherState);
            robot.rightServo.setPosition(rightPusherState);

            lastLeftBumperState = gamepad1.left_bumper;
            lastRightBumperState = gamepad1.right_bumper;


            //toggle reversed steering
            if(!lastAButtonState && gamepad1.a) {
                robot.initMotors(this, false, !robot.isReversed());
                if(robot.isReversed()){
                    robot.dim.setLED(0, true);
                    telemetry.addData("Reversed", "True");
                }
                else{
                    robot.dim.setLED(0, false);
                    telemetry.addData("Reversed", "False");
                }
            //}
            lastAButtonState = gamepad1.a;
        }
    }

    public static class UltraHeading implements HeadingSensor {
        UltrasonicSensor mLeft;
        UltrasonicSensor mRight;

        UltraHeading(UltrasonicSensor left, UltrasonicSensor right){
            mLeft = left;
            mRight = right;
        }

        public float getHeading(){
            return (float)(mRight.getUltrasonicLevel() - mLeft.getUltrasonicLevel());
        }

    }

    public static float[] getCorrectedSquirrleyMotorPowers(float dt, float mDirection, float mHeading, HeadingSensor mGyro, SensorLib.PID mPid, float mPower) {
        final float heading = mGyro.getHeading();     // get latest reading from direction sensor
        // convention is positive angles CCW, wrapping from 359-0

        final float error = SensorLib.Utils.wrapAngle(heading - mHeading);   // deviation from desired heading
        // deviations to left are positive, to right are negative

        // feed error through PID to get motor power correction value
        final float correction = -mPid.loop(error, dt);

        //calculate motor powers for fancy wheels
        AutoLib.MotorPowers mp = AutoLib.GetSquirrelyWheelMotorPowers(mDirection);

        final float leftPower = correction;
        final float rightPower = -correction;

        //fr, br, fl, bl
        final float[] ret = {
                (rightPower + (float)mp.Front()) * mPower,
                (rightPower + (float)mp.Back()) * mPower,
                (leftPower + (float)mp.Front()) * mPower,
                (leftPower + (float)mp.Back()) * mPower};

        return ret;
    }
}
