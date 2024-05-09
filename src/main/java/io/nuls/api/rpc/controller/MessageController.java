/*
 * MIT License
 * Copyright (c) 2017-2019 nuls.io
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.nuls.api.rpc.controller;

import io.nuls.api.manager.MessageManager;
import io.nuls.api.model.entity.Message;
import io.nuls.api.model.rpc.RpcResult;
import io.nuls.core.core.annotation.Controller;
import io.nuls.core.core.annotation.RpcMethod;
import io.nuls.core.model.StringUtils;
import io.nuls.core.rpc.util.NulsDateUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Message forwarding tool（Temporary implementation）
 *
 * @author Niels
 */
@Controller
public class MessageController {
    /**
     * Submit a message for other applications to query
     *
     * @param paramsMap
     * @return
     */
    @RpcMethod("commitData")
    public RpcResult commit(Map<String, Object> paramsMap) {
        String key = (String) paramsMap.get("key");
        Object value = paramsMap.get("value");
        if (StringUtils.isBlank(key) || null == value) {
            return RpcResult.paramError("Params is inValid");
        }
        Message message = new Message();
        message.setKey(key);
        message.setValue(value);
        message.setTime(NulsDateUtils.getCurrentTimeSeconds());
        MessageManager.putMessage(key, message);
        return RpcResult.success(key);
    }

    /**
     * Query a transit message, if not found, return failure
     *
     * @param paramsMap
     * @return
     */
    @RpcMethod("getData")
    public RpcResult query(Map<String, Object> paramsMap) {
        String key = (String) paramsMap.get("key");
        if (StringUtils.isBlank(key)) {
            return RpcResult.dataNotFound();
        }
        Message message = MessageManager.getMessage(key);
        if (null == message) {
            return RpcResult.dataNotFound();
        }
        return RpcResult.success(message.getValue());
    }

    /**
     * Submit a message for other applications to query
     *
     * @param params
     * @return
     */
    @RpcMethod("commitMsg")
    public RpcResult commitMsg(List<Object> params) {
        if (params.size() < 2) {
            return RpcResult.paramError("Params is inValid");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("key", params.get(0));
        map.put("value", params.get(1));
        return this.commit(map);
    }

    /**
     * Query a transit message, if not found, return failure
     *
     * @param params
     * @return
     */
    @RpcMethod("getMsg")
    public RpcResult queryMsg(List<Object> params) {
        if (params.size() < 1) {
            return RpcResult.paramError("Params is inValid");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("key", params.get(0));
        return this.query(map);
    }
}
