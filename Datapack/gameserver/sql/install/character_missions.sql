DROP TABLE IF EXISTS character_missions;
CREATE TABLE IF NOT EXISTS character_missions (
  `char_id`  INT UNSIGNED NOT NULL ,
  `mission_id`  INT UNSIGNED NOT NULL ,
  `status`  ENUM ('AVAILABLE', 'NOT_AVAILABLE', 'COMPLETED') NOT NULL DEFAULT 'NOT_AVAILABLE' ,
  `progress`  INT UNSIGNED NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`char_id`, `mission_id`),
  INDEX IDX_MISSION (`mission_id`),
  FOREIGN KEY FK_MISSION_CHARACTER (`char_id`) REFERENCES characters (`charId`) ON DELETE CASCADE
);
