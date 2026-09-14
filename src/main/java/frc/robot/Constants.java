// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.util.Units;

public final class Constants {

  // DRIVE
  public static class OperatorConstants {
    public static final int DRIVER_CONTROLLER_PORT = 0;
    public static final double DEADBAND = 0.1; // drift
  }

  // SWERVE
  public static final double MAXSPEED = Units.feetToMeters(6.5); // 4.5 // MAX: 12.0 - 15.0
}

