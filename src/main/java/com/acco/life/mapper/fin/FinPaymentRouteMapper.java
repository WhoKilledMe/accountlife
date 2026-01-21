package com.acco.life.mapper.fin;

import com.acco.life.dto.fin.FinPaymentRouteDto;
import com.acco.life.entity.fin.FinPaymentRoute;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * 支付拆分 Mapper
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinPaymentRouteMapper {

    FinPaymentRouteDto toDto(FinPaymentRoute entity);

    FinPaymentRoute toEntity(FinPaymentRouteDto dto);

    void updateEntityFromDto(FinPaymentRouteDto dto, @MappingTarget FinPaymentRoute entity);
}
