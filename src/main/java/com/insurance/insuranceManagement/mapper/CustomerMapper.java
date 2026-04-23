package com.insurance.insuranceManagement.mapper;

import com.insurance.insuranceManagement.domain.entity.CustomerEntity;
import com.insurance.insuranceManagement.dto.request.CustomerRequestRecord;
import com.insurance.insuranceManagement.dto.response.CustomerResponseRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CustomerMapper {

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "policies",  ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "country",   defaultValue = "Maroc")
    CustomerEntity toEntity(CustomerRequestRecord customerRequest);

    @Mapping(target = "fullName", expression = "java(customer.getFullName())")
    CustomerResponseRecord toResponse(CustomerEntity customerEntity);

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "email",     ignore = true)
    @Mapping(target = "policies",  ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(CustomerRequestRecord request, @MappingTarget CustomerEntity customer);
}
