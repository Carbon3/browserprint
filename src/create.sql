DROP DATABASE IF EXISTS `lpanopticlick`;
CREATE DATABASE `lpanopticlick`;
USE `lpanopticlick`;

CREATE TABLE `Samples` (
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
  PRIMARY KEY(`SampleID`)
)
ENGINE=InnoDB;