package org.bbz.program.mytoolbox

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpClientOptions
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.asyncsql.MySQLClient
import io.vertx.ext.auth.KeyStoreOptions
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.auth.jwt.JWTAuthOptions
import io.vertx.ext.sql.SQLClient
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.bbz.program.mytoolbox.http.createHttpServer
import org.bbz.program.mytoolbox.http.handlers.auth.CustomJwtImpl
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalTime
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.suspendCoroutine


/**
 * MainVerticle
 * ---------------------------
 */
fun main(args: Array<String>) {
//    System.setProperty("vertx.logger-delegate-factory-class-name",
//            "io.vertx.core.logging.Log4j2LogDelegateFactory")
    val vertxOptions = VertxOptions()
    vertxOptions.blockedThreadCheckInterval = 1000000
    val vertx = Vertx.vertx(vertxOptions)


    val content = String(Files.readAllBytes(Paths.get("resources/application-conf.json")))
    val config = JsonObject(content)
    val options = DeploymentOptions()
    options.instances = 1
    options.config = config


    vertx.deployVerticle(MainVerticle(), options)
}

@Suppress("unused")
class MainVerticle : CoroutineVerticle() {
    companion object {
        val logger = LoggerFactory.getLogger(this::class.java)!!
    }

    lateinit var jwtAuthProvider: JWTAuth
    lateinit var dbClient: SQLClient
    private lateinit var httpClient: HttpClient

    override suspend fun start() {
//        dbClient = MySQLClient.createShared(vertx, json {
//            obj(
//                    "host" to "127.0.0.1",
//                    "port" to 3306,
//                    "username" to "root",
//                    "password" to "root",
//                    "database" to "uaes_oa"
//            )
//        })

        println(config.getJsonObject("db"))
        dbClient = MySQLClient.createShared(vertx, config.getJsonObject("db"))

        createJwtProvider()
        createHttpServer(config.getJsonObject("server"))
        CustomJwtImpl.initRole2PermissionsMap(dbClient)
        createHttpClient()

//        findTusi2()
//        findTusi()
    }

    private fun createHttpClient() {
        httpClient = vertx.createHttpClient(HttpClientOptions().setVerifyHost(false).setSsl(true).setTrustAll(true))
    }

    private fun createJwtProvider() {
        val jwtAuthOptions = JWTAuthOptions()
                .setKeyStore(KeyStoreOptions()
                        .setPath("./resources/keystore.jceks")
                        .setType("jceks")
                        .setPassword("secret"))
        jwtAuthProvider = JWTAuth.create(vertx, jwtAuthOptions)
    }

    private suspend fun findTusi2() {
        val rs = List(100) {
            async(vertx.dispatcher()) {
                println("${Thread.currentThread().name} ${LocalTime.now()} 开始处理https://www.smzdm.com/p${201 + it}/")
                val buffer = getNow(443, "www.smzdm.com", "/p${201 + it}/").toString(Charset.defaultCharset())
                if (buffer.indexOf("这款LEGO 乐高 60169 城市系列") != -1) {
                    return@async it
                } else {
                    return@async -1
                }
            }
        }
        rs.forEach {
            val result = it.await()
            if (result != -1) {
                println("包含吐司的页面地址为https://www.smzdm.com/p$result/")
            }
        }
    }

    private suspend fun findTusi1() {
        val list = ArrayList<Deferred<Int>>()
        for (i in 201..310) {
            val rs = async(vertx.dispatcher()) {
                println("${Thread.currentThread().name} ${LocalTime.now()} 开始处理https://www.smzdm.com/p$i/")
                val buffer = getNow(443, "www.smzdm.com", "/p$i/").toString(Charset.defaultCharset())
                if (buffer.indexOf("这款LEGO 乐高 60169 城市系列") != -1) {
                    return@async i
                } else {
                    return@async -1
                }
            }
            list.add(rs)
        }
        for (deferred in list) {
            val rs = deferred.await()
            if (rs != -1) {
                println("包含吐司的页面地址为https://www.smzdm.com/p$rs/")
            } else {
                println("没找到")
            }
        }
    }

    private suspend fun findTusi() {
        val list = ArrayList<Int>()
        for (i in 201..210) {
            println("${Thread.currentThread().name} ${LocalTime.now()} 开始处理https://www.smzdm.com/p$i/")


            val buffer = getNow(443, "www.smzdm.com", "/p$i/").toString(Charset.defaultCharset())

            if (buffer.indexOf("这款LEGO 乐高 60169 城市系列") != -1) {
                list.add(i)
            }
            println("${LocalTime.now()} https://www.smzdm.com/p$i/处理完毕")

        }
        list.map {
            println("包含吐司的页面地址为https://www.smzdm.com/p$it/")
        }
    }

    private suspend fun getNow1(port: Int, host: String, uri: String): Buffer {
        return suspendCoroutine { cont: Continuation<Buffer> ->
            httpClient.get(port, host, uri) { response ->
                // println(response.headers().map { println(it) })
                response.bodyHandler { buffer ->
                    //println(Thread.currentThread())
                    cont.resume(buffer)
                }
            }.exceptionHandler { throwable ->
                cont.resumeWithException(throwable)
            }.end()
        }
    }

    private suspend fun getNow(port: Int, host: String, uri: String): Buffer =
            suspendCoroutine { cont ->
                httpClient.get(port, host, uri) { response ->
                    // println(response.headers().map { println(it) })
                    response.bodyHandler { buffer ->
                        //println(Thread.currentThread())
                        cont.resume(buffer)
                    }
                }.exceptionHandler { throwable ->
                    cont.resumeWithException(throwable)
                }.end()
            }

}

fun Route.coroutineHandler(fn: suspend (RoutingContext) -> Unit) {
    handler { ctx ->
        launch(ctx.vertx().dispatcher()) {
            try {
                fn(ctx)
            } catch (e: Exception) {
                ctx.fail(e)
            }
        }
    }
}

