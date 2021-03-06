SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

DROP SCHEMA IF EXISTS `prma_log` ;
CREATE SCHEMA IF NOT EXISTS `prma_log` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci ;
USE `prma_log` ;

-- -----------------------------------------------------
-- Table `prma_log`.`thread_name`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `prma_log`.`thread_name` ;

CREATE TABLE IF NOT EXISTS `prma_log`.`thread_name` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `value` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `prma_log`.`logger_name`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `prma_log`.`logger_name` ;

CREATE TABLE IF NOT EXISTS `prma_log`.`logger_name` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `value` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  FULLTEXT INDEX `idx_loggername_fulltxt` (`value` ASC))
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `prma_log`.`exception_msg`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `prma_log`.`exception_msg` ;

CREATE TABLE IF NOT EXISTS `prma_log`.`exception_msg` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `value` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  FULLTEXT INDEX `idx_exceptname_fulltxt` (`value` ASC))
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `prma_log`.`stack_trace`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `prma_log`.`stack_trace` ;

CREATE TABLE IF NOT EXISTS `prma_log`.`stack_trace` (
  `id` INT NOT NULL,
  `file` VARCHAR(255) NOT NULL,
  `class` VARCHAR(255) NULL,
  `function` VARCHAR(255) NOT NULL,
  `line` INT NOT NULL,
  PRIMARY KEY (`id`))
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `prma_log`.`exception_name`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `prma_log`.`exception_name` ;

CREATE TABLE IF NOT EXISTS `prma_log`.`exception_name` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `value` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  FULLTEXT INDEX `idx_exceptname_fulltxt` (`value` ASC))
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `prma_log`.`exception`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `prma_log`.`exception` ;

CREATE TABLE IF NOT EXISTS `prma_log`.`exception` (
  `id` BIGINT(20) NOT NULL,
  `name_id` INT NOT NULL,
  `msg_id` INT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_except_exp_msg_idx` (`msg_id` ASC),
  INDEX `fk_except_exp_name_idx` (`name_id` ASC))
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `prma_log`.`j_exception_stacktrace`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `prma_log`.`j_exception_stacktrace` ;

CREATE TABLE IF NOT EXISTS `prma_log`.`j_exception_stacktrace` (
  `seq` INT NOT NULL,
  `stacktrace_id` INT NOT NULL,
  `except_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`seq`, `except_id`, `stacktrace_id`),
  INDEX `fk_j_except_event_except_idx` (`except_id` ASC),
  INDEX `idx_j_except_ev_seq` USING BTREE (`except_id` ASC, `seq` ASC),
  INDEX `fk_j_except_stacktrace_ev0_idx` (`stacktrace_id` ASC, `except_id` ASC))
  ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `prma_log`.`event`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `prma_log`.`event` ;

CREATE TABLE IF NOT EXISTS `prma_log`.`event` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `time_created` BIGINT(20) NOT NULL,
  `level` TINYINT(4) NOT NULL,
  `logger_id` INT(11) NOT NULL,
  `thread_id` INT(11) NOT NULL,
  `caller_id` BIGINT(20) NULL,
  `flag` BIGINT(20) NOT NULL,
  `message` VARCHAR(2047) NOT NULL,
  `reserved` BIGINT(20) NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_event_st_idx` (`caller_id` ASC),
  INDEX `fk_event_logger_idx` (`logger_id` ASC),
  INDEX `fk_event_thread_idx` (`thread_id` ASC),
  INDEX `idx_event_time` USING BTREE (`time_created` ASC),
  FULLTEXT INDEX `idx_event_fulltext` (`message` ASC))
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `prma_log`.`property`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `prma_log`.`property` ;

CREATE TABLE IF NOT EXISTS `prma_log`.`property` (
  `id` INT NOT NULL,
  `key` VARCHAR(255) NOT NULL,
  `value` BLOB NOT NULL,
  PRIMARY KEY (`id`),
  FULLTEXT INDEX `idx_prop_fulltxt` (`key` ASC))
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `prma_log`.`j_event_prop`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `prma_log`.`j_event_prop` ;

CREATE TABLE IF NOT EXISTS `prma_log`.`j_event_prop` (
  `prop_id` INT(11) NOT NULL,
  `event_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`event_id`, `prop_id`),
  INDEX `fk_j_prop_event_prop_idx` (`prop_id` ASC))
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `prma_log`.`j_event_exception`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `prma_log`.`j_event_exception` ;

CREATE TABLE IF NOT EXISTS `prma_log`.`j_event_exception` (
  `seq` INT(11) NOT NULL,
  `event_id` BIGINT(20) NOT NULL,
  `except_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`seq`, `except_id`, `event_id`),
  INDEX `fk_j_except_event_ev_idx` (`event_id` ASC),
  INDEX `fk_j_except_event_except_idx` (`except_id` ASC),
  INDEX `idx_j_except_ev_seq` USING BTREE (`except_id` ASC, `seq` ASC))
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `prma_log`.`marker_name`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `prma_log`.`marker_name` ;

CREATE TABLE IF NOT EXISTS `prma_log`.`marker_name` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `value` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `prma_log`.`j_event_marker`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `prma_log`.`j_event_marker` ;

CREATE TABLE IF NOT EXISTS `prma_log`.`j_event_marker` (
  `marker_id` INT(11) NOT NULL,
  `event_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`event_id`, `marker_id`),
  INDEX `fk_j_marker_event_ev0_idx` (`marker_id` ASC))
  ENGINE = InnoDB;




SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
