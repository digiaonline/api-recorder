ALTER TABLE response ADD COLUMN type ENUM ("NEW", "OLD") default "NEW";
ALTER TABLE response ADD COLUMN response_code INT default 200;
ALTER TABLE response MODIFY body mediumtext NULL;
ALTER TABLE response ADD COLUMN response_time INT NOT NULL default 0;