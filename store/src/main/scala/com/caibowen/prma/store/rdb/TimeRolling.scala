package com.caibowen.prma.store.rdb

import java.sql.Connection

import com.caibowen.gplume.jdbc.JdbcSupport
import com.caibowen.gplume.jdbc.transaction.Transaction
import gplume.scala.conversion.CommonConversions._
import gplume.scala.jdbc.RowMappings
import com.caibowen.prma.api.LogLevel._
import com.caibowen.prma.core.StrLoader
import com.caibowen.prma.store.RollingEvent
import org.slf4j.LoggerFactory
import java.util.{List => JList}
import scala.collection.JavaConversions._

/**
 * @author BowenCai
 * @since  16/12/2014.
 */
class TimeRolling(private[this] val sqls: StrLoader) extends JdbcSupport with RollingEvent {

  private[this] val LOG = LoggerFactory.getLogger(classOf[TimeRolling])


  private[this] val _selectOldIds = sqls.get("TimeRolling.Event.selectOldIds")
  override def deleteOldEntry(minTime: Long): Unit = this.synchronized {
    execute((tnx: Transaction) => {
      LOG.info("Preparing to delete events older than " + minTime)

      val ids = queryList((con: Connection) => {
        val ps = con.prepareStatement(_selectOldIds)
        ps.setLong(1, minTime)
        ps
      }, RowMappings.LONG_MAPPING)

      LOG.info("Find " + ids.size() + " old events")

      deleteRelated(ids)

      LOG.info(ids.size + " event and related data deleted")
    })
  }



  private[this] val _selectLowerIds = sqls.get("TimeRolling.Event.selectLowerIds")
  override def deleteLowLevelEntry(lowerBound: LogLevel): Unit = this.synchronized {
    execute((tnx: Transaction) => {

      LOG.info("Preparing to delete events level lower than " + lowerBound.toString)

      val ids = queryList((con: Connection) => {
        val ps = con.prepareStatement(_selectLowerIds)
        ps.setByte(1, lowerBound.id.toByte)
        ps
      }, RowMappings.LONG_MAPPING)

      LOG.info("Find " + ids.size() + " with level lower than " + lowerBound.toString)

      deleteRelated(ids)

      LOG.info(ids.size + " event and related data deleted")
    })
  }


  private[this] val _delTags= sqls.get("TimeRolling.Event.deleteTag")
  private[this] val _delProps= sqls.get("TimeRolling.Event.deleteProp")
  private[this] val _delEvents= sqls.get("TimeRolling.Event.delete")
  private[this] val _delExcepts= sqls.get("TimeRolling.Except.delete")
  def deleteRelated(ids: JList[Long]): Unit = {

    execute((con: Connection) => {
      val ps = con.prepareStatement(_delTags)
      for (eid <- ids)
        ps.setLong(1, eid)
      ps
    })

    LOG.info("Old tags deleted")

    execute((con: Connection) => {
      val ps = con.prepareStatement(_delProps)
      for (eid <- ids)
        ps.setLong(1, eid)
      ps
    })

    LOG.info("Old properties deleted")

    execute((con: Connection) => {
      val ps = con.prepareStatement(_delExcepts)
      for (eid <- ids)
        ps.setLong(1, eid)
      ps
    })

    LOG.info("Old exceptions deleted")

    execute((con: Connection) => {
      val ps = con.prepareStatement(_delEvents)
      for (eid <- ids)
        ps.setLong(1, eid)
      ps
    })

    LOG.info("Old events deleted. Done")
  }


}
