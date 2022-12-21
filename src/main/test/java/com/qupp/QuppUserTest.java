package com.qupp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("local")
public class QuppUserTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    static String accessToken;


//    String authentication() throws Exception {
//        //  "id": 41,
//        //  "email": "testinit@gmail.com",
//        //  "nickname": "testinit1234"
//        // 엑세스 토큰 사용하기
//        MvcResult mvcResult = mvc
//                .perform(
//                    post("/login")
//                        .content("""
//                                        {
//                                          "email": "qupp@unittest.com",
//                                          "password": "2022qupp"
//                                        }
//                                        """.stripIndent())
//                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
//        ).andReturn();
//        accessToken = mvcResult.getResponse().getHeader("Authentication");
//    }

    @Test
    @DisplayName("POST /user 회원가입")
    void t1() throws Exception {
        //Given
        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/user")
                                .content("""
                                        {
                                          "email": "qupp@unittest.com",
                                          "nickname": "unittest1",
                                          "password": "2022qupp"
                                        }
                                        """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                ).andDo(print());
        // Then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.responseUser.email").value("qupp@unittest.com"))
                .andExpect(jsonPath("$.responseUser.nickname").value("unittest1"))
                .andExpect(jsonPath("$.jwtToken").isNotEmpty());
    }

    @Test
    @DisplayName("POST /login 로그인")
    void t2() throws Exception {
        //Given
        mvc.perform(
                post("/user")
                        .content("""
                                        {
                                          "email": "qupp@unittest.com",
                                          "nickname": "unittest1",
                                          "password": "2022qupp"
                                        }
                                        """.stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        );
        // When
        ResultActions resultActions = mvc.perform(
                post("/login")
                        .content("""
                                        {
                                          "email": "qupp@unittest.com",
                                          "password": "2022qupp"
                                        }
                                        """.stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful());
        MvcResult mvcResult = resultActions.andReturn();
        accessToken = mvcResult.getResponse().getHeader("Authentication");
    }

    @Test
    @DisplayName("GET /user/duplicate/email 이메일 중복체크")
    void t3() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/user/duplicate/email")
                                .param("email","qupp@gmail.com")
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                ).andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("GET /user/duplicate/nickname 닉네임 중복체크")
    void t4() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/user/duplicate/nickname")
                                .param("nickname","qupp")
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                ).andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("PUT /user/{id}/updateEmail 이메일 수정")
    void t5() throws Exception {
        //Given

        long id=41;
        mvc.perform(
                post("/login")

                        .content("""
                                        {
                                          "email": "testinit@gmail.com",
                                          "password": "testinit1234"
                                        }
                                        """.stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo(print());
        // When

        ResultActions resultActions = mvc
                .perform(
                        put("/user/%d/updateEmail".formatted(id))
                                .content("""
                                        {
                                          "email": "newemail@qupp.com",
                                        }
                                        """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                ).andDo(print());
        // Then
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("PUT /user/{id}/updateNickname 닉네임 수정")
    void t6() throws Exception {
        //Given
        long id = 41;
        // When
        ResultActions resultActions = mvc
                .perform(
                        put("/user/%d/updateEmail".formatted(id))

                                .content("""
                                        {
                                          "nickname": "newnickname"
                                        }
                                        """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                ).andDo(print());
        // Then
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("GET /user/{id}/questions 유저의 질문 조회")
    void t7() throws Exception {
        long id = 1;
        ResultActions resultActions = mvc
                .perform(
                        get("/user/%d/questions".formatted(id))

                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                ).andDo(print());
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("GET /user/{id}/answers 유저의 질문 조회")
    void t8() throws Exception {
        long id = 1;
        ResultActions resultActions = mvc
                .perform(
                        get("/user/%d/questions".formatted(id))

                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                ).andDo(print());
        resultActions
                .andExpect(status().is2xxSuccessful());

    }

    @Test
    @DisplayName("GET /user/{id}/comments 유저의 댓글 조회")
    void t9() throws Exception {
        long id = 1;
        ResultActions resultActions = mvc
                .perform(
                        get("/user/%d/questions".formatted(id))

                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                ).andDo(print());
        resultActions
                .andExpect(status().is2xxSuccessful());

    }

}
