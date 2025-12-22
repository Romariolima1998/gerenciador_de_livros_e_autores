package com.romario.gerenciador_livro_autor.infrastructure.entity;

import com.romario.gerenciador_livro_autor.infrastructure.enums.SexoEnum;
import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "autor")
public class AutorEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome",unique = true,  nullable = false)
    private String nome;

    @Column(name = "sexo")
    private SexoEnum sexo;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "senha")
    private String senha;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "pais_origem", length = 50, nullable = false)
    private String paisOrigem;

    @Column(name = "cpf", length = 11)
    private String cpf;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL)
    private List<LivroEntity> livros;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}