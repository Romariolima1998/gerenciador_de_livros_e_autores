package com.romario.gerenciador_livro_autor.business.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.romario.gerenciador_livro_autor.infrastructure.enums.SexoEnum;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AutorInDTO {
    private String nome;

    private SexoEnum sexo;

    private String email;

    private String senha;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dataNascimento;

    private String paisOrigem;

    private String cpf;

    //private List<LivroInDTO> livros;
}

