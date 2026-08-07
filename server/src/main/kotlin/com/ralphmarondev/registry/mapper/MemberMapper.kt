package com.ralphmarondev.registry.mapper

import com.ralphmarondev.registry.dto.MemberResponse
import com.ralphmarondev.registry.entity.Member

fun Member.toResponse() = MemberResponse(
    id = id,
    family = family.id,
    firstName = firstName,
    middleName = middleName,
    lastName = lastName,
    suffix = suffix,
    sex = sex,
    dateOfBirth = dateOfBirth,
    placeOfBirth = placeOfBirth,
    phoneNumber = phoneNumber,
    civilStatus = civilStatus,
    nationality = nationality,
    religion = religion,
    occupation = occupation,
    educationalAttainment = educationalAttainment,
    isHead = isHead,
    relationshipToHead = relationshipToHead,
    isIndigenous = isIndigenous,
    indigenousGroup = indigenousGroup,
    isDeleted = isDeleted
)