package com.romario.gerenciador_livro_autor.business.converter;

import com.romario.gerenciador_livro_autor.business.dtos.AutorInDTO;
import com.romario.gerenciador_livro_autor.business.dtos.AutorOutDTO;
import com.romario.gerenciador_livro_autor.business.dtos.LivroInDTO;
import com.romario.gerenciador_livro_autor.business.dtos.LivroOutDTO;
import com.romario.gerenciador_livro_autor.infrastructure.entity.AutorEntity;
import com.romario.gerenciador_livro_autor.infrastructure.entity.LivroEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConverterMapper {
    ConverterMapper INSTANCE = Mappers.getMapper(ConverterMapper.class);

    AutorEntity paraAutorEntity(AutorInDTO dto);

    AutorOutDTO paraAutorDTO(AutorEntity entity);

    List<AutorOutDTO> paraListAutorDTO(List<AutorEntity> entity);

    @Mapping(source = "autor.nome", target = "autor")
    LivroOutDTO paraLivroOutDTO(LivroEntity entity);

    @Mapping(source = "autor.nome", target = "autor")
    List<LivroOutDTO> paraListLivroOutDTO(List<LivroEntity> entity);

    LivroEntity paraLivroEntity(LivroInDTO dto);

}
