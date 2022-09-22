package com.fc.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fc.sns.controller.request.PostCreateRequest;
import com.fc.sns.controller.request.PostModifyRequest;
import com.fc.sns.controller.request.UserJoinRequest;
import com.fc.sns.exception.ErrorCode;
import com.fc.sns.exception.SnsApplicationException;
import com.fc.sns.fixture.PostEntityFixture;
import com.fc.sns.model.Post;
import com.fc.sns.model.User;
import com.fc.sns.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostService postService;

    @Test
    @WithMockUser
    void 포스트작성() throws Exception {
        //given
        String title = "title";
        String body = "body";

        //when & then
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body))))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @WithAnonymousUser
    void 포스트작성시_로그인하지않은경우() throws Exception {
        //given
        String title = "title";
        String body = "body";

        //when & then
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body))))
                .andDo(print())
                .andExpect(status().isUnauthorized());

    }

    @Test
    @WithMockUser
    void 포스트수정() throws Exception {
        //given
        String title = "title";
        String body = "body";

        given(postService.modify(eq(title), eq(body), any(), any())).
                willReturn(Post.fromEntity(PostEntityFixture.get("userName", 1L, 1L)));

        //when & then
        mockMvc.perform(patch("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body))))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @WithAnonymousUser
    void 포스트수정시_본인이_작성한_글이_아니면_에러발생() throws Exception {
        //given
        String title = "title";
        String body = "body";

        // mocking
        // TODO :
        willThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).given(postService).modify(eq(title), eq(body), any(), eq(1L));

        //when & then
        mockMvc.perform(patch("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body))))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void 포스트수정시_수정하려는_글이_없는경우_에러발생() throws Exception {
        //given
        String title = "title";
        String body = "body";

        willThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).given(postService).modify(eq(title), eq(body), any(), eq(1L));

        //when & then
        mockMvc.perform(patch("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body))))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void 포스트삭제() throws Exception {
        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

//        .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }


    @Test
    @WithAnonymousUser
    void 포스트삭제시_로그인한상태가_아니라면_에러발생() throws Exception {
        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 포스트삭제시_본인이_작성한_글이_아니라면_에러발생() throws Exception {
        willThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).given(postService).delete(any(), eq(1L));
        mockMvc.perform(delete("/api/v1/posts/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));
    }

    @Test
    @WithMockUser
    void 포스트삭제시_수정하려는글이_없다면_에러발생() throws Exception {
        willThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).given(postService).delete(any(), eq(1L));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()));
    }


    @Test
    @WithMockUser
    void 피드목록() throws Exception {
        // TODO : mocking
        mockMvc.perform(get("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }


    @Test
    @WithAnonymousUser
    void 피드목록요청시_로그인하지_않은경우_에러발생() throws Exception {
        // TODO : mocking
        mockMvc.perform(get("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 내피드목록() throws Exception {
        // TODO : mocking
        mockMvc.perform(get("/api/v1/posts/my")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }


    @Test
    @WithAnonymousUser
    void 내피드목록요청시_로그인하지_않은경우_에러발생() throws Exception {
        // TODO : mocking
        mockMvc.perform(get("/api/v1/posts/my")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser
    void 좋아요기능() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }


    @Test
    @WithAnonymousUser
    void 좋아요버튼클릭시_로그인하지_않은경우() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 좋아요버튼클릭시_게시물이_없는경우() throws Exception {
        willThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).given(postService).like(any(), any());

        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}
