package com.ralphmarondev.registry.controller

import com.ralphmarondev.registry.dto.MemberRequest
import com.ralphmarondev.registry.dto.MemberResponse
import com.ralphmarondev.registry.service.MemberService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("member/")
class MemberController(
    private val memberService: MemberService
) {
    @PostMapping
    fun create(@RequestBody request: MemberRequest): ResponseEntity<MemberResponse> {
        val member = memberService.create(request)
        return ResponseEntity.status(201).body(member)
    }

    @GetMapping
    fun getAll(): List<MemberResponse> {
        return memberService.getAll()
    }

    @DeleteMapping("{id}/")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        memberService.delete(id)
        return ResponseEntity.noContent().build()
    }
}