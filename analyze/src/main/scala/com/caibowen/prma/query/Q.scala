package com.caibowen.prma.query

import java.sql.ResultSet

import com.caibowen.prma.api.model.{ExceptionVO, EventVO}
import gplume.scala.jdbc.{DBSession, SQLOperation}
import gplume.scala.jdbc.SQLOperation._
/**
 * Created by Bowen Cai on 1/2/2015.
 */
object Q {

  private val _logggerNameByID = new SQLOperation("SELECT `value` FROM `logger` WHERE id=?")
  def logggerNameByID(id: Int)(implicit session: DBSession): Option[String] =
  _logggerNameByID.first(colStr, _.setInt(1, id))(session)

  private val _threadNameByID = new SQLOperation("SELECT `value` FROM `thread` WHERE id=?")
  def threadNameByID(id: Int)(implicit session: DBSession): Option[String] =
    _threadNameByID.first(colStr, _.setInt(1, id))(session)

  private val _tagsByEventID = new SQLOperation("SELECT TG.value FROM `tag` as TG INNER JOIN `j_event_tag` AS JET ON JET.tag_id = TG.id WHERE JET.event_id = ?")
  def tagsByEventID(id: Long)(implicit session: DBSession): Set[String] =
    _tagsByEventID.vector(colStr, _.setLong(1, id))(session).toSet

  private val _propsByEventID = new SQLOperation("SELECT PROP.key, PROP.value FROM `property` as PROP INNER JOIN `j_event_prop` AS JEP ON JEP.prop_id = PROP.id WHERE JEP.event_id = ?")
  def propsByEventID(id: Long)(implicit session: DBSession): Map[String, String] =
    _propsByEventID.map(rs=>(rs.getString(1), rs.getString(2)), _.setLong(1, id))(session)


  //      (String declaringClass, String methodName,
  //        String fileName, int lineNumber)
  private val colStackTrace = (rs: ResultSet) => {
    val line = rs.getInt(4)
    if (line <= 0)
      EventVO.NA_ST
    else
      new StackTraceElement(rs.getString(2),
        rs.getString(3),
        rs.getString(1),
        line)
  }
  private val _stackTraceByID = new SQLOperation("SELECT SCK.file, SCK.class, SCK.function, SCK.line FROM 'stack_trace' as SCK WHERE SCK.id = ?")
  //  String declaringClass, String methodName,
  //  String fileName, int lineNumber) {
  def stackTraceByID(id: Long)(implicit session: DBSession): StackTraceElement =
    _stackTraceByID.first(colStackTrace, before=_.setLong(1, id))(session).get


  private val _exceptStackTraces = new SQLOperation("SELECT SCK.file, SCK.class, SCK.function, SCK.line FROM 'stack_trace' as SCK INNER JOIN `j_exception_stacktrace` AS JES on JES.stacktrace_id = SCK.id WHERE JES.except_id = ?")
  def exceptStackTraces(id: Long)(implicit session: DBSession): Vector[StackTraceElement] =
    _exceptStackTraces.vector(colStackTrace, _.setLong(1, id))(session)


  private val _exceptByEventID = new SQLOperation("SELECT EXP.id, EXP.name, EXP.msg FROM `exception` AS EXP INNER JOIN j_event_exception AS JEE ON JEE.except_id = EXP.id WHERE JEE.event_id = ?")
  def exceptByEventID(id: Long)(implicit session: DBSession): Vector[ExceptionVO] =
    _exceptByEventID.vector(rs=>(rs.getLong(1), rs.getString(2), rs.getString(3)), _.setLong(1, id))(session)
      .map(t=>{
      val stacks = exceptStackTraces(t._1)
      new ExceptionVO(t._1, t._2,Option(t._3), if (stacks.size == 0) None else Some(stacks))
    })

}
