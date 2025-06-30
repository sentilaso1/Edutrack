package com.example.edutrack;

import com.example.edutrack.auth.controller.RoleSelectionController;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoleSelectionControllerTest_GetUserFromSession {
    @Mock
    HttpSession session;

    RoleSelectionController controller = new RoleSelectionController();

    @Test
    void returnsNull_whenUserInfoIsNull() {
        when(session.getAttribute("googleUserInfo")).thenReturn(null);
        assertNull(controller.getUserInfoFromSession(session));
    }

    @Test
    void returnsNull_whenEmailKeyMissing() {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", "Alice");
        when(session.getAttribute("googleUserInfo")).thenReturn(userInfo);
        assertNull(controller.getUserInfoFromSession(session));
    }

    @Test
    void returnsNull_whenNameKeyMissing() {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", "alice@email.com");
        when(session.getAttribute("googleUserInfo")).thenReturn(userInfo);
        assertNull(controller.getUserInfoFromSession(session));
    }

    @Test
    void returnsNull_whenEmailIsNull() {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", null);
        userInfo.put("name", "Alice");
        when(session.getAttribute("googleUserInfo")).thenReturn(userInfo);
        assertNull(controller.getUserInfoFromSession(session));
    }

    @Test
    void returnsNull_whenNameIsNull() {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", "alice@email.com");
        userInfo.put("name", null);
        when(session.getAttribute("googleUserInfo")).thenReturn(userInfo);
        assertNull(controller.getUserInfoFromSession(session));
    }

    @Test
    void returnsUserInfo_whenAllPresentAndNonNull() {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", "alice@email.com");
        userInfo.put("name", "Alice");
        when(session.getAttribute("googleUserInfo")).thenReturn(userInfo);

        Map<String, Object> result = controller.getUserInfoFromSession(session);
        assertNotNull(result);
        assertEquals("alice@email.com", result.get("email"));
        assertEquals("Alice", result.get("name"));
    }
}
