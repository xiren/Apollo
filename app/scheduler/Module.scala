package scheduler

import com.google.inject.AbstractModule
import com.google.inject.name.Names


class Module extends AbstractModule{
  override def configure(): Unit = {
    bind(classOf[SchedulerJob]).annotatedWith(Names.named("schedulerJob")).to(classOf[SchedulerJob]).asEagerSingleton()
  }
}

