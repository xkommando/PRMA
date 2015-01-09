package com.caibowen.prma.analyse.test

import java.util.Date

import com.caibowen.prma.query.{Statistician, Q}
import com.zaxxer.hikari.HikariDataSource
import gplume.scala.jdbc.DB

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.{GetResult, StaticQuery => SQ}

/**
 * @author BowenCai
 * @since  12/12/2014.
 */
object TestQuery extends App {
  val ds = new HikariDataSource
  ds.setAutoCommit(true)
  ds.setMinimumIdle(2)
  ds.setMaximumPoolSize(32)
  ds.setDriverClassName("com.mysql.jdbc.Driver")
  ds.setUsername("xKommando")
  ds.setPassword("123456")
  ds.setJdbcUrl("jdbc:mysql://localhost:3306/prma_log_event")

  val db = new DB(ds)
  db.readOnlySession{implicit session=>
    val r3 = Statistician.timelineCounter(1L, 1520727416662L)
    if (r3 != null) {
      println(r3._1)
      println(r3._2)
      println(r3._3)
    }
  }
//  val db = Database.forDataSource(ds)
//  val g = GetResult.createGetTuple3[Long, Int, Int]
//
//  db.withSession { implicit session =>
//    val rs = SQ.queryNA[(Long, Int, Int)]("select * from exception where id = -6052837899193958288")
//      .foreach(a=>println(a._1, a._2, a._3))
////    println(rs)
////    println(g.children)
//  }

//  val q = sql"select * from exception where id = -6052837899193958288".as[(Long, Int, Int)]
//  println(q.apply)
//  SQ.update("")

}
