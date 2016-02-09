CREATE  TABLE `db` (

  `card` VARCHAR(20) NOT NULL ,
  `pin` VARCHAR(4) NOT NULL ,
  `dbcol` VARCHAR(45) NULL ,
  `current_bal` VARCHAR(45) NULL ,
  PRIMARY KEY (`card`) 
);

INSERT INTO `db` (`card`, `pin`, `dbcol`, `current_bal`) 
	VALUES ('12345', '1234', '12333', '22222');

INSERT INTO `db` (`card`, `pin`, `dbcol`, `current_bal`) 
	VALUES ('23456', '1234', '12211', '223312');

INSERT INTO `db` (`card`, `pin`, `dbcol`, `current_bal`) 
	VALUES ('32312', '1111', '0', '0');

INSERT INTO `db` (`card`, `pin`, `dbcol`, `current_bal`) 
	VALUES ('11111', '1111', '11111', '11111');
