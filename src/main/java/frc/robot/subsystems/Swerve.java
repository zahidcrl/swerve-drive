// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import java.io.File;
import java.util.function.Supplier;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import swervelib.parser.SwerveParser;
import swervelib.telemetry.SwerveDriveTelemetry;
import swervelib.telemetry.SwerveDriveTelemetry.TelemetryVerbosity;
import swervelib.SwerveDrive;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;

import static edu.wpi.first.units.Units.Meter;

public class Swerve extends SubsystemBase {
  
  private final Field2d field = new Field2d();

  File directory = new File(Filesystem.getDeployDirectory(),"swerve");
  SwerveDrive  swerveDrive;

  public Swerve() {
    SwerveDriveTelemetry.verbosity = TelemetryVerbosity.HIGH;
    try {
      swerveDrive = new SwerveParser(directory).createSwerveDrive(
        Constants.MAXSPEED,
        new Pose2d(new Translation2d(
          Meter.of(1),
          Meter.of(4)
        ),
        Rotation2d.fromDegrees(0))
      );
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    setupPathPlanner();

    SmartDashboard.putData("Field", field);
  }

  public Command exampleMethodCommand() {
    return runOnce(() -> {});
  }

  public boolean exampleCondition() {
    return false;
  }

  @Override
  public void periodic() {
    swerveDrive.updateOdometry();
    field.setRobotPose(swerveDrive.getPose());
  }

  @Override
  public void simulationPeriodic() {}

  // == Odometry ======================================================= //

  public Pose2d getPose() {
    return swerveDrive.getPose();
  }

  public void resetOdometry(Pose2d pose) {
    swerveDrive.resetOdometry(pose);
  }

  public void setHeading(Rotation2d heading) {
    swerveDrive.resetOdometry(new Pose2d(swerveDrive.getPose().getTranslation(), heading));
  }

  public void zeroGyroWithAlliance() {
    zeroGyro();
  }

  // =================================================================== //

  public void zeroGyro() {
    swerveDrive.zeroGyro();
  }

  public void lock() {
    swerveDrive.lockPose();
  }

  public SwerveDrive getSwerveDrive() {
    return swerveDrive ;
  }

  public void driveFieldOriented(ChassisSpeeds velocity) {
    swerveDrive.driveFieldOriented(velocity);
  }
  
  public Command driveFieldOriented(Supplier<ChassisSpeeds> velocity) {
    return run(() -> {
      swerveDrive.driveFieldOriented(velocity.get());
    });
  }

  // === Path Planner ================================================== //

  public void setupPathPlanner() {
    
    RobotConfig config;
    try {
      config = RobotConfig.fromGUISettings();

      final boolean enableFeedforward = true;
      AutoBuilder.configure(
        swerveDrive::getPose,
        swerveDrive::resetOdometry,
        swerveDrive::getRobotVelocity,
        (speedsRobotRelative, moduleFeedForwards) -> {
          if (enableFeedforward) {
            swerveDrive.drive(
              speedsRobotRelative,
              swerveDrive.kinematics.toSwerveModuleStates(speedsRobotRelative),
              moduleFeedForwards.linearForces()
            );
          } else {
            swerveDrive.setChassisSpeeds(speedsRobotRelative);
          }
        },
        new PPHolonomicDriveController(
          new PIDConstants(5.0, 0.0, 0.0),
          new PIDConstants(5.0, 0.0, 0.0)
        ),
        config,
        () -> {
          var alliance = DriverStation.getAlliance();
          if (alliance.isPresent()) {
            return alliance.get() == DriverStation.Alliance.Red;
          }
          return false;
        },
        this
      );
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // =================================================================== //

  public Command getAutonomousCommand(String pathName) {
    // return new PathPlannerAuto(pathName);
    return null;
  }
}