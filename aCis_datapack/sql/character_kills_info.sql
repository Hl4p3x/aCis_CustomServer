CREATE TABLE IF NOT EXISTS character_kills_info (
  cycle INT NOT NULL AUTO_INCREMENT,
  cycle_start BIGINT UNSIGNED NOT NULL,
  winner_pvpkills INT UNSIGNED NOT NULL DEFAULT 0,
  winner_pvpkills_count INT UNSIGNED NOT NULL DEFAULT 0,
  winner_pkkills INT UNSIGNED NOT NULL DEFAULT 0,
  winner_pkkills_count INT UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (cycle)
);