DROP DATABASE IF EXISTS `lpanopticlick`;
CREATE DATABASE `lpanopticlick`;
USE `lpanopticlick`;

CREATE TABLE `Samples` (
  `IP` TEXT NOT NULL,
  `TimeStamp` DATETIME NOT NULL,
  `SampleID` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `UserAgent` TEXT,
  `AcceptHeaders` TEXT,
  `Platform` TEXT,
  `PlatformFlash` TEXT,
  `PluginDetails` TEXT,
  `TimeZone` INT,
  `ScreenDetails` TEXT,
  `ScreenDetailsFlash` TEXT,
  `LanguageFlash` TEXT,
  `Fonts` TEXT,
  `CookiesEnabled` BOOL NOT NULL,
  `SuperCookie` TEXT,
  `DoNotTrack` TEXT,
  `ClockDifference` BIGINT,
  `DateTime` TEXT,
  `MathTan` TEXT,
  `UsingTor` BOOL NOT NULL,
  `AdsBlocked` BOOL,
  `Canvas` TEXT,
  `WebGLVendor` TEXT,
  `WebGLRenderer` TEXT,
  PRIMARY KEY(`SampleID`)
)
ENGINE=InnoDB;

CREATE TABLE `SampleSets` (
  `SampleSetID` INT NOT NULL AUTO_INCREMENT,
  `SampleID` BIGINT UNSIGNED NOT NULL,
  FOREIGN KEY(`SampleID`) REFERENCES `Samples`(`SampleID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  PRIMARY KEY(`SampleSetID`,`SampleID`)
)
ENGINE=InnoDB;