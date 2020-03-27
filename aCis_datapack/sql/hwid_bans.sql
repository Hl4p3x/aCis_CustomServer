DROP TABLE IF EXISTS `hwid_bans`;
CREATE TABLE `hwid_bans` (
  `HWID` varchar(32) DEFAULT NULL,
  `HWIDSecond` varchar(32) DEFAULT NULL,
  `expiretime` int(11) NOT NULL DEFAULT '0',
  `comments` varchar(255) DEFAULT '',
  UNIQUE KEY `HWID` (`HWID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
