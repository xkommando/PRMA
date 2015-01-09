-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema prma_log_event
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `prma_log_event` ;

-- -----------------------------------------------------
-- Schema prma_log_event
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `prma_log_event` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci ;
USE `prma_log_event` ;

-- -----------------------------------------------------
-- Table `prma_log_event`.`stack_trace`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `prma_log_event`.`stack_trace` ;

CREATE TABLE IF NOT EXISTS `prma_log_event`.`stack_trace` (
  `id` INT NOT NULL,
  `file` VARCHAR(255) NOT NULL,
  `class` VARCHAR(255) NULL,
  `function` VARCHAR(255) NOT NULL,
  `line` INT NOT NULL,
  PRIMARY KEY (`id`))
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `prma_log_event`.`exception`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `prma_log_event`.`exception` ;

CREATE TABLE IF NOT EXISTS `prma_log_event`.`exception` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `msg` VARCHAR(255) NULL,
  PRIMARY KEY (`id`))
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `prma_log_event`.`logger`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `prma_log_event`.`logger` ;

CREATE TABLE IF NOT EXISTS `prma_log_event`.`logger` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `value` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `prma_log_event`.`thread`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `prma_log_event`.`thread` ;

CREATE TABLE IF NOT EXISTS `prma_log_event`.`thread` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `value` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `prma_log_event`.`event`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `prma_log_event`.`event` ;

CREATE TABLE IF NOT EXISTS `prma_log_event`.`event` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `time_created` BIGINT(20) NOT NULL,
  `level` TINYINT(4) NOT NULL,
  `logger_id` INT NOT NULL,
  `thread_id` INT NOT NULL,
  `caller_id` INT NOT NULL,
  `flag` BIGINT(20) NOT NULL,
  `message` VARCHAR(2047) NOT NULL,
  `reserved` BIGINT(20) NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_event_st_idx` (`caller_id` ASC),
  INDEX `idx_event_time` USING BTREE (`time_created` ASC),
  FULLTEXT INDEX `idx_event_fulltext` (`message` ASC),
  INDEX `fk_event_logger_idx` (`logger_id` ASC),
  INDEX `fk_event_thread_idx` (`thread_id` ASC),
  CONSTRAINT `fk_event_st`
  FOREIGN KEY (`caller_id`)
  REFERENCES `prma_log_event`.`stack_trace` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_event_logger`
  FOREIGN KEY (`logger_id`)
  REFERENCES `prma_log_event`.`logger` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_event_thread`
  FOREIGN KEY (`thread_id`)
  REFERENCES `prma_log_event`.`thread` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `prma_log_event`.`property`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `prma_log_event`.`property` ;

CREATE TABLE IF NOT EXISTS `prma_log_event`.`property` (
  `id` INT NOT NULL,
  `key` VARCHAR(255) NOT NULL,
  `value` BLOB NOT NULL,
  PRIMARY KEY (`id`),
  FULLTEXT INDEX `idx_prop_fulltxt` (`key` ASC))
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `prma_log_event`.`j_event_prop`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `prma_log_event`.`j_event_prop` ;

CREATE TABLE IF NOT EXISTS `prma_log_event`.`j_event_prop` (
  `prop_id` INT(11) NOT NULL,
  `event_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`event_id`, `prop_id`),
  INDEX `fk_j_prop_event_prop_idx` (`prop_id` ASC),
  CONSTRAINT `fk_j_prop_event_ev`
  FOREIGN KEY (`event_id`)
  REFERENCES `prma_log_event`.`event` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_j_prop_event_prop`
  FOREIGN KEY (`prop_id`)
  REFERENCES `prma_log_event`.`property` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `prma_log_event`.`j_event_exception`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `prma_log_event`.`j_event_exception` ;

CREATE TABLE IF NOT EXISTS `prma_log_event`.`j_event_exception` (
  `seq` INT(11) NOT NULL,
  `event_id` BIGINT(20) NOT NULL,
  `except_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`seq`, `except_id`, `event_id`),
  INDEX `fk_j_except_event_ev_idx` (`event_id` ASC),
  INDEX `fk_j_except_event_except_idx` (`except_id` ASC),
  INDEX `idx_j_except_ev_seq` USING BTREE (`except_id` ASC, `seq` ASC),
  CONSTRAINT `fk_j_except_event_ev`
  FOREIGN KEY (`event_id`)
  REFERENCES `prma_log_event`.`event` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_j_except_event_except`
  FOREIGN KEY (`except_id`)
  REFERENCES `prma_log_event`.`exception` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `prma_log_event`.`tag`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `prma_log_event`.`tag` ;

CREATE TABLE IF NOT EXISTS `prma_log_event`.`tag` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `value` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `prma_log_event`.`j_event_tag`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `prma_log_event`.`j_event_tag` ;

CREATE TABLE IF NOT EXISTS `prma_log_event`.`j_event_tag` (
  `tag_id` INT(11) NOT NULL,
  `event_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`event_id`, `tag_id`),
  INDEX `fk_j_marker_event_ev0_idx` (`tag_id` ASC),
  CONSTRAINT `fk_j_marker_event_ev0`
  FOREIGN KEY (`tag_id`)
  REFERENCES `prma_log_event`.`tag` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_j_marker_event_marker0`
  FOREIGN KEY (`event_id`)
  REFERENCES `prma_log_event`.`event` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `prma_log_event`.`j_exception_stacktrace`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `prma_log_event`.`j_exception_stacktrace` ;

CREATE TABLE IF NOT EXISTS `prma_log_event`.`j_exception_stacktrace` (
  `seq` INT NOT NULL,
  `stacktrace_id` INT NOT NULL,
  `except_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`seq`, `except_id`, `stacktrace_id`),
  INDEX `fk_j_except_event_except_idx` (`except_id` ASC),
  INDEX `idx_j_except_ev_seq` USING BTREE (`except_id` ASC, `seq` ASC),
  INDEX `fk_j_except_stacktrace_ev0_idx` (`stacktrace_id` ASC),
  CONSTRAINT `fk_j_except_stacktrace_ev0`
  FOREIGN KEY (`stacktrace_id`)
  REFERENCES `prma_log_event`.`stack_trace` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_j_except_stacktrace_except0`
  FOREIGN KEY (`except_id`)
  REFERENCES `prma_log_event`.`exception` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
