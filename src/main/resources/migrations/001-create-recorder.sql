CREATE TABLE IF NOT EXISTS `record` (
  `id` int(11) AUTO_INCREMENT ,
  `uuid` varchar(40) COLLATE utf8_bin NOT NULL,
  `start` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `end` timestamp DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uuid_index` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `request` (
  `id` int(11) AUTO_INCREMENT ,
  `record_id` int(11) NOT NULL,
  `url` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `period` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `FK_REQUEST_RECORD` (`record_id`),
  CONSTRAINT `FK_REQUEST_RECORD` FOREIGN KEY (`record_id`) REFERENCES `record` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `response` (
  `id` int(11) AUTO_INCREMENT ,
  `body` mediumtext COLLATE utf8_bin NOT NULL,
  `request_id` int(11) NOT NULL,
  `timestamp` timestamp NOT NULL,
  `time_offset` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `FK_RESPONSE_REQUEST` (`request_id`),
  CONSTRAINT `FK_RESPONSE_REQUEST` FOREIGN KEY (`request_id`) REFERENCES `request` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=UTF8_BIN;