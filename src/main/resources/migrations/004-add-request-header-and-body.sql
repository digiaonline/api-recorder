ALTER TABLE request ADD COLUMN headers VARCHAR(500) NULL;
ALTER TABLE request ADD COLUMN body VARCHAR(1000) NULL;
ALTER TABLE request ADD COLUMN method VARCHAR(10) DEFAULT 'GET';
ALTER TABLE response ADD COLUMN headers VARCHAR(500) NULL;
ALTER TABLE response ADD COLUMN hash TINYBLOB NULL;