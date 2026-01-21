package com.easemob.app.api;

import com.easemob.app.model.request.LoginAppUser;
import com.easemob.app.model.response.UserLoginResponse;
import com.easemob.app.service.AppGroupService;
import com.easemob.app.service.AppUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MockAppServerTests {

    private MockMvc mockMvcUser;
    private MockMvc mockMvcGroup;

    @Mock
    private AppUserService appUserService;

    @Mock
    private AppGroupService appGroupService;

    @InjectMocks
    private AppUserController appUserController;

    @InjectMocks
    private AppGroupController appGroupController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvcUser = MockMvcBuilders.standaloneSetup(appUserController).build();
        mockMvcGroup = MockMvcBuilders.standaloneSetup(appGroupController).build();
    }

    @Test
    public void testLoginWithPhoneNumber() throws Exception {
        LoginAppUser loginUser = new LoginAppUser();
        loginUser.setPhoneNumber("13800138000");
        loginUser.setSmsCode("123456");

        UserLoginResponse mockResponse = new UserLoginResponse();
        mockResponse.setToken("mock-token");
        mockResponse.setPhoneNumber("13800138000");
        mockResponse.setUserName("mockUser");
        mockResponse.setAvatarUrl("http://mock.url/avatar.png");

        given(appUserService.loginWithPhoneNumber(any(), any(LoginAppUser.class))).willReturn(mockResponse);

        mockMvcUser.perform(post("/inside/app/user/login/V2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.token").value("mock-token"))
                .andExpect(jsonPath("$.chatUserName").value("mockUser"));
    }

    @Test
    public void testAvatarUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", "test-image".getBytes());
        String chatUsername = "testUser";
        String mockUrl = "http://mock.url/new-avatar.png";

        given(appUserService.uploadAvatar(any(), eq(chatUsername), any())).willReturn(mockUrl);

        mockMvcUser.perform(multipart("/inside/app/user/{chatUsername}/avatar/upload", chatUsername)
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.avatarUrl").value(mockUrl));
    }

    @Test
    public void testGetGroupAvatarUrl() throws Exception {
        String groupId = "12345";
        String mockUrl = "http://mock.url/group-avatar.png";

        given(appGroupService.getAvatarUrl(any(), eq(groupId))).willReturn(mockUrl);

        mockMvcGroup.perform(get("/inside/app/group/{groupId}/avatarurl", groupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.avatarUrl").value(mockUrl));
    }
}
