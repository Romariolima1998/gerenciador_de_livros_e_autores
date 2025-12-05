package com.romario.gerenciador_livro_autor.business.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LivroOutDTO {

    private Long id;

    private String nome;

    private String descricao;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dataPublicacao;

    private String autor;
}
