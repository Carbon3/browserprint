#MyISAM does't support foreign keys [and therefore cascading]. The FK constraints in the script are just there for show really.
#InnoDB does't support AUTO_INCREMENT in the nice way that MyISAM does [We cannot autoincrement composites].
DROP DATABASE IF EXISTS `lpanopticlick`;
CREATE DATABASE `lpanopticlick`;
USE `lpanopticlick`;

CREATE TABLE `Characteristics` (
  `TypeID` TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `CharacteristicName` VARCHAR(10) NOT NULL,
  UNIQUE(`CharacteristicName`),
  PRIMARY KEY(`TypeID`)
)
ENGINE=InnoDB;

CREATE TABLE `SampleIDs` (
	`SampleID` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	`Cookie` VARCHAR(10),
	UNIQUE(`Cookie`),
	PRIMARY KEY(`SampleID`)
)
ENGINE=InnoDB;

CREATE TABLE `Samples` (
  `SampleID` BIGINT UNSIGNED NOT NULL,
  `TypeID` TINYINT UNSIGNED NOT NULL,
  `SampleValue` TEXT NOT NULL,
  PRIMARY KEY(`SampleID`,`TypeID`),
  FOREIGN KEY(`SampleID`) REFERENCES `SampleIDs`(`SampleID`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY(`TypeID`) REFERENCES `Characteristics`(`TypeID`) ON DELETE CASCADE ON UPDATE CASCADE
)
ENGINE=InnoDB;

INSERT INTO `Characteristics`(`CharacteristicName`)VALUES
('User Agent');

INSERT INTO `SampleIDs` VALUES();
#Get latest SampleID AUTO_INCREMENTED in the connection
#Cookie doesn't need to be used. It