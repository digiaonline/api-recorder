CREATE TABLE IF NOT EXISTS `response_body` (
  `id` int(11) AUTO_INCREMENT,
  `hash` varchar(50) NULL,
  `body` mediumtext COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `hash` (`hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

ALTER TABLE `response`
	ADD COLUMN `response_body_id` int(11) ,
	ADD CONSTRAINT `FK_RESPONSE_RESPONSE_BODY` FOREIGN KEY (`response_body_id`)
	REFERENCES `response_body` (`id`);

ALTER TABLE `response`
    CHANGE COLUMN `body` `custom_body` mediumtext;

ALTER TABLE `response` DROP COLUMN `hash`;