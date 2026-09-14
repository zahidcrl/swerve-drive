// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Constants.OperatorConstants;
import frc.robot.subsystems.Swerve;
import swervelib.SwerveInputStream;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import com.pathplanner.lib.auto.NamedCommands;

public class RobotContainer {
  private final Swerve drivebase = new Swerve();
  private final CommandXboxController m_driverController = new CommandXboxController(OperatorConstants.DRIVER_CONTROLLER_PORT);

  public RobotContainer() {
    configureBindings();
    DriverStation.silenceJoystickConnectionWarning(true);
    drivebase.setDefaultCommand(drivefieldOrientedDirectVelocity);
    NamedCommands.registerCommand("test", Commands.print("Hello World"));
  }

  SwerveInputStream driveAngularVelocity = SwerveInputStream.of(
    drivebase.getSwerveDrive(),
    () -> m_driverController.getLeftY(),  // FRONT / BACK
    () -> m_driverController.getLeftX()   // LEFT / RIGHT
  )
  .withControllerRotationAxis(() -> m_driverController.getRightX())
  .deadband(OperatorConstants.DEADBAND)
  .scaleTranslation(0.8)
  .allianceRelativeControl(false);

  SwerveInputStream driveDirectAngle = driveAngularVelocity.copy()
  .withControllerHeadingAxis(
    () -> m_driverController.getRightY(),
    () -> m_driverController.getRightX()
  )
  .headingWhile(true);

  Command drivefieldOrientedDirectAngule = drivebase.driveFieldOriented(driveDirectAngle);
  Command drivefieldOrientedDirectVelocity = drivebase.driveFieldOriented(driveAngularVelocity);

  private void configureBindings() {
    m_driverController.b().onTrue(new InstantCommand(drivebase::zeroGyroWithAlliance));
    m_driverController.x().whileTrue(new InstantCommand(drivebase::lock, drivebase));
  }

  public Command getAutonomousCommand() {
    // return drivebase.getAutonomousCommand("New Auto");
    return null;
  }
}
