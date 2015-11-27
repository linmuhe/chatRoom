package com.toudu98.www.websocket.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.toudu98.www.websocket.inter.DecodeReader;
import com.toudu98.www.websocket.inter.Message;
import com.toudu98.www.websocket.msg.TextDecodeReader;
import com.toudu98.www.websocket.msg.TextMessage;
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface LaWebSocket {
 String value() default "/";
 String url() default "/";
 Class<? extends DecodeReader<Message>>  decoder() default TextDecodeReader.class;
}
