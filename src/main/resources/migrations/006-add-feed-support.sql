ALTER TABLE request
    ADD COLUMN feed_item_path VARCHAR(100),
    ADD COLUMN parent_request_id INT(10),
    ADD CONSTRAINT `FK_REQUEST_REQUEST` FOREIGN KEY (`parent_request_id`)
    REFERENCES `request` (`id`);