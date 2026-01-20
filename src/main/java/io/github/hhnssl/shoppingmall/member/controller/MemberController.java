package io.github.hhnssl.shoppingmall.member.controller;

import io.github.hhnssl.shoppingmall.member.dto.ExistsResponse;
import io.github.hhnssl.shoppingmall.member.dto.MemberCreateReq;
import io.github.hhnssl.shoppingmall.member.dto.MemberResponse;
import io.github.hhnssl.shoppingmall.member.dto.MemberUpdateReq;
import io.github.hhnssl.shoppingmall.member.service.MemberService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse register(@Valid @RequestBody MemberCreateReq request) {
        return memberService.register(request);
    }

    @GetMapping
    public Page<MemberResponse> findAll(@PageableDefault(size = 10) Pageable pageable) {
        return memberService.findAll(pageable);
    }

    @GetMapping("/{memberId}")
    public MemberResponse findById(@PathVariable Long memberId) {
        return memberService.findById(memberId);
    }

    @PatchMapping("/{memberId}")
    public MemberResponse update(@PathVariable Long memberId, @Valid @RequestBody MemberUpdateReq request) {
        return memberService.update(memberId, request);
    }

    @DeleteMapping("/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long memberId) {
        memberService.delete(memberId);
    }

    @GetMapping("/exists")
    public ExistsResponse existsByEmail(@RequestParam String email) {
        return new ExistsResponse(memberService.existsByEmail(email));
    }
}