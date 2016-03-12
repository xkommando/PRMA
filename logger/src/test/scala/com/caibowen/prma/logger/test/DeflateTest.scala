package com.caibowen.prma.logger.test

import java.io._
import java.util.zip.{GZIPInputStream, DataFormatException, Inflater, InflaterInputStream}
import com.caibowen.prma.logger.logback.DeflateEncoder
import com.caibowen.prma.serilaize.json.JSON
import org.junit.{Before, Test}

import scala.io.Source

/**
 * Created by Bowen Cai on 1/22/2015.
 */
class DeflateTest {

  @Test
  def t3: Unit = {
    val fout = new DataOutputStream(new FileOutputStream("SSSSS.txt"))
    Source.fromFile("__TEST__.data","UTF-8").getLines().foreach(fout.writeUTF(_))
  }

  val f2 = new DataOutputStream(new FileOutputStream("__TEST__RAW_FROM_ZIP.data"))

  @Before
  def t1: Unit = {
    val encoder = new DeflateEncoder()
    encoder.init(new FileOutputStream("__TEST__.data"))
    val f2 = new DataOutputStream(new FileOutputStream("__TEST__RAW.data"))
    for (i <- 1 to 200) {
      val vo = LogBackTest.gen
      encoder.doEncode(vo)
      val str = vo.toString
      f2.writeChars(str.length.toString)
      f2.writeChars(str)
    }
    encoder.close
    f2.flush()
    f2.close()
    println("t1 done")
  }

  @Test
  def t2: Unit = {
    sendToDB("__TEST__.data")
    println("t2 done")
  }

  def sendToDB(nameOfFile2zip: String): Unit = {
    val file2zip = new File(nameOfFile2zip)
    if (!file2zip.exists()) {
      return
    }

//    val input = new GZIPInputStream(new FileInputStream(file2zip), 16384)
    val input = new InflaterInputStream(new FileInputStream(file2zip), new Inflater(), 16384)
    val zipbuf = new Array[Byte](16384)
    var inbuf = new Array[Byte](16384)

    var inOff = 0
    var logLen = this.readInt(input)
    // while
    var readLen = input.read(inbuf, 0, inbuf.length)
    var inCount = readLen
    import scala.util.control.Breaks._
    breakable {
      while (readLen > 0) {

        while (logLen + 4 > inCount) {
          val newbuf = new Array[Byte](inbuf.length * 2)
          System.arraycopy(inbuf, 0, newbuf, 0, inCount)
          readLen = input.read(newbuf, inCount, inbuf.length)
          if (readLen <= 0) {
            if (logLen == inCount) {
              val str = new String(inbuf, inOff, logLen)
              val vo = JSON.readEventVO(str)
              f2.writeChars(vo.toString)
              break() // end
            }
            else
              throw new DataFormatException("Incomplete string in the end of file")
          }
          inbuf = newbuf
          inCount += readLen
        }

        do {
          val str = new String(inbuf, inOff, logLen)
          val vo = JSON.readEventVO(str)
          f2.writeChars(vo.toString)

          inOff += logLen
          logLen = intCombine(inbuf(inOff), inbuf(inOff + 1), inbuf(inOff + 2), inbuf(inOff + 3))
          inOff += 4
        } while (inOff + logLen + 4 < inCount)

        val rest = inCount - inOff
        System.arraycopy(inbuf, inOff, inbuf, 0, rest)

        readLen = input.read(inbuf, rest, inbuf.length - rest)
        inCount = rest + readLen
        inOff = 0
      }
    }
  }



  @inline
  def readInt(in: InputStream): Int = {
    val ch1: Int = in.read
    val ch2: Int = in.read
    val ch3: Int = in.read
    val ch4: Int = in.read
    if ((ch1 | ch2 | ch3 | ch4) < 0)
      throw new EOFException(in.toString)
    intCombine(ch1, ch2, ch3, ch4)
  }
    @inline
    def intCombine(ch1: Int, ch2: Int, ch3: Int, ch4: Int): Int
    = (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0)
}
