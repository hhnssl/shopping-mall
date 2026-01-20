package io.github.hhnssl.shoppingmall.member.repository;

import io.github.hhnssl.shoppingmall.member.domain.Member;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class MemoryMemberRepository implements MemberRepository {

    private static final Map<Long, Member> store = new ConcurrentHashMap<>();
    private static final AtomicLong sequence = new AtomicLong(0);

    @Override
    public Member save(Member member) {
        if (member.getMemberId() == null) {
            long id = sequence.incrementAndGet();
            member.assignId(id);
            store.put(id, member);
        } else {
            // 기존 회원인 경우 업데이트
            store.put(member.getMemberId(), member);
        }
        return member;
    }

    @Override
    public Optional<Member> findById(Long memberId) {

        return Optional.ofNullable(store.get(memberId));
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return store.values().stream()
            .filter(member -> member.getEmail().equals(email))
            .findAny();
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }


    @Override
    public Page<Member> findAll(Pageable pageable) {
        List<Member> allMembers = new ArrayList<>(store.values());

        allMembers.sort(Comparator.comparing(Member::getMemberId).reversed());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allMembers.size());

        List<Member> content = (start > allMembers.size())
            ? Collections.emptyList()
            : allMembers.subList(start, end);

        return new PageImpl<>(content, pageable, allMembers.size());
    }

    @Override
    public void deleteById(Long memberId) {
        store.remove(memberId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return store.values().stream()
            .anyMatch(member -> member.getEmail().equals(email));
    }

    // 테스트 할 때 사용
    public void clearStore() {
        store.clear();
    }
}