/*
 * Stratio Meta
 *
 * Copyright (c) 2014, Stratio, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package com.stratio.meta.driver;

import com.stratio.meta.common.result.IResultHandler;
import com.stratio.meta.common.result.QueryStatus;
import com.stratio.meta.common.result.Result;

import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Asynchronous interface tests.
 */
public class AsyncDriverTest extends DriverParentTest {

  @Test
  public void basicSelect(){
    String query = "select * from system.schema_columns;";
    ResultHandlerWrapper rhw = new ResultHandlerWrapper();
    driver.asyncExecuteQuery("user", "", query, rhw);
    waitForAnswer();
    assertTrue(rhw.isAckReceived(), "Ack has not been received.");
    assertFalse(rhw.isErrorReceived(), "No error expected.");
    assertTrue(rhw.isResultReceived(), "Result has not been received.");
  }

  public void waitForAnswer(){
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
