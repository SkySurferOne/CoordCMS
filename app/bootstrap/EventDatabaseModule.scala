package bootstrap

import com.google.inject.AbstractModule

class EventDatabaseModule extends AbstractModule  {
  protected def configure: Unit = {
    bind(classOf[InitialData]).asEagerSingleton()
  }
}
