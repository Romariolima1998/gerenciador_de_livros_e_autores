package com.romario.gerenciador_livro_autor.business.converter;

import com.romario.gerenciador_livro_autor.business.dtos.AutorInDTO;
import com.romario.gerenciador_livro_autor.business.dtos.LivroInDTO;
import com.romario.gerenciador_livro_autor.infrastructure.entity.AutorEntity;
import com.romario.gerenciador_livro_autor.infrastructure.entity.LivroEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UpdateMapper {
    void updateAutor(AutorInDTO dto, @MappingTarget AutorEntity entity);

    void updateLivro(LivroInDTO dto, @MappingTarget LivroEntity entity);
}
