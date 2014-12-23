DROP DATABASE IF EXISTS `lpanopticlick`;
CREATE DATABASE `lpanopticlick`;
USE `lpanopticlick`;

CREATE TABLE `Samples` (
  `SampleID` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `UserAgent` TEXT NOT NULL,
  `AcceptHeaders` TEXT NOT NULL,
  `PluginDetails` TEXT,
  `TimeZone` VARCHAR(5),
  `ScreenDetails` TEXT,
  `Fonts` TEXT,
  `CookiesEnabled` BOOL NOT NULL,
  `SuperCookie` TEXT,
  `DoNotTrack` TEXT,
  PRIMARY KEY(`SampleID`)
)
ENGINE=InnoDB;