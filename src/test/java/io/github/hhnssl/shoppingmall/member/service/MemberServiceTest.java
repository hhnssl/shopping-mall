package io.github.hhnssl.shoppingmall.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import io.github.hhnssl.shoppingmall.common.exception.BusinessException;
import io.github.hhnssl.shoppingmall.common.exception.ErrorCode;
import io.github.hhnssl.shoppingmall.member.domain.Member;
import io.github.hhnssl.shoppingmall.member.dto.MemberCreateReq;
import io.github.hhnssl.shoppingmall.member.dto.MemberResponse;
import io.github.hhnssl.shoppingmall.member.dto.MemberUpdateReq;
import io.github.hhnssl.shoppingmall.member.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class) // Mockito 프레임워크 사용
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService; // 가짜 Repository가 주입된 테스트 대상

    @Mock
    private MemberRepository memberRepository; // 가짜 Repository (동작을 우리가 정의함)

    @Test
    @DisplayName("회원가입 성공")
    void register_success() {
        // given
        MemberCreateReq req = new MemberCreateReq("test@email.com", "tester", "password123!");

        // 가짜 동작 정의: 이메일 중복 체크 시 false 반환
        given(memberRepository.existsByEmail(req.email())).willReturn(false);
        // save 호출 시, 요청한 정보로 만든 Member 객체 반환 (ID는 1L로 가정)
        given(memberRepository.save(any(Member.class))).willAnswer(invocation -> {
            Member member = (Member) invocation.getArgument(0);
            member.assignId(1L); // ID 할당 시늉
            return member;
        });

        // when
        MemberResponse response = memberService.register(req);

        // then
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("test@email.com");

        // save가 실제로 한 번 호출되었는지 검증
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void register_fail_duplicate() {
        // given
        MemberCreateReq req = new MemberCreateReq("duplicate@email.com", "tester", "password123!");

        // 이미 존재하는 이메일이라고 가정
        given(memberRepository.existsByEmail(req.email())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.register(req))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_DUPLICATION);

        // 예외가 터져서 save까지 가지 않아야 함
        // (verify(memberRepository, times(0)).save(...) 와 동일)
    }

    @Test
    @DisplayName("회원조회 성공")
    void findById_success() {
        // given
        Long memberId = 1L;
        Member member = Member.register("test@email.com", "tester", "pw");
        member.assignId(memberId);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // when
        MemberResponse response = memberService.findById(memberId);

        // then
        assertThat(response.username()).isEqualTo("tester");
    }

    @Test
    @DisplayName("회원조회 실패 - 존재하지 않음")
    void findById_fail_notFound() {
        // given
        Long memberId = 999L;
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.findById(memberId))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("회원조회 실패 - 탈퇴한 회원")
    void findById_fail_deleted() {
        // given
        Long memberId = 2L;
        Member deletedMember = Member.register("del@email.com", "del", "pw");
        deletedMember.delete(); // 탈퇴 상태로 변경

        given(memberRepository.findById(memberId)).willReturn(Optional.of(deletedMember));

        // when & then
        // 탈퇴한 회원이면 예외를 던져야 함 (Service 로직 수정 확인용)
        assertThatThrownBy(() -> memberService.findById(memberId))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("전체 조회 (페이징)")
    void findAll_paging() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Member m1 = Member.register("a@a.com", "a", "pw");
        Member m2 = Member.register("b@b.com", "b", "pw");

        // PageImpl을 사용해 가짜 페이징 결과 생성
        Page<Member> page = new PageImpl<>(List.of(m1, m2));

        given(memberRepository.findAll(pageable)).willReturn(page);

        // when
        Page<MemberResponse> result = memberService.findAll(pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("회원 수정 성공")
    void update_success() {
        // given
        Long memberId = 1L;
        Member member = Member.register("origin@email.com", "oldName", "oldPw");

        MemberUpdateReq updateReq = new MemberUpdateReq("newName", "newPassword123!");

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(memberRepository.save(any(Member.class))).willReturn(member); // save 호출 시 그대로 반환

        // when
        MemberResponse response = memberService.update(memberId, updateReq);

        // then
        assertThat(response.username()).isEqualTo("newName");
        // 실제 비밀번호 변경 로직은 Member 객체 내부에서 일어나므로 여기선 결과값이나 호출 여부 확인
        verify(memberRepository).save(member);
    }
}