package com.caibowen.prma.query

import com.caibowen.prma.api.LogLevel
import com.caibowen.prma.api.LogLevel.{LogLevel => LevelT}
import scala.slick.driver.MySQLDriver.simple._
import scala.slick.lifted.{ForeignKeyQuery, ProvenShape}

/**
 * @author BowenCai
 * @since  12/12/2014.
 */
class LogTag(tag: Tag) extends Table[(Int, String)](tag, "tag") {
  def id = column[Int]("ID", O.PrimaryKey)
  def value = column[String]("VALUE")
  def * = (id, value)
}

class StackTrace(tag: Tag) extends Table[(String, String, String, Int)](tag, "stack_trace") {
  def id = column[Int]("id", O.PrimaryKey)
  def file = column[String]("file")
  def className = column[String]("class")
  def function = column[String]("function")
  def line = column[Int]("line")
  def * = (file, className, function, line)
}

class Property(tag: Tag) extends Table[(String, String)](tag, "property") {
  def id = column[Int]("ID", O.PrimaryKey)
  def key = column[String]("key")
  def value = column[String]("VALUE")
  def * = (key, value)

}

class Except(tag: Tag) extends Table[(Long, String, String)](tag, "exception") {
  def id = column[Long]("ID", O.PrimaryKey)
  def name = column[String]("name")
  def message = column[String]("msg")

  def * = (id, name, message)
}

class Event(tag: Tag) extends Table[(Long, Long, LevelT, String, String, Long, Option[Long], Int)](tag, "event") {
  implicit val levelMapper = MappedColumnType.base[LevelT, Int](
  { e => e.id
  }, {
    i => LogLevel.from(i)
  }
  )

  def id = column[Long]("ID", O.PrimaryKey)

  def timeCreated = column[Long]("time_created")

  def level = column[LevelT]("level")

  def loggerName = column[String]("logger")

  def threadName = column[String]("thread")

  def flag = column[Long]("flag")

  def reserved = column[Option[Long]]("reserved")

  def _stackTraceID = column[Int]("caller_id")

  def callerStackTrace: ForeignKeyQuery[StackTrace, (String, String, String, Int)]
  = foreignKey("", _stackTraceID, TableQuery[StackTrace])(_.id)

  def message = column[String]("message")

  def * : ProvenShape[(Long, Long, LevelT, String, String, Long, Option[Long], Int)]
  = (id, timeCreated, level, loggerName, threadName, flag, reserved, _stackTraceID)
}