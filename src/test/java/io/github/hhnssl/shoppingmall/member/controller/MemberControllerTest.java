package io.github.hhnssl.shoppingmall.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hhnssl.shoppingmall.common.exception.BusinessException;
import io.github.hhnssl.shoppingmall.common.exception.ErrorCode;
import io.github.hhnssl.shoppingmall.member.domain.MemberStatus;
import io.github.hhnssl.shoppingmall.member.dto.MemberCreateReq;
import io.github.hhnssl.shoppingmall.member.dto.MemberResponse;
import io.github.hhnssl.shoppingmall.member.dto.MemberUpdateReq;
import io.github.hhnssl.shoppingmall.member.service.MemberService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberController.class) // Controller만 로드하여 가볍게 테스트
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc; // HTTP 요청을 흉내내는 객체

    @Autowired
    private ObjectMapper objectMapper; // 객체 -> JSON 변환기

    @MockitoBean
    private MemberService memberService;

    @Test
    @DisplayName("회원 가입 성공 - 201 Created")
    void register_success() throws Exception {
        // given
        MemberCreateReq req = new MemberCreateReq("test@email.com", "tester", "password123!");
        MemberResponse res = new MemberResponse(1L, "tester", "test@email.com", MemberStatus.ACTIVE);

        // Service가 호출되면 무조건 res를 반환한다고 가정
        given(memberService.register(any(MemberCreateReq.class))).willReturn(res);

        // when & then
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))) // DTO를 JSON 문자열로 변환
            .andDo(print()) // 콘솔에 요청/응답 로그 출력
            .andExpect(status().isCreated()) // 201 상태코드 확인
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.email").value("test@email.com"));
    }

    @Test
    @DisplayName("회원 가입 실패 - 입력값 검증(@Valid) 실패 - 400 Bad Request")
    void register_fail_invalid_input() throws Exception {
        // given
        // 이메일 형식이 아니고, 비밀번호가 너무 짧음
        MemberCreateReq req = new MemberCreateReq("not-email", "tester", "123");

        // when & then
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andDo(print())
            .andExpect(status().isBadRequest()) // 400 에러 확인
            .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
            .andExpect(jsonPath("$.errors").isArray()); // 에러 상세 목록이 있는지 확인
    }

    @Test
    @DisplayName("회원 상세 조회 성공 - 200 OK")
    void findById_success() throws Exception {
        // given
        Long memberId = 1L;
        MemberResponse res = new MemberResponse(memberId, "tester", "test@email.com", MemberStatus.ACTIVE);
        given(memberService.findById(memberId)).willReturn(res);

        // when & then
        mockMvc.perform(get("/api/members/{memberId}", memberId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("tester"));
    }

    @Test
    @DisplayName("회원 상세 조회 실패 - 존재하지 않는 회원 - 404 Not Found")
    void findById_fail_notFound() throws Exception {
        // given
        Long memberId = 999L;
        // Service가 예외를 던지도록 설정
        given(memberService.findById(memberId))
            .willThrow(new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/members/{memberId}", memberId))
            .andExpect(status().isNotFound()) // 404 확인
            .andExpect(jsonPath("$.code").value(ErrorCode.MEMBER_NOT_FOUND.getCode()));
    }

    @Test
    @DisplayName("전체 조회 (페이징) - 200 OK")
    void findAll_paging() throws Exception {
        // given
        // Page<MemberResponse> 가짜 데이터 생성
        List<MemberResponse> content = List.of(
            new MemberResponse(2L, "user2", "u2@a.com", MemberStatus.ACTIVE),
            new MemberResponse(1L, "user1", "u1@a.com", MemberStatus.ACTIVE)
        );
        Page<MemberResponse> pageResult = new PageImpl<>(content, PageRequest.of(0, 10), 2);

        given(memberService.findAll(any(Pageable.class))).willReturn(pageResult);

        // when & then
        // ?page=0&size=5 파라미터 전달 테스트
        mockMvc.perform(get("/api/members")
                .param("page", "0")
                .param("size", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].username").value("user2"))
            .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("회원 수정 성공 - 200 OK")
    void update_success() throws Exception {
        // given
        Long memberId = 1L;
        MemberUpdateReq req = new MemberUpdateReq("updateName", "newPw123!@#");
        MemberResponse res = new MemberResponse(memberId, "updateName", "test@email.com", MemberStatus.ACTIVE);

        given(memberService.update(eq(memberId), any(MemberUpdateReq.class))).willReturn(res);

        // when & then
        mockMvc.perform(patch("/api/members/{memberId}", memberId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("updateName"));
    }

    @Test
    @DisplayName("회원 삭제 성공 - 204 No Content")
    void delete_success() throws Exception {
        // given
        Long memberId = 1L;
        // void 메서드는 특별한 stubbing 없이 호출 여부만 확인하거나, 예외가 안 나면 성공

        // when & then
        mockMvc.perform(delete("/api/members/{memberId}", memberId))
            .andExpect(status().isNoContent()); // 204 확인
    }

    @Test
    @DisplayName("이메일 중복 확인 - 200 OK")
    void existsByEmail() throws Exception {
        // given
        String email = "exist@email.com";
        given(memberService.existsByEmail(email)).willReturn(true);

        // when & then
        mockMvc.perform(get("/api/members/exists")
                .param("email", email))
            .andExpect(status().isOk())
            // ExistsResponse(record)가 {"exists": true}로 잘 변환되는지 확인
            .andExpect(jsonPath("$.exists").value(true));
    }
}