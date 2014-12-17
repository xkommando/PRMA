package com.caibowen.prma.analyse.test

import com.zaxxer.hikari.HikariDataSource

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.{GetResult, StaticQuery => SQ}

/**
 * @author BowenCai
 * @since  12/12/2014.
 */
object Q extends App {
  val ds = new HikariDataSource
  ds.setAutoCommit(true)
  ds.setMinimumIdle(2)
  ds.setMaximumPoolSize(32)
  ds.setDriverClassName("com.mysql.jdbc.Driver")
  ds.setUsername("bitranger")
  ds.setPassword("123456")
  ds.setJdbcUrl("jdbc:mysql://localhost:3306/prma_log")
  val db = Database.forDataSource(ds)
  val g = GetResult.createGetTuple3[Long, Int, Int]

  db.withSession { implicit session =>
    val rs = SQ.queryNA[(Long, Int, Int)]("select * from exception where id = -6052837899193958288")
      .foreach(a=>println(a._1, a._2, a._3))
//    println(rs)
//    println(g.children)
  }

//  val q = sql"select * from exception where id = -6052837899193958288".as[(Long, Int, Int)]
//  println(q.apply)
//  SQ.update("")

}
