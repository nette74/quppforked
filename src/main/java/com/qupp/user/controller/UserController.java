package com.qupp.user.controller;

import com.qupp.jwt.UserContext;
import com.qupp.jwt.Util;
import com.qupp.post.dto.response.ResponseQuestion;
import com.qupp.user.controller.dto.request.RequestCreateUser;
import com.qupp.user.controller.dto.request.RequestEmailUpdate;
import com.qupp.user.controller.dto.request.RequestLogin;
import com.qupp.user.controller.dto.request.RequestNicknameUpdate;
import com.qupp.user.controller.dto.response.ResponseRegister;
import com.qupp.user.controller.dto.response.ResponseUser;
import com.qupp.user.controller.dto.response.ResponseUserUpdate;
import com.qupp.user.controller.dto.response.ResponseLogin;
import com.qupp.user.service.UserService;
import com.qupp.user.controller.dto.response.Response;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@Tag(name = "user", description = "user API")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PreAuthorize("isAnonymous()")
    @Operation(summary = "회원가입", description = "email, password를 입력받아 회원가입 실행", tags = "user")
    @PostMapping("/user")
    public ResponseEntity<ResponseRegister> register(
            @RequestBody RequestCreateUser requestCreateUser
    ) {
        return ResponseEntity.ok(userService.userRegister(requestCreateUser));
    }

    @PreAuthorize("isAnonymous()")
    @Operation(summary = "중복이메일 조회", description = "email이 중복되는지 검사 실행", tags = "user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "사용가능한 이메일입니다."),
            @ApiResponse(code = 400, message = "이미 존재하는 이메일입니다."),
    })
    @GetMapping("/user/duplicate/email")
    public ResponseEntity<Response> isDuplicateEmail(
            @RequestParam("email") String email
    ) {
        String result = "사용가능한 아이디입니다.";
        if (userService.isDuplicateEmail(email)) {
            result = "이미 존재하는 아이디입니다.";
        }
        return ResponseEntity.ok(Response.builder()
                .msg(result)
                .build()
        );
    }

    @PreAuthorize("isAnonymous()")
    @Operation(summary = "중복닉네임 조회", description = "nickname이 중복되는지 검사 실행", tags = "user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "사용가능한 닉네임입니다."),
            @ApiResponse(code = 400, message = "이미 존재하는 닉네임입니다."),
    })
    @GetMapping("/user/duplicate/nickname")
    public ResponseEntity<Response> isDuplicateNickname(
            @RequestParam("nickname") String nickname
    ) {
        String result = "사용가능한 닉네임입니다.";
        if (userService.isDuplicateNickname(nickname)) {
            result = "이미 존재하는 닉네임입니다.";
        }
        return ResponseEntity.ok(Response.builder()
                .msg(result)
                .build()
        );
    }

    @PreAuthorize("isAnonymous()")
    @Operation(summary = "로그인", description = "id, password를 입력받아 로그인 실행", tags = "user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "로그인 성공"),
            @ApiResponse(code = 400, message = "잘못된 접근"),
            @ApiResponse(code = 404, message = "존재하지 않는 리소스 접근")
    })
    @PostMapping("/login")
    public ResponseEntity<ResponseLogin> login(@RequestBody RequestLogin requestLogin) {
        return ResponseEntity.ok(userService.login(requestLogin));
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "프로필 조회 , 접근 제한 API", description = "id로 사용자 정보 조회 ", tags = "user")
    @GetMapping("/user/{id}")
    public ResponseEntity<ResponseUser> loadProfile(@PathVariable long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "프로필 닉네임 수정 , 접근 제한 API", description = "id와 수정을 원하는 정보중 하나를 입력받아서 회원정보 수정 실행", tags = "user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "닉네임 변경 완료"),
            @ApiResponse(code = 400, message = "이미 존재하는 닉네임입니다."),
    })
    @PutMapping("/user/{id}/updateNickname")
    public ResponseEntity<ResponseUserUpdate> updateProfileNickname(
            @PathVariable long id,
            @RequestBody RequestNicknameUpdate requestNicknameUpdate
    ) {
        if (requestNicknameUpdate.getNickname() != null)
            return ResponseEntity.ok(updateNickname(id, requestNicknameUpdate));

        throw new NoSuchElementException("수정할 데이터가 없습니다");
    }

    private ResponseUserUpdate updateNickname(long id, RequestNicknameUpdate requestNicknameUpdate) {
        return userService.updateNickname(id, requestNicknameUpdate);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "프로필 이메일 수정 , 접근 제한 API", description = "id와 수정을 원하는 정보중 하나를 입력받아서 회원정보 수정 실행", tags = "user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "이메일 변경 완료"),
            @ApiResponse(code = 400, message = "이미 존재하는 이메일입니다."),
    })
    @PutMapping("/user/{id}/updateEmail")
    public ResponseEntity<ResponseUserUpdate> updateProfileEmail(
            @PathVariable long id,
            @RequestBody RequestEmailUpdate requestEmailUpdate
            ){
        if (requestEmailUpdate.getEmail() != null)
            return ResponseEntity.ok(updateEmail(id, requestEmailUpdate));

        throw new NoSuchElementException("수정할 데이터가 없습니다");
    }

    private ResponseUserUpdate updateEmail(long id, RequestEmailUpdate requestEmailUpdate) {
        return userService.updateEmail(id, requestEmailUpdate);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "유저 질문 조회 , 접근 제한 API", description = "id를 입력받아서, 유저가 작성한 질문 목록을 조회", tags = "user")
    @GetMapping("/user/{id}/questions")
    public ResponseEntity<Page<ResponseQuestion>> findUserQuestions(
            @PathVariable long id,
            @RequestParam(value = "page", defaultValue = "0") int page
    ) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("registerTime").descending());
        return ResponseEntity.ok(userService.findUserQuestions(id, pageRequest));
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "유저 답변 조회 , 접근 제한 API", description = "id를 입력받아서, 유저가 작성한 답변글이 달린 질문을 조회", tags = "user")
    @GetMapping("/user/{id}/answers")
    public ResponseEntity<Page<ResponseQuestion>> findUserAnswers(
            @PathVariable long id,
            @RequestParam(value = "page", defaultValue = "0") int page
    ) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("registerTime").descending());
        return ResponseEntity.ok(userService.findUserAnswers(id, pageRequest));
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "유저 댓글 조회 , 접근 제한 API", description = "id를 입력받아서, 유저가 작성한 댓글이 달린 질문을 조회", tags = "user")
    @GetMapping("/user/{id}/comments")
    public ResponseEntity<Page<ResponseQuestion>> findUserComments(
            @PathVariable long id,
            @RequestParam(value = "page", defaultValue = "0") int page
    ) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("registerTime").descending());
        return ResponseEntity.ok(userService.findUserComments(id, pageRequest));
    }

}
