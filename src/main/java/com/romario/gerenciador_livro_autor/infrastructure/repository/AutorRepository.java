package com.romario.gerenciador_livro_autor.infrastructure.repository;

import com.romario.gerenciador_livro_autor.infrastructure.entity.AutorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AutorRepository extends JpaRepository<AutorEntity, Long> {

    Boolean existsByEmail(String email);
    Boolean existsByNome(String nome);


    Optional<AutorEntity> findByEmail(String email);
}
