package io.github.hhnssl.shoppingmall.member.repository;

import io.github.hhnssl.shoppingmall.member.domain.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(Long memberId);
    Optional<Member> findByEmail(String email);

    List<Member> findAll();

    Page<Member> findAll(Pageable pageable);

    void deleteById(Long memberId);
    boolean existsByEmail(String email);
}