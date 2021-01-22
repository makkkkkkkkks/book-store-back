package com.heroke.bookstore.config;

import com.heroke.bookstore.security.CustomUserDetailsService;
import com.heroke.bookstore.security.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

/**
 * Класс для настройки параметров WebSocket
 */

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final Logger logger = LoggerFactory.getLogger(WebSocketConfiguration.class);
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    /**
     * Настраивает брокер сообщений
     *
     * @param config - настройки брокера сообщений
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/user"); //"/chat",
        //Установка префикса для URL
        config.setApplicationDestinationPrefixes("/app");
        //Установка префикса для отправки сообщений определенным юзерам
        config.setUserDestinationPrefix("/user");
    }

    /**
     * Устанавливаем перехватчик сообщений, чтобы аутентифицировать
     * пользователя при подключение через STOMP заголовок, согласно:
     * https://github.com/spring-projects/spring-framework/blob/master/src/docs/asciidoc/web/websocket.adoc#token-authentication
     *
     * @param registration - конфигурации канала, включающая в частности перехватчики сообщений
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new CustomChannelInterceptor(tokenProvider, customUserDetailsService));
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(new ObjectMapper());
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);
        return false;
    }
}