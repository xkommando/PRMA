import java.util

import com.caibowen.prma.monitor.MonitorBuilder
import com.caibowen.prma.monitor.eval.LevelEval
import com.caibowen.prma.monitor.notify.Notifier
import org.junit.Test
import akka.actor.ActorSystem
/**
 * @author BowenCai
 * @since  10/12/2014.
 */
class MonitorTest {

  @Test
  def t1(): Unit = {
    val b = new MonitorBuilder
    b.evaluator = new LevelEval(Int.MinValue, Int.MaxValue)
    b.notifiers = util.Collections.emptyList().asInstanceOf[util.List[Notifier]]
    val system = ActorSystem("test-prma-actor")
    val monitorRef = b.buildWith(system)

    println(monitorRef)
  }
}
