package com.caibowen.prma.core.zip

import java.util.zip.{DataFormatException, Inflater, Deflater}


/**
 * Created by Bowen Cai on 1/14/2015.
 */
class DeflateCompressor(var strategy: Int, var level: Int) extends Compressor {

  def this() {
    this(Deflater.DEFAULT_STRATEGY, Deflater.DEFAULT_COMPRESSION)
  }
  def algorithm = Compressor.DEFLATE

  /**
   * Compress a number of bytes.
   *
   * @param in the input data
   * @param inLen the number of bytes to compress
   * @param out the output area
   * @param outPos the offset at the output array
   * @return the end position
   */
  def compress(in: Array[Byte], inLen: Int, out: Array[Byte], outPos: Int): Int = {
    val deflater = new Deflater(level)
    deflater.setInput(in, 0, inLen)
    deflater.finish()
    val compressed = deflater.deflate(out, outPos, out.length - outPos)
    while (compressed == 0) {
      strategy = Deflater.DEFAULT_STRATEGY
      level = Deflater.DEFAULT_COMPRESSION
      return compress(in, inLen, out, outPos)
    }
    deflater.end()
    outPos + compressed
  }

  /**
   *
   * @param in the compressed data
   * @param inPos the offset at the input array
   * @param inLen the number of bytes to read
   * @param out the output area
   * @param outPos the offset at the output array
   * @param outLen the size of the uncompressed data
   */
  def expand(in: Array[Byte], inPos: Int, inLen: Int, out: Array[Byte], outPos: Int, outLen: Int): Int = {
    val decompresser = new Inflater
    decompresser.setInput(in, inPos, inLen)
    decompresser.finished
    val len = decompresser.inflate(out, outPos, outLen)
    decompresser.end()
    len
  }

}
