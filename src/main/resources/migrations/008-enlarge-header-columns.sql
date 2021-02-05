ALTER TABLE `request`
    MODIFY COLUMN `headers` varchar(1000);

ALTER TABLE `response`
    MODIFY COLUMN `headers` varchar(1000);