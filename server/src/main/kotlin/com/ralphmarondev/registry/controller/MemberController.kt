package com.ralphmarondev.registry.controller

import com.ralphmarondev.registry.dto.MemberRequest
import com.ralphmarondev.registry.dto.MemberResponse
import com.ralphmarondev.registry.service.MemberService
import org.springframework.http.HttpStatus
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
        return ResponseEntity.status(HttpStatus.CREATED).body(member)
    }

    @GetMapping
    fun getAll(): List<MemberResponse> {
        return memberService.getAll()
    }

    @GetMapping("{id}/")
    fun getById(@PathVariable id: Long): ResponseEntity<MemberResponse> {
        return ResponseEntity.ok(memberService.getById(id))
    }

    @GetMapping("family/{familyId}/")
    fun getByFamilyId(@PathVariable familyId: Long): List<MemberResponse> {
        return memberService.getByFamilyId(familyId)
    }

    @PutMapping("{id}/")
    fun update(
        @PathVariable id: Long,
        @RequestBody request: MemberRequest
    ): ResponseEntity<MemberResponse> {
        return ResponseEntity.ok(memberService.update(id, request))
    }

    @DeleteMapping("{id}/")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        memberService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("batch/")
    fun batch(@RequestBody requests: List<MemberRequest>): ResponseEntity<List<MemberResponse>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.batch(requests))
    }
}