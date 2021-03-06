package org.bbz.program.mytoolbox.http.handlers.template

import io.vertx.ext.sql.SQLClient
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.JsonArray
import io.vertx.kotlin.core.json.array
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import org.bbz.program.mytoolbox.consts.JsonConsts
import org.bbz.program.mytoolbox.coroutineHandler
import org.bbz.program.mytoolbox.db.TemplateDataProvider
import org.bbz.program.mytoolbox.http.handlers.AbstractHandler
import org.bbz.program.mytoolbox.http.handlers.auth.CustomJwtImpl
import org.bbz.program.mytoolbox.http.handlers.auth.anno.RequirePermissions
import org.bbz.program.mytoolbox.http.handlers.endSuccess
import org.bbz.program.mytoolbox.util.CustomHashStrategy

class TemplateHandler(dbClient: SQLClient) : AbstractHandler() {
    private val dataProvider = TemplateDataProvider(dbClient)
    fun addRouter(mainRouter: Router): Router? {

//        mainRouter.route("/save").coroutineHandler { save(it) }
        mainRouter.route("/create").coroutineHandler { create(it) }
        mainRouter.route("/update").coroutineHandler { update(it) }
        mainRouter.route("/del").coroutineHandler { del(it) }
        mainRouter.route("/query").coroutineHandler { query(it) }
        mainRouter.route("/permisstionsQuery").coroutineHandler { permisstionsQuery(it) }
        return mainRouter
    }

//    @RequirePermissions("sys:user:create")
//    private suspend fun save(ctx: RoutingContext) {
//        val userJson = ctx.bodyAsJson
//        checkArguments(userJson, "username", "password")
//        val postId = userJson.getString(JsonConsts.DB_ID)
//        val isCreate = (postId == null)
//        val result = if (isCreate) {
////            create( userJson)
//        } else {
//            update(userJson)
//        }
//        ctx.response().endSuccess(result.toJson())
//    }

    @RequirePermissions("sys:user:del")
    private suspend fun del(ctx: RoutingContext) {
        val bodyAsJson = ctx.bodyAsJson

        val updateResult = dataProvider.create(json { array(bodyAsJson.getValue("name")) })
        ctx.response().endSuccess(updateResult.keys.encode())
    }

    @RequirePermissions("sys:user:create")
    private suspend fun create(ctx: RoutingContext) {
        val userJson = ctx.bodyAsJson
        checkArguments(userJson, "username", "password")
        val salt = CustomHashStrategy.generateSalt()

        val cryptPassword = CustomHashStrategy.INSTANCE
                .cryptPassword(userJson.getString(JsonConsts.USER_PASSWORD), salt)
        json { array(userJson.getValue("username"), salt, cryptPassword) }

        val result = dataProvider.create(json {
            array(
                    userJson.getValue("username"),
                    salt,
                    cryptPassword
            )
        })
        ctx.response().endSuccess(result.toJson())
    }

    @RequirePermissions("sys:user:update")
    private suspend fun update(ctx: RoutingContext) {
        val userJson = ctx.bodyAsJson
        checkArguments(userJson, "username", "password")
        val params = JsonArray()
        params.add(userJson.getValue("name"))
        val updateResult = dataProvider.create(params)
        ctx.response().endSuccess(updateResult.keys.encode())
    }

    @RequirePermissions("sys:template:query")
    private suspend fun query(ctx: RoutingContext) {
//        val condition = ctx.bodyAsJson
//        val result = dataProvider.query(condition)
//
//        ctx.response().endSuccess(result.toJson())
        ctx.response().endSuccess(json {
            obj("host" to "127.0.0.1",
                    "port" to 3306,
                    "username" to "root",
                    "password" to "root",
                    "database" to "uaes_oa")
        })
    }

    @RequirePermissions("sys:permissions:query")
    private suspend fun permisstionsQuery(ctx: RoutingContext) {

        ctx.response().endSuccess(CustomJwtImpl.URI_PERMISSIONS_MAP.values.flatMap { it }.toSet().joinToString(","))
    }


}