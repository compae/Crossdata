/*
 * Copyright (C) 2015 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stratio.crossdata.security

import org.apache.log4j.Logger

trait CrossdataSecurityManager {

  def start() : Unit
  def stop(): Unit

  def authorize(userId: String, resource: Resource, action: Action): Boolean

  def audit(auditEvent: AuditEvent): Unit

}

class DummyCrossdataSecurityManager extends CrossdataSecurityManager{

  private val logger = Logger.getLogger(classOf[DummyCrossdataSecurityManager])

  def start() : Unit = {
    logger.info("CrossdataSecurityManager started")
  }

  def stop(): Unit = ()

  def authorize(userId: String, resource: Resource, action: Action) = {
    logger.info(s"user=$userId requests authorization for the action:$action on the resource:$resource")
    true
  }

  def audit(auditEvent: AuditEvent): Unit = ()
}
