package com.caibowen.prma.store

import java.io._
import java.util.zip.{DataFormatException, Inflater, InflaterInputStream}

import akka.actor.{ActorLogging, Actor, ActorRef}
import com.caibowen.prma.core.JSON

/**
 * Created by Bowen Cai on 1/19/2015.
 */
class FileLogLoader(store: ActorRef) extends Actor with ActorLogging {

  def sendToDB(nameOfFile2zip: String): Unit = {
    val file2zip = new File(nameOfFile2zip)
    if (!file2zip.exists()) {
      log.error("The file to compress named [" + nameOfFile2zip + "] does not exist.")
      return
    }
    
    val input = new InflaterInputStream(new FileInputStream(file2zip), new Inflater(), 16384)
    val zipbuf = new Array[Byte](16384)
    var inbuf = new Array[Byte](16384)

    var inOff = 0
    var logLen = readInt(input)
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
              store.tell(vo, store)
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
          store.tell(vo, store)

          inOff += logLen
          logLen = intCombine(inbuf(logLen), inbuf(logLen + 1), inbuf(logLen + 2), inbuf(logLen + 3))
          inOff += 4
        } while (inOff + logLen + 4 < inCount)

        val rest = inCount - inOff
        System.arraycopy(inbuf, inOff, inbuf, 0, rest)

        readLen = input.read(inbuf, rest, inbuf.length - rest)
        inCount = rest + readLen
        inOff = 0
      }
    }

    // -----------------------------------------


  }

  def store(buf: Array[Byte], start: Int, end: Int): Unit = {

    val str = new String(buf, start, end)
    val vo = JSON.readEventVO(str)
    store.tell(vo, store)
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

  def receive = {
    case name: String => sendToDB(name)
    case x => unhandled(x)
  }
}
