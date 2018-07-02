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


//fun HttpServerResponse.endFail(msg: String?, errorCode: ErrorCode = ErrorCode.SYSTEM_ERROR) {
//    endJson(json {
//        obj(
//                "eid" to errorCode,
//                "error" to msg
//        )}, 500)
//}

fun HttpServerResponse.endFail(exception: ErrorCodeException) {
    endFail(exception.errorCode, exception.message)
}

fun HttpServerResponse.endFail(errorCode: ErrorCode) {
//    this.setStatusCode(500).putHeader("content-type", "application/json; charset=utf-8")
//            .end(json {
//                obj(
//                        "eid" to errorCode
//                )
//            }.encode())
    endJson(json {
        obj(
                "eid" to errorCode
        )
    }, 500)

}

fun HttpServerResponse.endFail(errorCode: ErrorCode = ErrorCode.SYSTEM_ERROR, msg: String?) {
//    this.setStatusCode(500).putHeader("content-type", "application/json; charset=utf-8")
//            .end(json {
//                obj(
//                        "eid" to errorCode,
//                        "msg" to msg
//                )
//            }.encode())
    endJson(json {
        obj(
                "eid" to errorCode,
                "msg" to msg
        )
    }, 500)

}

fun HttpServerResponse.endSuccess(body: JsonObject) {
    endJson(body.put("time", System.currentTimeMillis()))

}

fun HttpServerResponse.endSuccess(body: String) {
    endJson(JsonObject().put("msg", body))
}

fun HttpServerResponse.endJson(body: JsonObject, statusCode: Int = 200) {
    this.setStatusCode(statusCode).putHeader("content-type", "application/json; charset=utf-8")
            .end(body.encode())
}