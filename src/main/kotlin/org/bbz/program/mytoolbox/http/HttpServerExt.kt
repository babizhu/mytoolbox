package org.bbz.program.mytoolbox.http



import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.StaticHandler
import org.bbz.program.mytoolbox.MainVerticle
import org.bbz.program.mytoolbox.consts.Consts
import org.bbz.program.mytoolbox.consts.ErrorCodeException
import org.bbz.program.mytoolbox.http.handlers.template.TemplateHandler
import org.bbz.program.mytoolbox.http.handlers.auth.CustomJwtImpl
import org.bbz.program.mytoolbox.http.handlers.endFail
import org.bbz.program.mytoolbox.http.handlers.user.LoginHandler
import org.bbz.program.mytoolbox.http.handlers.user.UserHandler

fun MainVerticle.createHttpServer(conf:JsonObject) {
    val logger = MainVerticle.logger
    val router = Router.router(vertx)
    initHandler(router)


    vertx.createHttpServer()
            .requestHandler(router::accept)
            .listen(conf.getInteger("port"), {
                if (it.succeeded()) {
                    logger.info("http server start at port  ${it.result().actualPort()}")
                } else {
//                        logger.error("http server error :: " + it.cause().message)
                    it.cause().printStackTrace()
                }
            })
}

private fun MainVerticle.initHandler(mainRouter: Router) {
    mainRouter.route().handler(BodyHandler.create())

    mainRouter.mountSubRouter("/", LoginHandler(jwtAuthProvider, dbClient).addRouter(Router.router(vertx)))
    mainRouter.route("${Consts.API_PREFIX}*").handler(CustomJwtImpl(jwtAuthProvider))
    mainRouter.route().failureHandler(errorHandler)
    dispatcherHandler(mainRouter)
    adapterReactHandler(mainRouter)//这个只能放在倒数第二的位置

    mainRouter.route().handler(StaticHandler.create())//静态文件处理，必须放在最后

}

/**
 * 异常处理函数
 */
val errorHandler = Handler<RoutingContext> {
    val failure = it.failure()
    failure.printStackTrace()
    println("uri ${it.request().uri()}")

    if (failure is ErrorCodeException) {
        it.response().endFail(failure)
    } else {
        it.response().endFail("ERROR ${failure.message}")
    }
}

private fun MainVerticle.dispatcherHandler(mainRouter: Router) {
    mainRouter.mountSubRouter(Consts.API_PREFIX + "user", UserHandler(dbClient).addRouter(Router.router(vertx)))
    mainRouter.mountSubRouter(Consts.API_PREFIX + "template", TemplateHandler(dbClient).addRouter(Router.router(vertx)))
}


/**
 * 适配react客户端的路由模式，访问任何页面都重定向到index.html中
 * @param mainRouter mainRouter
 */
private fun adapterReactHandler(mainRouter: Router) {
    mainRouter.route("/*").handler { ctx ->
        if (!ctx.request().uri().contains(".")) {
            ctx.reroute("/index.html")
        } else {
            ctx.next()
        }
    }
}