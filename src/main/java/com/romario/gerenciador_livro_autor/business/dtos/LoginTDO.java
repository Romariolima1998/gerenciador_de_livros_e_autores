package com.romario.gerenciador_livro_autor.business.dtos;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginTDO {
    private String email;
    private String senha;
}
