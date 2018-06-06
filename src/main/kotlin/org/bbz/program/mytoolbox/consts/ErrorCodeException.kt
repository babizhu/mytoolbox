package org.bbz.program.mytoolbox.consts


class ErrorCodeException(val errorCode: ErrorCode, message: String) : RuntimeException(message) {

    constructor(errorCode: ErrorCode) : this(errorCode, "")


}

fun main(args: Array<String>) {
    val errorCodeException = ErrorCodeException(ErrorCode.DB_ERROR, "db error")
    println(errorCodeException.message + ":" + errorCodeException.errorCode)

    val errorCodeException1 = ErrorCodeException(ErrorCode.DB_ERROR)
    println(errorCodeException1.message + ":" + errorCodeException1.errorCode)

}