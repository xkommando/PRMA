package com.caibowen.prma.logger.test

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
class Runs {


  @Test
  def r2: Unit = {

  }


  def addALl(props: Int, tags: Int, excepts: Int) = add(excepts, add(tags.toShort, props.toShort))

  private[prma] def add(a: Short, b: Short) = (a.toInt << 16) | (b.toInt & 0xFFFF)

  private[prma] def add(a: Int, b: Int) = (a.toLong << 32) | (b.toLong & 0xFFFFFFFFL)

  @inline private def part1(c: Long) = (c >> 32).toInt
  @inline private def part2(c: Long) = c.toInt
  @inline private def part1(c: Int) = (c >> 16).toShort
  @inline private def part2(c: Int) = c.toShort
}
