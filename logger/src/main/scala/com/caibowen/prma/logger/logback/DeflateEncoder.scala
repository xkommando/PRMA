package com.caibowen.prma.logger.logback

import java.io.OutputStream
import java.nio.charset.Charset
import java.util.zip.{GZIPOutputStream, Deflater, DeflaterOutputStream}

import ch.qos.logback.core.encoder.EncoderBase
import com.caibowen.prma.api.model.EventVO

/**
 * Created by Bowen Cai on 1/22/2015.
 */
class DeflateEncoder extends EncoderBase[EventVO] {

  var os: DeflaterOutputStream = _

  override def init(fos: OutputStream): Unit = {
//    os =  new GZIPOutputStream(fos, 16384)
    os = new DeflaterOutputStream(fos, new Deflater(), 16384)
  }

  private[this] val charset = Charset.forName("UTF-8")
  private[this] val strBuf = new java.lang.StringBuilder(8192)

  override def doEncode (event: EventVO): Unit = {
    event.appendJson(strBuf, prettyPrint = false)
    val raw = strBuf.toString.getBytes(charset)
    val byteLen = raw.length
    writeInt(os, byteLen)
    os.write(raw, 0, byteLen)
    strBuf.setLength(0)
  }

  private[this] def writeInt(out: OutputStream, v: Int): Unit = {
    out.write((v >>> 24) & 0xFF)
    out.write((v >>> 16) & 0xFF)
    out.write((v >>> 8) & 0xFF)
    out.write((v >>> 0) & 0xFF)
  }

  override def close: Unit = {
    os.flush()
    os.close()
  }
}
