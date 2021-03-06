// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc1388.commands;
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc1388.Robot;
import org.usfirst.frc1388.UsbLogging;
import org.usfirst.frc1388.RobotMap;

/**
 *
 */
public class AutonomousTurnTo extends Command {

	private double power;

	private double angle = 99999;
	private double time = 99999;

	private double target;
	private double error;
	private final double k_p = 0.02;
	private final double k_maxTurnPwr = 0.5; // not tested
	private final double k_marginOfError = 2; // not tested
	private final double k_powerOffset = 0.08;
	private final double k_minPwrCutoff = 0.2;

	private int stallCount;
	private final int k_stallCountThreshold = 25;
	private final double k_minSpeedThreshold = 5;


	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

	// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
	public AutonomousTurnTo(double angle) {

		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING

		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
		requires(Robot.driveTrain);

		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
		this.angle = angle;
	}

	public AutonomousTurnTo(double timeOrAngle, boolean isTime) {
		if(isTime = true) {
			this.time = timeOrAngle;
		} else {
			this.angle = timeOrAngle;
		}
		requires(Robot.driveTrain);


	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		// if time true, start timer
		UsbLogging.printLog(">>> " + this.getClass().getSimpleName() + " started");
		UsbLogging.printLog("Time: " + time + " Angle: " + angle + " Heading:" + Robot.gyro.getAngleZ());
		
		RobotMap.driveTrainmecanumDrive.setDeadband(0);

		setTimeout(time);

		stallCount = 0;

		target = angle;
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		error = target - Robot.gyro.getAngleZ();

		power = k_p * error + Math.copySign(k_powerOffset, error);

		power = Math.min(power,  k_maxTurnPwr);
		power = Math.max(power, -k_maxTurnPwr);

		if(Math.abs(power) <= k_minPwrCutoff) {
			power = 0;
		}
		
		if(Math.abs(Robot.gyro.getRate()) < k_minSpeedThreshold) {
			stallCount++;
		} else {
			stallCount = 0;
		}

		RobotMap.driveTrainmecanumDrive.driveCartesian(0, 0, power, 0);
	}


	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {
		// if this.time == timer return true
		if((Math.abs(error) < k_marginOfError) || isTimedOut() || (stallCount > k_stallCountThreshold) ) {
			return true;
		}
		return false;
	}


	// Called once after isFinished returns true
	@Override
	protected void end() {
		UsbLogging.printLog("<<< " + this.getClass().getSimpleName() + " ended");
		UsbLogging.printLog("Error: " + error + " StallCount: " + stallCount + " Heading:" + Robot.gyro.getAngleZ());
		RobotMap.driveTrainmecanumDrive.driveCartesian(0, 0, 0, 0);
		RobotMap.driveTrainmecanumDrive.setDeadband(0);
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {
		UsbLogging.printLog("<<< " + this.getClass().getSimpleName() + " interrupted");
	}
}
