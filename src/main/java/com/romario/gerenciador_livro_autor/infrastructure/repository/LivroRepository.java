package com.romario.gerenciador_livro_autor.infrastructure.repository;

import com.romario.gerenciador_livro_autor.infrastructure.entity.LivroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LivroRepository extends JpaRepository<LivroEntity, Long> {
}
