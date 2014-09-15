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

package com.stratio.meta2.core.validator;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.stratio.meta.common.exceptions.IgnoreQueryException;
import com.stratio.meta.common.exceptions.ValidationException;
import com.stratio.meta.common.exceptions.validation.ExistNameException;
import com.stratio.meta.common.exceptions.validation.NotExistNameException;
import com.stratio.meta2.common.api.Manifest;
import com.stratio.meta2.common.data.*;
import com.stratio.meta2.common.metadata.ColumnMetadata;
import com.stratio.meta2.common.metadata.IndexMetadata;
import com.stratio.meta2.common.metadata.TableMetadata;
import com.stratio.meta2.core.metadata.MetadataManager;
import com.stratio.meta2.core.query.MetaDataParsedQuery;
import com.stratio.meta2.core.query.ParsedQuery;
import com.stratio.meta2.core.query.ValidatedQuery;
import com.stratio.meta2.core.statements.*;
import org.apache.log4j.Logger;


public class Validator {
  /**
   * Class logger.
   */
  private static final Logger LOG = Logger.getLogger(Validator.class);

  public ValidatedQuery validate(ParsedQuery parsedQuery)
      throws ValidationException, IgnoreQueryException {
      ValidatedQuery validatedQuery=null;
      MetaDataParsedQuery metaDataParsedQuery=null;

      for (Validation val:parsedQuery.getStatement().getValidationRequirements().getValidations()) {
          switch (val) {
              case MUST_NOT_EXIST_CATALOG:
                  validateNotExistCatalog(
                      new CatalogName(parsedQuery.getStatement().getEffectiveCatalog()), true);
                  break;
              case MUST_EXIST_CATALOG:
                  validateExist(new CatalogName(parsedQuery.getStatement().getEffectiveCatalog()),true);
                  break;
              case MUST_EXIST_TABLE:
                   validateExist(parsedQuery.getStatement().getFromTables(),true);
                  break;
              case MUST_NOT_EXIST_TABLE:
                  validateNotExist(parsedQuery.getStatement().getFromTables(),true);
                  break;
              case MUST_NOT_EXIST_CLUSTER:
                  metaDataParsedQuery=(MetaDataParsedQuery)parsedQuery;
                  validateNotExist(
                      metaDataParsedQuery.getStatement().getClusterMetadata().getName(), true);
                  break;
              case MUST_EXIST_CLUSTER:
                  metaDataParsedQuery=(MetaDataParsedQuery)parsedQuery;
                  validateExist(metaDataParsedQuery.getStatement().getClusterMetadata().getName(),
                      true);
                  break;
              case MUST_EXIST_CONNECTOR:
                  metaDataParsedQuery=(MetaDataParsedQuery)parsedQuery;
                  validateExist(metaDataParsedQuery.getStatement().getConnectorMetadata().getName(),true);
                  break;
              case MUST_NOT_EXIST_CONNECTOR:
                  metaDataParsedQuery=(MetaDataParsedQuery)parsedQuery;
                  validateNotExist(metaDataParsedQuery.getStatement().getConnectorMetadata().getName(),true);
                  break;
              case MUST_EXIST_DATASTORE:
                  metaDataParsedQuery=(MetaDataParsedQuery)parsedQuery;
                  validateExist(metaDataParsedQuery.getStatement().getDataStoreMetadata().getName(),true);
                  break;
              case MUST_NOT_EXIST_DATASTORE:
                  metaDataParsedQuery=(MetaDataParsedQuery)parsedQuery;
                  validateNotExist(
                      metaDataParsedQuery.getStatement().getDataStoreMetadata().getName(), true);
                  break;
              case VALID_DATASTORE_MANIFEST:
                  //TODO Manifest is an API call with a previous validation
                  Manifest datastoreManifest=null;
                  validateManifest(datastoreManifest);
                  break;
              case VALID_CONNECTOR_MANIFEST:
                  //TODO Manifest is an API call with a previous validation
                  Manifest connectorManifest=null;
                  validateManifest(connectorManifest);
                  break;
              case VALID_CLUSTER_OPTIONS:
                  validateOptions(parsedQuery.getStatement());
                  break;
              case VALID_CONNECTOR_OPTIONS:
                  validateOptions(parsedQuery.getStatement());
                  break;
              case MUST_EXIST_ATTACH_CONNECTOR_CLUSTER:
                  break;
              case MUST_EXIST_PROPERTIES:
                  if (parsedQuery.getStatement() instanceof AlterCatalogStatement) {
                      AlterCatalogStatement stmt=(AlterCatalogStatement)parsedQuery.getStatement();
                      validateExistsAlterCatalogProperties(stmt);
                  }
                  break;
              case MUST_NOT_EXIST_INDEX:
                  metaDataParsedQuery=(MetaDataParsedQuery)parsedQuery;
                  Map<IndexName, IndexMetadata> indexes=metaDataParsedQuery.getStatement().getTableMetadata().getIndexes();
                  validateNotIndexExist(indexes, true);
                  break;
              case MUST_EXIST_INDEX:
                  metaDataParsedQuery=(MetaDataParsedQuery)parsedQuery;
                  Map<IndexName, IndexMetadata> indexExists=metaDataParsedQuery.getStatement().getTableMetadata().getIndexes();
                  validateIndexExist(indexExists, true);
                  break;
              case MUST_EXIST_COLUMN:
                  metaDataParsedQuery=(MetaDataParsedQuery)parsedQuery;
                  TableMetadata tableMetadata=metaDataParsedQuery.getStatement().getTableMetadata();
                  validateExistColumn(tableMetadata);
                  break;
              case MUST_NOT_EXIST_COLUMN:
                  metaDataParsedQuery=(MetaDataParsedQuery)parsedQuery;
                  TableMetadata tableMetadataNotExist=metaDataParsedQuery.getStatement().getTableMetadata();
                  validateNotExistColumn(tableMetadataNotExist);
                  break;
              default:
                  break;

          }
      }
      //validatedQuery=(ValidatedQuery)parsedQuery;
      return validatedQuery;
  }


    private void validateExistColumn(TableMetadata tableMetadata) throws NotExistNameException {
        for (ColumnName columnName:tableMetadata.getColumns().keySet()){
            if(!MetadataManager.MANAGER.exists(columnName)){
                throw new NotExistNameException(columnName);
            }
        }
    }

    private void validateNotExistColumn(TableMetadata tableMetadata)
        throws ExistNameException {
        for (ColumnName columnName:tableMetadata.getColumns().keySet()){
            if(MetadataManager.MANAGER.exists(columnName)){
                throw new ExistNameException(columnName);
            }
        }
    }

    private void validateExistsAlterCatalogProperties(AlterCatalogStatement stmt) throws ValidationException {
        if (stmt.getOptions().isEmpty()){
            throw new ValidationException("AlterCatalog options can't be empty");
        }

    }



    private void validateExist(List<? extends Name> names, boolean hasIfExists)
      throws IgnoreQueryException, NotExistNameException {
    for(Name name:names){
      this.validateExist(name, hasIfExists);
    }
  }



  private void validateExist(Name name, boolean hasIfExists)
      throws NotExistNameException, IgnoreQueryException {
    if(!MetadataManager.MANAGER.exists(name)){
      if(hasIfExists) {
        throw new IgnoreQueryException("["+ name + "] doesn't exist");
      }else{
        throw new NotExistNameException(name);
      }
    }
  }

  private void validateNotExist(Name name, boolean hasIfExists)
      throws ExistNameException, IgnoreQueryException {
    if(MetadataManager.MANAGER.exists(name)){
      if(hasIfExists) {
         throw new IgnoreQueryException("["+ name + "] doesn't exist");
      }else{
         throw new ExistNameException(name);
      }
    }
  }

  private void validateNotExist(List<? extends Name> names, boolean hasIfNotExist)
      throws ExistNameException, IgnoreQueryException {
    for(Name name:names){
      this.validateNotExistCatalog(name,hasIfNotExist);
    }
  }


  private void validateNotExistCatalog(Name name, boolean onlyIfNotExist)
      throws ExistNameException, IgnoreQueryException {
    if(MetadataManager.MANAGER.exists(name)){
      if(onlyIfNotExist) {
        throw new IgnoreQueryException("["+ name + "] exists");
      }else{
        throw new ExistNameException(name);
      }
    }
  }

  private void validateManifest(Manifest manifest){

  }

  private void validateOptions(MetaStatement stmt) throws ValidationException {

      if (stmt instanceof AttachClusterStatement) {
          AttachClusterStatement myStmt = (AttachClusterStatement) stmt;
          if (myStmt.getOptions().isEmpty()){
              throw new ValidationException("AttachClusteStatemet options can't be empty");
          }
      }else {
          if (stmt instanceof AttachConnectorStatement) {
              AttachConnectorStatement myStmt = (AttachConnectorStatement) stmt;
              if (myStmt.getOptions().isEmpty()){
                  throw new ValidationException("AttachConnectorStatemet options can't be empty");
              }
          }
      }



  }

  private void validateIndexExist(Map<IndexName, IndexMetadata> indexes, boolean hasIfExists)
      throws IgnoreQueryException, ExistNameException {
      for (IndexName indexName:indexes.keySet()) {
          if (MetadataManager.MANAGER.exists(indexName)) {
              if (hasIfExists) {
                  throw new IgnoreQueryException("[" + indexName + "] exists");
              } else {
                  throw new ExistNameException(indexName);
              }
          }
      }

  }

  private void validateNotIndexExist(Map<IndexName, IndexMetadata> indexes, boolean hasIfExists)
      throws IgnoreQueryException, NotExistNameException {
      for (IndexName indexName:indexes.keySet()) {
          if (!MetadataManager.MANAGER.exists(indexName)) {
              if (hasIfExists) {
                  throw new IgnoreQueryException("[" + indexName + "]  not exists");
              } else {
                  throw new NotExistNameException(indexName);
              }
          }
          for(ColumnMetadata columnMetadata:indexes.get(indexName).getColumns()) {
              if (!MetadataManager.MANAGER.exists(columnMetadata.getName())) {
                  if (hasIfExists) {
                      throw new IgnoreQueryException("[" + columnMetadata.getName() + "]  not exists to create the index [" + indexName +"]");
                  } else {
                      throw new NotExistNameException(indexName);
                  }
              }
          }



      }

  }


}