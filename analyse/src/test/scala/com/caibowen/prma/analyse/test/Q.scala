package com.caibowen.prma.analyse.test
import scala.slick.driver.H2Driver.simple.Database
/**
 * @author BowenCai
 * @since  12/12/2014.
 */
object Q extends App {

  val db = Database.forURL("jdbc:h2:mem:hello", driver = "org.h2.Driver")

  val c = db.createConnection()
  println("ddddddddddd")
}
