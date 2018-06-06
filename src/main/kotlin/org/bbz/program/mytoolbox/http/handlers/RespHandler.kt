package org.bbz.program.mytoolbox.http.handlers


import org.bbz.program.mytoolbox.consts.ErrorCode
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import org.bbz.program.mytoolbox.consts.ErrorCodeException

/**
 * Created by sweet on 2017/12/25.
 * ---------------------------
 */
fun HttpServerResponse.endFail(msg: String?, errorCode: ErrorCode = ErrorCode.SYSTEM_ERROR) {
    this.setStatusCode(500).putHeader("content-type", "application/json; charset=utf-8")
            .end(json {
                obj(
                        "eid" to errorCode,
                        "error" to msg
                )
            }.encode())
}

fun HttpServerResponse.endFail(exception: ErrorCodeException) {
//    this.setStatusCode(500).putHeader("content-type", "application/json; charset=utf-8")
//            .end(json {
//                obj(
//                        "eid" to exception.errorCode,
//                        "msg" to exception.message
//                )
//            }.encode())
    endFail(exception.message,exception.errorCode)
}

fun HttpServerResponse.endFail(errorCode: ErrorCode) {
    this.setStatusCode(500).putHeader("content-type", "application/json; charset=utf-8")
            .end(json {
                obj(
                        "eid" to errorCode
                )
            }.encode())
}

fun HttpServerResponse.endFail(errorCode: ErrorCode, msg: String) {
    this.setStatusCode(500).putHeader("content-type", "application/json; charset=utf-8")
            .end(json {
                obj(
                        "eid" to errorCode,
                        "msg" to msg
                )
            }.encode())
}

fun HttpServerResponse.endSuccess(body: JsonObject) {
    this.putHeader("content-type", "application/json; charset=utf-8")
            .end(body.put("time", System.currentTimeMillis()).encode())
}

fun HttpServerResponse.endSuccess(body: String) {
    this.putHeader("content-type", "application/json; charset=utf-8")
            .end(JsonObject().put("msg",body).encode())
}