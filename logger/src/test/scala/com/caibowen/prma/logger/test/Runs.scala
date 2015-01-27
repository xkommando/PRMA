package com.caibowen.prma.logger.test

import java.util
import java.util.concurrent.{TimeUnit, ConcurrentLinkedQueue, Executors}

import com.caibowen.prma.api.model.EventVO
import com.zaxxer.hikari.HikariDataSource
import org.junit.Test

/**
 * @author BowenCai
 * @since  16/12/2014.
 */
object Runs{
  val cq = new ConcurrentLinkedQueue[(Int, Int, Int)]
}
case class  C1(s: String, i: Int)
class C2(val s: String, val i: Int)
class CC3(private var _s: Int){
  def s: Int = _s
  def s_=(i: Int): Unit = {
    println("setter")
    _s = i
  }
}
class Person(private var _name: String) {
  def name = _name                             // accessor
  def name_=(aName: String) {
    println("setter")
    _name = aName
  }  // mutator
}
class Runs {

  @Test
  def t3: Unit = {

    val ss = new CC3(6)
    ss.s = 5
    val p = new Person("Jonathan")
    p.name = "Jony"    // setter
    println(p.name)    // getter
//    import StrictMath._
//    println(log(8) + log(2))
//    println(log(16))
//
//    val c1 = new C1("c1 str", 1)
//    val c2 = new C2("c2 str", 2)
//    for (i <- 1 to 5)
//      println(i)
  }

  @Test
  def r2: Unit = {

    val a = Array(1, 2, 3, 4, 5)
    val b = a.sortWith(_>_)
    println(util.Arrays.toString(b))
    val c = (1, 2, 3)
    println(c)
    println(b.mkString("[", ",", "]"))

    val op = Some(c)
    println(op)
    println(None)

    println("\\u%04x".format(55: Int))
    println("\\u%04x".format(Int.MaxValue))
    println("\\u%04x".format(Int.MinValue))

//    println(Int.MaxValue)
//    println(Long.MaxValue)
//    2147483647
//    9223372036854775807
//    31536000000
  }


  def addALl(props: Int, tags: Int, excepts: Int) = add(excepts, add(tags.toShort, props.toShort))

  private[prma] def add(a: Short, b: Short) = (a.toInt << 16) | (b.toInt & 0xFFFF)

  private[prma] def add(a: Int, b: Int) = (a.toLong << 32) | (b.toLong & 0xFFFFFFFFL)

  @inline private def part1(c: Long) = (c >> 32).toInt
  @inline private def part2(c: Long) = c.toInt
  @inline private def part1(c: Int) = (c >> 16).toShort
  @inline private def part2(c: Int) = c.toShort
}
