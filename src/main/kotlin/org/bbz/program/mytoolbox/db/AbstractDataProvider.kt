package org.bbz.program.mytoolbox.db

import io.vertx.ext.sql.SQLClient

abstract class AbstractDataProvider(dbClient: SQLClient)   {
    val dbClient: SQLClient = dbClient
}