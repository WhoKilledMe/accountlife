package com.acco.life.mapper.fin;

import com.acco.life.dto.fin.FinPaymentMethodDto;
import com.acco.life.entity.fin.FinPaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * 支付方式 Mapper
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinPaymentMethodMapper {

    FinPaymentMethodDto toDto(FinPaymentMethod entity);

    FinPaymentMethod toEntity(FinPaymentMethodDto dto);

    void updateEntityFromDto(FinPaymentMethodDto dto, @MappingTarget FinPaymentMethod entity);
}
