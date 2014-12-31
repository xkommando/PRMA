package com.caibowen.prma.core.filter

import com.caibowen.gplume.common.collection.{URIPrefixTrie, URISuffixTrie}
import com.caibowen.gplume.misc.Str.Utils._
/**
 * @author BowenCai
 * @since  01/12/2014.
 */
class PartialStrFilter(private[this] val configPath: String) extends StrFilter(configPath) {

  private[this] val NA = new AnyRef
  protected[this] val matchPrefix = new URIPrefixTrie[AnyRef]
  protected[this] val matchSuffix = new URISuffixTrie[AnyRef]

  override protected def parse(ptn: String): Unit = {
    val idx = ptn.indexOf('#')
    if (idx != -1 && isBlank(ptn.substring(0, idx)))
      return

    if (ptn.startsWith("*"))
      matchSuffix.makeBranch(ptn.substring(1, ptn.length), NA)
    else if (ptn.endsWith("*"))
      matchPrefix.makeBranch(ptn.substring(0, ptn.length - 1), NA)
    else
      matchFull.add(ptn)

  }

  @inline
  override def doAccept(str: String): Int = {
    if (matchFull.contains(str)
      || matchPrefix.matchPrefix(str) != null
      || matchSuffix.matchSuffix(str) != null)
      1
    else
      -1
  }

}
