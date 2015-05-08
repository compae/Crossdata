/*
 * Licensed to STRATIO (C) under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  The STRATIO (C) licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.stratio.crossdata.core.statements;

import com.stratio.crossdata.common.data.CatalogName;
import com.stratio.crossdata.common.data.TableName;
import com.stratio.crossdata.core.validator.requirements.ValidationRequirements;

/**
 * Abstract metadata table statement class to create and alter table statements.
 */
public abstract class AbstractMetadataTableStatement extends MetadataStatement implements ITableStatement{
    
    private static class InnerTableStatement extends TableStatement {   }

    protected TableStatement tableStatement=new InnerTableStatement();


    protected AbstractMetadataTableStatement() {
    }


    @Override public ValidationRequirements getValidationRequirements() {
        return null;
    }

    @Override public TableName getTableName() {
        return tableStatement.getTableName();
    }

    @Override public void setTableName(TableName tableName) {
        tableStatement.setTableName(tableName);

    }
    
    @Override
    public CatalogName getEffectiveCatalog() { return tableStatement.getEffectiveCatalog(); }

}
