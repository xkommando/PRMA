package com.caibowen.prma.api.model

import java.lang.{StringBuilder => JStrBuilder}

/**
 * Created by Bowen Cai on 1/9/2015.
 */
private[prma] object Helper {

  @inline def hashCombine(obj1: AnyRef, obj2: AnyRef): Long = {
    val h1 = obj1.hashCode.toLong
    val h2 = obj1.hashCode.toLong
    (h1 << 32) | (h2 & 0xFFFFFFFFL)
  }

  @inline def add(a: Short, b: Short) = (a.toInt << 16) | (b.toInt & 0xFFFF)
  @inline def add(a: Int, b: Int) = (a.toLong << 32) | (b.toLong & 0xFFFFFFFFL)
  @inline def part1(c: Long) = (c >> 32).toInt
  @inline def part2(c: Long) = c.toInt
  @inline def part1(c: Int) = (c >> 16).toShort
  @inline def part2(c: Int) = c.toShort

  @inline
  def prettyStackTraceJson(st: StackTraceElement)(implicit json: JStrBuilder): JStrBuilder =
    json.append("{\r\n\t\"file\":\"").append(st.getFileName)
      .append("\",\r\n\t\"className\":\"").append(st.getClassName)
      .append("\",\r\n\t\"function\":\"").append(st.getMethodName)
      .append("\",\r\n\t\"line\":").append(st.getLineNumber).append("\r\n}")

  def stackTraceJson(st: StackTraceElement)(implicit json: JStrBuilder): JStrBuilder =
    json.append("{\"file\":\"").append(st.getFileName)
      .append("\",\"className\":\"").append(st.getClassName)
      .append("\",\"function\":\"").append(st.getMethodName)
      .append("\",\"line\":").append(st.getLineNumber).append("}")


  def appendQuote(s: String)(implicit b: JStrBuilder): JStrBuilder = {
    for (i <- 0 until s.length) {
      s.charAt(i) match {
        case '"' =>  b append '\\' append '\"'
        case '\\' => b append '\\' append '\\'
        case '\b' => b append '\\' append 'b'
        case '\f' => b append '\\' append 'f'
        case '\n' => b append '\\' append 'n'
        case '\r' => b append '\\' append 'r'
        case '\t' => b append '\\' append 't'
        case c if ((c >= '\u0000' && c < '\u0020')) => b append "\\u%04x".format(c: Int)
        case c => b append c
      }
    }
    b
  }

  def unquote(s: String): String = {
    val b = new JStrBuilder(s.length * 4 / 3)
    val len = s.length
    var i = 0
    while (i != len) {
      val c = s.charAt(i)
      if (s.charAt(i) == '\\') {
        i += 1
        s.charAt(i) match {
          case '"' => b.append('"')
          case '\\' => b.append('\\')
          case '/' => b.append('/')
          case 'b' => b.append('\b')
          case 'f' => b.append('\f')
          case 'n' => b.append('\n')
          case 'r' => b.append('\r')
          case 't' => b.append('\t')
          case 'u' =>
            val chars = Array(s.charAt(i + 1), s.charAt(i + 2), s.charAt(i + 3), s.charAt(i + 4))
            val codePoint = Integer.parseInt(new String(chars), 16)
            b.appendCodePoint(codePoint)
            i += 4
          case _ => b.append('\\')
        }
      } else b append c
      i += 1
    }
    b.toString
  }

}
