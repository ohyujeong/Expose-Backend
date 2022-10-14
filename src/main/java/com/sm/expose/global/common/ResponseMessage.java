package com.sm.expose.global.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data // getter, toString 등 편리성을 위한 기능 제공 + non-final에는 setter도 제공
@Builder
@AllArgsConstructor
public class ResponseMessage {
    private int status;
    private String message;
    private Object data;

    //data 없이 response 보낼 때: 그냥 ResponseMessage 객체 생성해서 보냄
    public ResponseMessage(final int status, final String msg){
        this.status = status;
        this.message = msg;
        this.data = null;
    }

    //data, status, msg 다 보낼 때: ResponseMessage.withMsg(params)
    public static ResponseMessage withData(final int status, final String message, final Object data) {
        return ResponseMessage.builder()
                .data(data)
                .message(message)
                .status(status)
                .build();
    }
}
