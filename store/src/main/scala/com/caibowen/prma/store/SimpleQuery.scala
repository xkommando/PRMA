package com.caibowen.prma.store

import com.caibowen.prma.api.model.EventVO

/**
 * Created by Bowen Cai on 12/26/2014.
 */
trait SimpleQuery {

  def execute(q: String): List[EventVO]
}
