package com.romario.gerenciador_livro_autor.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "livro")
public class LivroEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "descricao", length = 240)
    private String descricao;

    @Column(name = "data_publicacao")
    private LocalDate dataPublicacao;

    @ManyToOne
    @JoinColumn(name = "autor_id")
    private AutorEntity autor;

}
