package com.insurance.insuranceManagement.mapper;

import com.insurance.insuranceManagement.domain.entity.ClaimEntity;
import com.insurance.insuranceManagement.dto.request.ClaimRequestRecord;
import com.insurance.insuranceManagement.dto.response.ClaimResponseRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClaimMapper {

    @Mapping(target = "id",              ignore = true)
    @Mapping(target = "createdAt",       ignore = true)
    @Mapping(target = "updatedAt",       ignore = true)
    @Mapping(target = "createdBy",       ignore = true)
    @Mapping(target = "updatedBy",       ignore = true)
    ClaimEntity toEntity(ClaimRequestRecord claimRequest);

    @Mapping(target = "policyId",         source = "policy.id")
    @Mapping(target = "policyNumber",     source = "policy.policyNumber")
    @Mapping(target = "customerFullName", expression = "java(claim.getPolicy().getCustomer().getFullName())")
    ClaimResponseRecord toResponse(ClaimEntity claim);
}