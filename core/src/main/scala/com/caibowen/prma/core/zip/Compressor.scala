package com.caibowen.prma.core.zip

/**
 * Created by Bowen Cai on 1/14/2015.
 */
object Compressor {
  /**
   * No compression is used.
   */
  val NO: Int = 0
  /**
   * The LZF compression algorithm is used
   */
  val LZF: Int = 1
  /**
   * The DEFLATE compression algorithm is used.
   */
  val DEFLATE: Int = 2
}
trait Compressor {

  def algorithm: Int

  /**
   * Compress a number of bytes.
   *
   * @param in the input data
   * @param inLen the number of bytes to compress
   * @param out the output area
   * @param outPos the offset at the output array
   * @return the end position
   */
  def compress(in: Array[Byte], inLen: Int, out: Array[Byte], outPos: Int): Int

  /**
   * Expand a number of compressed bytes.
   *
   * @param in the compressed data
   * @param inPos the offset at the input array
   * @param inLen the number of bytes to read
   * @param out the output area
   * @param outPos the offset at the output array
   * @param outLen the size of the uncompressed data
   */
  def expand(in: Array[Byte], inPos: Int, inLen: Int, out: Array[Byte], outPos: Int, outLen: Int): Int

}
