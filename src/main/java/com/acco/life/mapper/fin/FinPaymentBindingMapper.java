package com.acco.life.mapper.fin;

import com.acco.life.dto.fin.FinPaymentBindingDto;
import com.acco.life.entity.fin.FinPaymentBinding;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * 支付方式绑定 Mapper
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinPaymentBindingMapper {

    FinPaymentBindingDto toDto(FinPaymentBinding entity);

    FinPaymentBinding toEntity(FinPaymentBindingDto dto);

    void updateEntityFromDto(FinPaymentBindingDto dto, @MappingTarget FinPaymentBinding entity);
}
