package com.romario.gerenciador_livro_autor.controller;

import com.romario.gerenciador_livro_autor.business.AutorService;
import com.romario.gerenciador_livro_autor.business.dtos.AutorInDTO;
import com.romario.gerenciador_livro_autor.business.dtos.AutorOutDTO;
import com.romario.gerenciador_livro_autor.business.dtos.LoginTDO;
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
@Tag(name = "gerenciador de autores")
@SecurityRequirement(name= SecurityConfig.SECURITY_SCHEME)
public class GerenciadorAutorController {

    private final AutorService autorService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginTDO loginDTO){

        return  ResponseEntity.status(201).body(autorService.autenticaAutor(loginDTO));
    }


    @PostMapping("/autor")
    public ResponseEntity<AutorOutDTO> criaAutor(@RequestBody AutorInDTO dto){
        return ResponseEntity.status(201).body(autorService.criaAutor(dto));
    }


    @GetMapping("/autor")
    public ResponseEntity<List<AutorOutDTO>> buscaAutores(@RequestParam int page, @RequestParam int limit){
        return ResponseEntity.ok(autorService.buscaAutores(page, limit));
    }


    @PutMapping("/autor")
    public ResponseEntity<AutorOutDTO> atualizaAutor(
            @RequestHeader(name = "Authorization", required = false) String token, @RequestBody AutorInDTO dto
            ){
        return ResponseEntity.ok(autorService.atualizaAutor(dto, token));
    }


    @DeleteMapping("/autor")
    public ResponseEntity<Void> deletaAutor(@RequestHeader(name = "Authorization", required = false) String token){
        autorService.deleteAutor(token);
        return ResponseEntity.ok().build();
    }
}
