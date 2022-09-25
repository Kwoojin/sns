package com.fc.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fc.sns.controller.request.UserJoinRequest;
import com.fc.sns.controller.request.UserLoginRequest;
import com.fc.sns.exception.ErrorCode;
import com.fc.sns.exception.SnsApplicationException;
import com.fc.sns.model.User;
import com.fc.sns.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    public void 회원가입() throws Exception {
        //given
        String username = "name";
        String password = "password";

        // TODO : mocking
        given(userService.join(username, password)).willReturn(mock(User.class));

        //when & then
        mockMvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(username, password))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void 회원가입시_이미_회원가입된_userName으로_회원가입을_하는경우_에러반환() throws Exception {
        //given
        String username = "userName";
        String password = "password";
        given(userService.join(username, password)).willThrow(new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME));

        // TODO : mocking

        //when & then
        mockMvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        // TODO : add request body
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(username, password)))
                ).andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void 로그인() throws Exception {
        //given
        String username = "userName";
        String password = "password";

        // TODO : mocking
        given(userService.login(username, password)).willReturn("test_token");

        //when & then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        // TODO : add request body
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(username, password)))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void 로그인시_회원가입이_안된_userName을_입력할경우_에러반환() throws Exception {
        //given
        String username = "userName";
        String password = "password";

        // TODO : mocking
        given(userService.login(username, password)).willThrow(new SnsApplicationException(ErrorCode.USER_NOT_FOUND));

        //when & then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(username, password)))
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void 로그인시_틀린_password를_입력할경우_에러반환() throws Exception {
        //given
        String username = "userName";
        String password = "password";

        // TODO : mocking
        given(userService.login(username, password)).willThrow(new SnsApplicationException(ErrorCode.INVALID_PASSWORD));

        //when & then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(username, password)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 알람기능() throws Exception {
        given(userService.alarmList(any(), any())).willReturn(Page.empty());

        mockMvc.perform(get("/api/v1/uesrs/alarm")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void 알람리스트요청시_로그인하지_않은경우() throws Exception {
        given(userService.alarmList(any(), any())).willReturn(Page.empty());
        mockMvc.perform(get("/api/v1/uesrs/alarm")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
