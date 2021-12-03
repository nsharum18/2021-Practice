package frc.robot;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.cscore.UsbCamera;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;



/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
@SuppressWarnings({ "deprecation" })
public class Robot extends IterativeRobot {


	

	String autoSelected;
	Joystick stick = new Joystick(0);
	private DifferentialDrive DemoDrive;
	Joystick xBox;
	WPI_TalonSRX Left1, Left2, Right1, Right2;
	JoystickButton xBoxa, xBoxselect1, xBoxstart1, xBoxx, xBoxy, xBoxb, xBoxlb, xBoxrb, xBoxStick, xBoxStick2;
	DoubleSolenoid double1, double2, double3;
	Compressor Comp = new Compressor(0);
	Spark Motor1;
	Talon winch;
	NetworkTable table = NetworkTable.getTable("limelight");
	UsbCamera Back_Camera;
	Timer timer;
	double loop, t;
	Command autonomusCommand;
	SendableChooser<String> autoChooser = new SendableChooser<>();

	int timeoutMs = 10;


	//encoders
	public static final int LENC = 2;
	public static final int RENC = 4;

	WPI_TalonSRX LEncoder = new WPI_TalonSRX(LENC);
	WPI_TalonSRX REncoder = new WPI_TalonSRX(RENC);


	public enum AutoStage{

		kStart,
		kDriveForward,
		kDriveForward1,
		kDriveForward2,
		kDriveForward3,
		kTurn,
		kTurn1,
		kTurn2,
		kTurn3,
		kDriveBack,
		kDriveBack1,
		kDriveBack2,
		kArmsDown,
		kArmsUp,
		kLowerFly,
		kRaiseFly,
		kDropCube,
		kDone


	}

	public void move(double distance, double speed) {
		if (Currentstage == AutoStage.kStart) {  // When start
			DemoDrive.arcadeDrive((speed * 0.01), -0.1); // Start moving forward
			SmartDashboard.putString("Auto Stage", "kStart");
			if (REncoder.getSelectedSensorPosition(0) >= distance) { // If motor has rotated more than ten times
				Currentstage = AutoStage.kDriveForward; // Set stage to done
			}
		}
	}


	AutoStage Currentstage = AutoStage.kStart;






	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {

		//timer
		timer = new Timer();



		//winch motor
		winch = new Talon(1);

		//cameras
		Back_Camera = CameraServer.getInstance().startAutomaticCapture();

		//xBox controller
		xBox = new Joystick(0);

		//buttons
		xBoxa = new JoystickButton(xBox, 1);
		xBoxb = new JoystickButton(xBox, 2);
		xBoxx = new JoystickButton(xBox, 3);
		xBoxy = new JoystickButton(xBox, 4);
		xBoxlb = new JoystickButton(xBox, 5);
		xBoxrb = new JoystickButton(xBox, 6);
		xBoxselect1 = new JoystickButton(xBox, 7);
		xBoxstart1 = new JoystickButton(xBox, 8);
		xBoxStick = new JoystickButton(xBox, 9);
		xBoxStick2 = new JoystickButton(xBox, 10);

		//compressor
		Comp.setClosedLoopControl(false);

		//Talons
		Left1 = new WPI_TalonSRX(1);
		Left2 = new WPI_TalonSRX(2);
		Right1 = new WPI_TalonSRX(4); 
		Right2 = new WPI_TalonSRX(3);

		//Climber 
		Motor1 = new Spark(0);

		//talon groups
		SpeedControllerGroup m_left = new SpeedControllerGroup(Left1, Left2);
		SpeedControllerGroup m_right = new SpeedControllerGroup(Right1, Right2);

		//Drivetrain
		DemoDrive = new DifferentialDrive(m_left, m_right);

		//Solenoids
		double1 = new DoubleSolenoid(0,1);
		double2 = new DoubleSolenoid(2,3);
		double3 = new DoubleSolenoid(4,5);



		REncoder.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 0);
		//REncoder.setSensorPhase(false);
		LEncoder.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 0);
		LEncoder.setSensorPhase(false);

	}

	public void calibrateNavX()
	{	
		System.out.println("Calibrating NavX...");


		
	}


	/**
	 * This function is run once each time the robot enters autonomous mode
	 */
	@Override
	public void autonomousInit() {


		Currentstage = AutoStage.kStart;


		REncoder.setSelectedSensorPosition(0, 0, 0);
		LEncoder.setSelectedSensorPosition(0, 0, 0);
		

		Timer.delay(.2);

		//set limelight led/camera
		table.putNumber( "ledMode", 1);
		table.putNumber( "camMode", 1);

		Comp.setClosedLoopControl(true);
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {	


		Left2.configOpenloopRamp(.4, timeoutMs);
		Right1.configOpenloopRamp(.4, timeoutMs);

		SmartDashboard.putNumber("Right Sensor position", REncoder.getSelectedSensorPosition(0));
		SmartDashboard.putNumber("Left Sensor position", LEncoder.getSelectedSensorPosition(0));
		
		String gameData;
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		
/*--------------------------------------------------------------------------------------------------------------*/

		move(3800, -50);
		if (Currentstage == AutoStage.kDriveForward) {
			DemoDrive.arcadeDrive(0.5, -0.107);
			SmartDashboard.putString("Auto Stage", "kDriveForward");
			if (REncoder.getSelectedSensorPosition(0) <= -3800) { // If motor has rotated more than ten times
				Currentstage = AutoStage.kDriveBack; // Set stage to done
			}
		}
		if (Currentstage == AutoStage.kDriveBack) {
			DemoDrive.arcadeDrive(-0.5, -0.107);
			SmartDashboard.putString("Auto Stage", "kDriveBack");
			if (REncoder.getSelectedSensorPosition(0) >= 0) { // If motor has rotated more than ten times
				Currentstage = AutoStage.kDone; // Set stage to done
			}
		}
		if (Currentstage == AutoStage.kDone) { // When stage is done
			DemoDrive.arcadeDrive(0,0);
			SmartDashboard.putString("Auto Stage", "kDone");
		}
	}


	




	/**
	 * This function is called once each time the robot enters tele-operated
	 * mode
	 */
	@Override
	public void teleopInit() {

		Comp.setClosedLoopControl(true);

		Currentstage = AutoStage.kDone;

		REncoder.setSelectedSensorPosition(0, 0, 0);
		LEncoder.setSelectedSensorPosition(0, 0, 0);
		
		
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {

		SmartDashboard.putNumber("Right Sensor position", REncoder.getSelectedSensorPosition(0));
		SmartDashboard.putNumber("Left Sensor position", LEncoder.getSelectedSensorPosition(0));

		// Variables (Added by Wesley)
		double WMaxSpeed = 70;
		double WRotationOffset = 0.00;
		double Wl = 0;
		double Wr = 0;
		

		Left2.configOpenloopRamp(0, timeoutMs);
		Right1.configOpenloopRamp(0, timeoutMs);

		//limelight camera/led set
		table.putNumber( "ledMode", 1);
		table.putNumber( "camMode", 1);

		//set stage
		Currentstage = AutoStage.kDone;


		//Drivesticks (Replaced by Wesley's Drivestick functions)
		double WVelocity = (xBox.getRawAxis(1) * WMaxSpeed * -0.01);
			double WAngularVelocity = (xBox.getRawAxis(0) + WRotationOffset);
			DemoDrive.arcadeDrive(WVelocity, WAngularVelocity*0.5);
			
			if (xBox.getRawAxis(2) >= 0.1 || xBox.getRawAxis(3) >= 0.1) {
				if (xBoxlb.get()) { // Reverse left side with button
					Wl = -1;
				} else {
					Wl = 1;
				}
				if (xBoxrb.get()) { // Reverse right side with button
					Wr = -1;
				} else {
					Wr = 1;
				}
				DemoDrive.tankDrive((WMaxSpeed*Wl*(xBox.getRawAxis(2)+WRotationOffset)), (WMaxSpeed*Wr*(xBox.getRawAxis(3)-WRotationOffset)));
			}

		//compressor on						
		if (xBoxstart1.get()) {
			Comp.setClosedLoopControl(true);
		}
		//compressor off
		else if (xBoxselect1.get()) {
			Comp.setClosedLoopControl(false);
		
		}
		
		//Arms up
		else if (xBoxStick.get()) {
			double1.set(DoubleSolenoid.Value.kForward); // Up
		}

		// Arms down
		else if (xBoxStick2.get()) {
			double1.set(DoubleSolenoid.Value.kReverse); // Down
		}
		
		//Flysection down (Edited)
		else if (xBox.getRawAxis(5) >= 0.1) {
			Motor1.set(-0.7 * xBox.getRawAxis(5));
			SmartDashboard.putString("Flysection Status", "DOWN");
		}
		//Flysection up (Edited)
		else if (xBox.getRawAxis(5) <= -0.1) {
			Motor1.set(-0.7 * xBox.getRawAxis(5));
			SmartDashboard.putString("Flysection Status", "UP");
		}
		//Arms Close (Edited)
		else if (xBox.getRawAxis(4) >= 0.3) {
			double2.set(DoubleSolenoid.Value.kForward);
			SmartDashboard.putString("Arm Status", "CLOSED");
		}
		//Arms Open (Edited)
		else if (xBox.getRawAxis(4) <= -0.3) {
			double2.set(DoubleSolenoid.Value.kReverse);
			SmartDashboard.putString("Arm Status", "OPEN");
		}
		//Easy Mode (Added)
		else if (xBoxa.get()) {
			WMaxSpeed = 35;
			SmartDashboard.putString("Current Mode", "EASY");
		}
		else if (xBoxy.get()) {
			WMaxSpeed = 100;
			SmartDashboard.putString("Current Mode", "HARD");
		}
		//Rotation Offset Adjustment (Added)
		else if (xBoxx.get()) {
			WRotationOffset -= 0.05;
			SmartDashboard.putNumber("Rotation Offset:", WRotationOffset);
			Timer.delay(0.1);
		}
		else if (xBoxb.get()) {
			WRotationOffset += 0.05;
			SmartDashboard.putNumber("Rotation Offset:", WRotationOffset);
			Timer.delay(0.1);
		}
		else {
			//off until used
			double1.set(DoubleSolenoid.Value.kOff);
			double2.set(DoubleSolenoid.Value.kOff);
			double3.set(DoubleSolenoid.Value.kOff);
			Motor1.set(0);
			winch.set(0);
			SmartDashboard.putString("Flysection Status", "NEUTRAL");
		}

		if (Comp.getClosedLoopControl() == false) {
			SmartDashboard.putString("Compressor Status", "OFF");
		}

		if (Comp.getClosedLoopControl() == true) {
			SmartDashboard.putString("Compressor Status", "ON");
		}

	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {	}
}
