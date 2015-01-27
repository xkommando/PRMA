package com.caibowen.prma.logger.logback

import java.io.{DataOutputStream, OutputStream, IOException}
import java.lang.StringBuilder
import java.nio.charset.Charset
import java.util.zip.DeflaterOutputStream

import ch.qos.logback.core.encoder.EncoderBase
import com.caibowen.prma.api.model.EventVO
import com.caibowen.prma.core.zip.Compressor

/**
 * Created by Bowen Cai on 1/14/2015.
 */
class CompressingEncoder(compressor: Compressor) extends EncoderBase[EventVO] {

  var os: DeflaterOutputStream = _

  override def init(os: OutputStream) {
    this.outputStream = os
    this.os = new DeflaterOutputStream(os)
    writeInt(compressor.algorithm)(os)
  }

  private[this] val buf = new Array[Byte](16384)
  private[this] val strBuf = new StringBuilder(16384)

  private[this] val charset = Charset.forName("UTF-8")

  override def doEncode (event: EventVO): Unit = {
    event.appendJson(strBuf, false)
    val raw = strBuf.toString.getBytes(charset)
    val len = compressor.compress(raw, raw.length, buf, 0)
    writeInt(len)(os)
    os.write(buf, 0, len)
    strBuf.setLength(0)
  }

  private[this] def writeInt(v: Int)(implicit out: OutputStream): Unit = {
    out.write((v >>> 24) & 0xFF)
    out.write((v >>> 16) & 0xFF)
    out.write((v >>> 8) & 0xFF)
    out.write((v >>> 0) & 0xFF)
  }

  override def close: Unit = {}
}
