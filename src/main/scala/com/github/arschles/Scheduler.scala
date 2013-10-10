package com.github.arschles

import java.lang.Runnable
import java.util.concurrent.{ThreadFactory, Executors}
import java.util.concurrent.atomic._
import scala.concurrent._

/**
 * a scheduler for executing functions in a thread pool
 * @param name the name of this scheduler
 * @param maxThreads the maximum number of threads this scheduler should use
 */
class Scheduler(name: String, maxThreads: Int) {
  private val numThreads = new AtomicInteger(0)

  private lazy val factory = new ThreadFactory {
    override def newThread(r: Runnable): Thread = {
      val t = new Thread(r)
      t.setDaemon(true)
      t.setName(s"$name-scheduler-${numThreads.incrementAndGet()}")
      t
    }
  }

  private lazy val exSvc = Executors.newFixedThreadPool(maxThreads, factory)
  private implicit lazy val exCtx = ExecutionContext.fromExecutorService(exSvc)

  //TODO: add a name for each operation and encapsulate inside an Operation object
  def apply(ops: (() => Unit) *): Future[Unit] = {
    val futures = ops.map { op =>
      Future(op())
    }
    Future.sequence(futures).map { _ =>
      ()
    }
  }
}
