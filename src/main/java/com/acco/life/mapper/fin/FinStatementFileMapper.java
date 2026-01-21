package com.acco.life.mapper.fin;

import com.acco.life.dto.fin.FinStatementFileDto;
import com.acco.life.entity.fin.FinStatementFile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * 账单文件导入日志 Mapper
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinStatementFileMapper {

    FinStatementFileDto toDto(FinStatementFile entity);

    FinStatementFile toEntity(FinStatementFileDto dto);

    void updateEntityFromDto(FinStatementFileDto dto, @MappingTarget FinStatementFile entity);
}
