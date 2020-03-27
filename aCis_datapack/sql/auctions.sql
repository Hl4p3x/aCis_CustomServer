CREATE TABLE IF NOT EXISTS `auctions` (
	`clanhall_id` INT UNSIGNED NOT NULL DEFAULT 0,
	`bidder_name` VARCHAR(35) NOT NULL DEFAULT '',
	`clan_oid` INT UNSIGNED NOT NULL DEFAULT 0,
	`clan_name` VARCHAR(20) NOT NULL DEFAULT '',
	`max_bid` INT UNSIGNED NOT NULL DEFAULT 0,
	`time_bid` BIGINT UNSIGNED NOT NULL DEFAULT 0,
	PRIMARY KEY (`clanhall_id`, `clan_oid`)
);