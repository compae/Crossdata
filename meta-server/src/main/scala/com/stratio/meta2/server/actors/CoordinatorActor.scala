package com.stratio.meta2.server.actors

import com.stratio.meta2.core.coordinator.Coordinator
import com.stratio.meta2.core.query
import akka.actor.{ Props, ActorLogging, Actor, ActorRef }
import akka.cluster.ClusterEvent._
import com.stratio.meta2.core.query.PlannedQuery

object CoordinatorActor {
  def props(connector: ActorRef, coordinator: Coordinator): Props = Props(new CoordinatorActor(connector, coordinator))
}

class CoordinatorActor(connector: ActorRef, coordinator: Coordinator) extends Actor with ActorLogging {

  log.info("Lifting coordinator actor")

  var coordinatorsMap: Map[String, ActorRef] = Map()

  def receive = {

    case query: PlannedQuery => {
      log.info("Connector Actor received PlannedQuery")
      connector ! "QUERY!!!"
    }

    case _ =>
      println("coordinator actor receives event")
    //      sender ! "OK"
    //memberActorRef.tell(objetoConWorkflow, context.sender)
  }

}