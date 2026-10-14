package com.splitgo.api.member;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.splitgo.api.member.dto.CreateMemberRequest;
import com.splitgo.api.member.dto.MemberResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/groups/{groupId}/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse create(@PathVariable UUID groupId, @Valid @RequestBody CreateMemberRequest request) {
        return memberService.create(groupId, request);
    }
}
