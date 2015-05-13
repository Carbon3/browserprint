DROP DATABASE IF EXISTS `lpanopticlick`;
CREATE DATABASE `lpanopticlick`;
USE `lpanopticlick`;

CREATE TABLE `Samples` (
  `IP` TEXT NOT NULL,
  `TimeStamp` DATETIME NOT NULL,
  `SampleID` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `UserAgent` TEXT,
  `AcceptHeaders` TEXT,
  `PluginDetails` TEXT,
  `TimeZone` VARCHAR(5),
  `ScreenDetails` TEXT,
  `Fonts` TEXT,
  `CookiesEnabled` BOOL NOT NULL,
  `SuperCookie` TEXT,
  `DoNotTrack` TEXT,
  `ClockDifference` BIGINT,
  `DateTime` TEXT,
  `MathTan` TEXT,
  `UsingTor` BOOL NOT NULL,
  PRIMARY KEY(`SampleID`)
)
ENGINE=InnoDB;