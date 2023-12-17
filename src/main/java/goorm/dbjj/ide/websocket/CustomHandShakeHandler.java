package goorm.dbjj.ide.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Slf4j
@Component
public class CustomHandShakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        log.trace("determineUser execute");
        String sessionId = (String) attributes.get("WebSocketUserSessionId");
        log.trace("sessionId = {}", sessionId);
        return new StompPrincipal(sessionId);
    }
}
