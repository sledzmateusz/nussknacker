package pl.touk.nussknacker.ui.listener

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import pl.touk.nussknacker.engine.util.loader.ScalaServiceLoader
import pl.touk.nussknacker.ui.security.api.LoggedUser
import pl.touk.nussknacker.ui.listener.services.NussknackerServices

import scala.concurrent.ExecutionContext

trait ProcessChangeListenerFactory {
  def create(config: Config, services: NussknackerServices): ProcessChangeListener
}

object ProcessChangeListenerFactory extends LazyLogging {
  def serviceLoader(classLoader: ClassLoader): ProcessChangeListenerFactory = {
    new ProcessChangeListenerAggregatingFactory(ScalaServiceLoader.load[ProcessChangeListenerFactory](classLoader): _*)
  }
}

class ProcessChangeListenerAggregatingFactory(val factories: ProcessChangeListenerFactory*) extends ProcessChangeListenerFactory with LazyLogging {
  final override def create(config: Config, services: NussknackerServices): ProcessChangeListener = {
    val listeners = factories.map(_.create(config, services))
    new ProcessChangeListener {
      override def handle(event: ProcessChangeEvent)(implicit ec: ExecutionContext, user: LoggedUser): Unit = {
        def handleSafely(listener: ProcessChangeListener): Unit = {
          try {
            listener.handle(event)
          } catch {
            case ex: Throwable => logger.error(s"Error while handling event $event by listener ${listener.getClass.getName}", ex)
          }
        }

        listeners.foreach(handleSafely)
      }
    }
  }
}
