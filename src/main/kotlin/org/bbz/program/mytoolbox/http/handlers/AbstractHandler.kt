package org.bbz.program.mytoolbox.http.handlers

import org.bbz.program.mytoolbox.consts.ErrorCode
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.bbz.program.mytoolbox.consts.ErrorCodeException
import org.bbz.program.mytoolbox.http.handlers.endFail

/**
 *
 */
abstract class  AbstractHandler {
    /**
     * 检测客户端输入参数是否正确，不多也不少
     *
     * @param keys 需要的key
     * @param arguments 客户上传的json
     */
    protected fun checkArgumentsStrict(arguments: JsonObject, vararg keys: String) {
        println()
        if (arguments.size() != keys.size) {
            throw ErrorCodeException(ErrorCode.PARAMETER_ERROR)
        }
        checkArguments(arguments, *keys)
    }

    /**
     * 检测客户端输入参数是否正确，要求keys内的项目不能少，但是其余的输入不做硬性要求
     *
     * @param keys 需要的key
     * @param arguments 客户上传的json
     */
    protected fun checkArguments(arguments: JsonObject, vararg keys: String) {

        for (key in keys) {
            if (!arguments.containsKey(key)) {
                throw ErrorCodeException(ErrorCode.PARAMETER_ERROR, key + " is null")
            }
        }
    }
    protected fun endFail(ctx: RoutingContext, errorCode: ErrorCode){
        ctx.response().endFail(errorCode)
    }
}