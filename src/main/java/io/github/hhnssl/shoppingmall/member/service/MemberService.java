package io.github.hhnssl.shoppingmall.member.service;


import io.github.hhnssl.shoppingmall.common.exception.BusinessException;
import io.github.hhnssl.shoppingmall.common.exception.ErrorCode;
import io.github.hhnssl.shoppingmall.member.domain.Member;
import io.github.hhnssl.shoppingmall.member.dto.MemberCreateReq;
import io.github.hhnssl.shoppingmall.member.dto.MemberResponse;
import io.github.hhnssl.shoppingmall.member.dto.MemberUpdateReq;
import io.github.hhnssl.shoppingmall.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponse register(MemberCreateReq request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATION);
        }

        Member member = request.toEntity();
        Member saved = memberRepository.save(member);

        return MemberResponse.from(saved);
    }

    public MemberResponse findById(Long memberId) {
        Member member = findMemberOrThrow(memberId);
        return MemberResponse.from(member);
    }

    public Page<MemberResponse> findAll(Pageable pageable) {
        return memberRepository.findAll(pageable)
            .map(MemberResponse::from);
    }

    public MemberResponse update(Long memberId, MemberUpdateReq request) {
        Member member = findMemberOrThrow(memberId);

        if (request.username() != null) {
            member.changeUsername(request.username());
        }
        if (request.password() != null) {
            member.changePassword(request.password());
        }

        Member saved = memberRepository.save(member);
        return MemberResponse.from(saved);
    }

    public void delete(Long memberId) {
        Member member = findMemberOrThrow(memberId);

        member.delete();

        memberRepository.save(member);
    }

    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    private Member findMemberOrThrow(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (!member.isActive()) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }

        return member;
    }
}