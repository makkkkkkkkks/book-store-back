package com.heroke.bookstore.config;


import com.heroke.bookstore.security.CustomUserDetailsService;
import com.heroke.bookstore.security.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomChannelInterceptor implements ChannelInterceptor {
    private final Logger logger = LoggerFactory.getLogger(WebSocketConfiguration.class);
    private final TokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    public CustomChannelInterceptor(TokenProvider tokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.tokenProvider = tokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        try {
            StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
            logger.info("***** STOMP COMMAND ***** -> " + accessor.getCommand());
            logger.info("*****Accessor Authorization ***** -> " + accessor.getFirstNativeHeader("Authorization"));
            logger.info("************ Accessor ***** -> " + accessor);
            logger.info("***** STOMP access destination ***** -> " + accessor.getDestination());
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                if (accessor.getNativeHeader("Authorization") != null) {
                    String authToken = accessor.getFirstNativeHeader("Authorization");
                    logger.info("***** Get token from websocket ***** -> " + authToken);
                    String clearToken = getJwtFromRequest(authToken);
                    logger.info("Get clear token () -> " + clearToken);
                    //  if (StringUtils.hasText(authToken) || authToken.startsWith("Bearer ")) {//
                    Long userId = tokenProvider.getUserIdFromToken(clearToken);
                    logger.info("***** parse user id from token ***** -> " + userId);
                    UserDetails userDetails = customUserDetailsService.loadUserById(userId);
                    logger.info("******* get user details ******* ->" + userDetails);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    logger.info("***** Authentication info ***** -> " + authentication);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        accessor.setUser(authentication);
                    }
                }
            } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                try {
                    logger.info("Disconnected Sess : " + accessor.getSessionId());
                } catch (Exception e) {
                    //TODO: handle exception
                    logger.error(e.getMessage());
                }
            }
        } catch (Exception e) {
            //TODO: handle exception
            logger.error(e.getMessage());
        }
        return message;
    }

    private String getJwtFromRequest(String bearerToken) {
        if (bearerToken.startsWith("Bearer ")) return bearerToken.substring(7);
        else return "";
    }
}
