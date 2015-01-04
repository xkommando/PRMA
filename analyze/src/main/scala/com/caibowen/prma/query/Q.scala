package com.caibowen.prma.query

import java.sql.ResultSet

import com.caibowen.prma.api.model.{ExceptionVO, EventVO}
import gplume.scala.jdbc.{DBSession, SQLOperation}
import gplume.scala.jdbc.SQLOperation._
/**
 * Created by Bowen Cai on 1/2/2015.
 */
object Q {

  val _tagsByEventID = new SQLOperation("SELECT TG.value FROM `tag` as TG INNER JOIN `j_event_tag` AS JET ON JET.tag_id = TG.id WHERE JET.event_id = ?", null)
  def tagsByEventID(id: Long)(implicit session: DBSession): Set[String] =
    (_tagsByEventID.list(colStr, _.setLong(1, id))(session)).toSet

  val _propsByEventID = new SQLOperation("SELECT PROP.key, PROP.value FROM `property` as PROP INNER JOIN `j_event_prop` AS JEP ON JEP.prop_id = PROP.id WHERE JEP.event_id = ?", null)
  def propsByEventID(id: Long)(implicit session: DBSession): Map[String, String] =
    _propsByEventID.list(rs=>(rs.getString(1), rs.getString(2)), _.setLong(1, id))(session).toMap


  val colStackTrace = (rs: ResultSet) => {
    val line = rs.getInt(4)
    if (line < 0)
      EventVO.NA_ST
    else
      new StackTraceElement(rs.getString(2),
        rs.getString(3),
        rs.getString(1),
        line)
  }
  val _callerStackTrace = new SQLOperation("SELECT SCK.file, SCK.class, SCK.function, SCK.line FROM 'stack_trace' as SCK WHERE SCK.id = ?", null)
  //  String declaringClass, String methodName,
  //  String fileName, int lineNumber) {
  def callerStackTrace(id: Long)(implicit session: DBSession): StackTraceElement =
    _callerStackTrace.single(colStackTrace, _.setLong(1, id))(session).get



  val _exceptStackTraces = new SQLOperation("SELECT SCK.file, SCK.class, SCK.function, SCK.line FROM 'stack_trace' as SCK\n\tINNER JOIN `j_exception_stacktrace` AS JES on JES.stacktrace_id = SCK.id\nWHERE JES.except_id = ?", null)
  def exceptStackTraces(id: Long)(implicit session: DBSession): List[StackTraceElement] =
    _exceptStackTraces.list(colStackTrace, _.setLong(1, id))(session)


  val _exceptByEventID = new SQLOperation("SELECT EXP.id, EXP.name, EXP.msg FROM `exception` AS EXP INNER JOIN j_event_exception AS JEE ON JEE.except_id = EXP.id WHERE JEE.event_id = ?", null)
  def exceptByEventID(id: Long)(implicit session: DBSession): List[ExceptionVO] =
    _exceptByEventID.list(rs=>(rs.getLong(1), rs.getString(2), rs.getString(3)), _.setLong(1, id))(session)
      .map(t=>{
      val stacks = exceptStackTraces(t._1)
      new ExceptionVO(t._1, t._2, if (t._3 == null) None else Some(t._3), if (stacks.size == 0) None else Some(stacks))
    })

}
