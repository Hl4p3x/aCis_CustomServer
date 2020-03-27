CREATE TABLE IF NOT EXISTS `gameservers` (
  `server_id` int(11) NOT NULL default '1',
  `hexid` varchar(50) NOT NULL default 'c51c24edcb29504fd4584bc92968e8b',
  `host` varchar(50) NOT NULL default '127.0.0.1',
  PRIMARY KEY (`server_id`)
);