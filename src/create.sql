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
  PRIMARY KEY(`SampleID`)
)
ENGINE=InnoDB;

#INSERT INTO `Samples`(`UserAgent`, `AcceptHeaders`, `CookiesEnabled`) VALUES(?, ?, ?);
INSERT INTO `Samples`(`UserAgent`, `AcceptHeaders`, `CookiesEnabled`) VALUES('','',TRUE);

SELECT * FROM `Samples`;

#SELECT TRUE FROM `Samples` WHERE `SampleID` = ? AND `UserAgent` = ? AND `AcceptHeaders` = ? AND `CookiesEnabled` = ?;
SELECT TRUE FROM `Samples` WHERE `SampleID` = 1
 AND `UserAgent` = ''
 AND `AcceptHeaders` = ''
 AND `CookiesEnabled` = TRUE;

SELECT TRUE FROM `Samples` WHERE
 `SampleID` = 59 AND
 `UserAgent` = 'Mozilla/5.0 (X11; Linux x86_64; rv:34.0) Gecko/20100101 Firefox/34.0' AND
 `AcceptHeaders` = 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8 gzip, deflate en-au,en;q=0.7,en-us;q=0.3' AND
 `PluginDetails` = null AND
 `TimeZone` = null AND
 `ScreenDetails` = null AND
 `Fonts` = null AND
 `CookiesEnabled` = true AND
 `SuperCookie` = null;


#SELECT TRUE FROM `Samples` WHERE `SampleID` = ? AND `UserAgent` = ? AND `AcceptHeaders` = ? AND `PluginDetails` = ? AND `TimeZone` = ? AND `ScreenDetails` = ? AND `Fonts` = ? AND `CookiesEnabled` = ? AND `SuperCookie` = ?;
#INSERT INTO `Samples`(`UserAgent`, `AcceptHeaders`, `PluginDetails`, `TimeZone`, `ScreenDetails`, `Fonts`, `CookiesEnabled`, `SuperCookie`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);

SELECT COUNT(*) FROM `Samples` WHERE `ScreenDetails` IS NULL;
SELECT COUNT(*) FROM `Samples` WHERE `UserAgent` = 'Mozilla/5.0 (X11; Linux x86_64; rv:34.0) Gecko/20100101 Firefox/34.0';