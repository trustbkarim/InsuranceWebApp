package com.insurance.insuranceManagement.mapper;

import com.insurance.insuranceManagement.domain.entity.PolicyEntity;
import com.insurance.insuranceManagement.dto.request.PolicyRequestRecord;
import com.insurance.insuranceManagement.dto.response.PolicyResponseRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PolicyMapper {

    @Mapping(target = "id",              ignore = true)
    @Mapping(target = "createdAt",       ignore = true)
    @Mapping(target = "updatedAt",       ignore = true)
    @Mapping(target = "createdBy",       ignore = true)
    @Mapping(target = "updatedBy",       ignore = true)
    PolicyEntity toEntity(PolicyRequestRecord policyRequestRecord);

    @Mapping(target = "customerId",             source = "customer.id")
    @Mapping(target = "customerFullName",       expression = "java(policy.getCustomer().getFullName())")
    @Mapping(target = "customerEmail",          source = "customer.email")
    @Mapping(target = "active",                 expression = "java(policy.isActive())")
    PolicyResponseRecord toResponse(PolicyEntity policyEntity);
}