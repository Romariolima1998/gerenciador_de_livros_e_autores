package com.romario.gerenciador_livro_autor.controller;

import com.romario.gerenciador_livro_autor.business.LivroService;
import com.romario.gerenciador_livro_autor.business.dtos.LivroInDTO;
import com.romario.gerenciador_livro_autor.business.dtos.LivroOutDTO;
import com.romario.gerenciador_livro_autor.infrastructure.security.SecurityConfig;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
@Tag(name = "gerenciador de livros")
@SecurityRequirement(name= SecurityConfig.SECURITY_SCHEME)
public class GerenciadorLivroController {

    private final LivroService service;

    @PostMapping("/livro")
    public ResponseEntity<LivroOutDTO> criaLivro(@RequestBody LivroInDTO dto, @RequestHeader(name = "Authorization", required = false) String token){
        return ResponseEntity.status(201).body(service.criaLivro(dto, token));
    }

    @GetMapping("/livro")
    public ResponseEntity<List<LivroOutDTO>> buscaLivros(@RequestParam int page, @RequestParam int limit){
        return ResponseEntity.ok(service.buscaLivros(page, limit));
    }

    @PutMapping("/livro/{id}")
    public ResponseEntity<LivroOutDTO> atualizaLivro(
            @PathVariable Long id, @RequestHeader(name = "Authorization", required = false) String token, @RequestBody LivroInDTO dto
    ){
        return ResponseEntity.ok(service.atualizaLivro(id, token, dto));
    }

    @DeleteMapping("/livro/{id}")
    public ResponseEntity<Void> deletaLivro( @PathVariable Long id, @RequestHeader(name = "Authorization", required = false) String token){
        service.deletaLivro(id, token);
        return ResponseEntity.ok().build();
    }
}
