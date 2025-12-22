package com.romario.gerenciador_livro_autor.controller;

import com.romario.gerenciador_livro_autor.business.dtos.LivroInDTO;
import com.romario.gerenciador_livro_autor.business.dtos.LoginTDO;
import com.romario.gerenciador_livro_autor.business.exceptions.BadRequestException;
import com.romario.gerenciador_livro_autor.business.exceptions.ForbiddenException;
import com.romario.gerenciador_livro_autor.business.exceptions.ResourceNotFoundException;
import com.romario.gerenciador_livro_autor.infrastructure.entity.AutorEntity;
import com.romario.gerenciador_livro_autor.infrastructure.entity.LivroEntity;
import com.romario.gerenciador_livro_autor.infrastructure.enums.SexoEnum;
import com.romario.gerenciador_livro_autor.infrastructure.repository.AutorRepository;
import com.romario.gerenciador_livro_autor.infrastructure.repository.LivroRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestContainerConfig.class)
class GerenciadorLivroControllerTest {

    @Autowired
    AutorRepository autorRepository;

    @Autowired
    LivroRepository livroRepository;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    AutorEntity criaAutorTest(String nome, String email){
        String senha = "1234";
        AutorEntity autor = AutorEntity.builder()
                .id(null)
                .nome(nome)
                .cpf("11111111111")
                .senha(passwordEncoder.encode(senha))
                .dataNascimento(LocalDate.now())
                .email(email)
                .livros(null)
                .paisOrigem("brasil")
                .sexo(SexoEnum.M)
                .build();
        if(nome == null){
            autor.setNome("test");
        }
        if(email == null){
            autor.setEmail("test@test.com");
        }
        AutorEntity autorEntity = autorRepository.save(autor);
        autorEntity.setSenha(senha);
        return autorEntity;
    }

    String criaTokenTest(AutorEntity autorEntity) throws Exception {
        LoginTDO loginTDO = LoginTDO.builder()
                .email(autorEntity.getEmail())
                .senha(autorEntity.getSenha())
                .build();
        String loginRequest = mapper.writeValueAsString(loginTDO);
        String token = mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(loginRequest)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return token;
    }

    LivroEntity criaLivroTest(AutorEntity autorEntity){
        LivroEntity livro = LivroEntity.builder()
                .id(null)
                .nome("livro test")
                .autor(autorEntity)
                .dataPublicacao(LocalDate.now())
                .descricao("descricao test")
                .build();
        return livroRepository.save(livro);
    }

    @AfterEach
    void down(){
        autorRepository.deleteAll();
        livroRepository.deleteAll();
    }

    @Test
    @DisplayName("cria livro com sucesso")
    void criaLivro() throws Exception {
        AutorEntity autorEntity = criaAutorTest(null, null);
        String token = criaTokenTest(autorEntity);
        LivroInDTO livroDTO = LivroInDTO.builder()
                .dataPublicacao(LocalDate.now())
                .descricao("test descricao")
                .nome("livro test")
                .build();
        String livroDTORequest = mapper.writeValueAsString(livroDTO);
                mockMvc.perform(MockMvcRequestBuilders.post("/api/livro")
                                .header("Authorization", token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(livroDTORequest)
                )
                        .andExpect(MockMvcResultMatchers.status().isCreated())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value(livroDTO.getNome()));

    }

    @Test
    @DisplayName("erro BadRequest ao cria livro")
    void criaLivroErrorNomeObrigatorio() throws Exception {
        AutorEntity autorEntity = criaAutorTest(null, null);
        String token = criaTokenTest(autorEntity);
        LivroInDTO livroDTO = LivroInDTO.builder()
                .dataPublicacao(LocalDate.now())
                .descricao("test descricao")
                .nome(null)
                .build();
        String livroDTORequest = mapper.writeValueAsString(livroDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/livro")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(livroDTORequest)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(
                        result -> Assertions.assertTrue(result.getResolvedException() instanceof BadRequestException)
                );

    }

    @Test
    void buscaLivros() throws Exception {
        AutorEntity autorEntity = criaAutorTest(null, null);
        LivroEntity livroEntity = criaLivroTest(autorEntity);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/livro")
                .param("page", "0")
                .param("limit", "10")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("atualiza livro com sucesso")
    void atualizaLivro() throws Exception {
        AutorEntity autorEntity = criaAutorTest(null, null);
        String token = criaTokenTest(autorEntity);
        LivroEntity livroEntity = criaLivroTest(autorEntity);
        LivroInDTO livroDTO = LivroInDTO.builder()
                .dataPublicacao(null)
                .descricao(null)
                .nome("livro test update")
                .build();
        String livroDTORequest = mapper.writeValueAsString(livroDTO);
        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/livro/{id}", livroEntity.getId())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(livroDTORequest)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value(livroDTO.getNome()));
    }

    @Test
    @DisplayName("Erro NotFound ao atualizar livro")
    void atualizaLivroErroIdNaoExiste() throws Exception {
        AutorEntity autorEntity = criaAutorTest(null, null);
        String token = criaTokenTest(autorEntity);
        LivroInDTO livroDTO = LivroInDTO.builder()
                .dataPublicacao(null)
                .descricao(null)
                .nome("livro test update")
                .build();
        String livroDTORequest = mapper.writeValueAsString(livroDTO);
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/livro/{id}", 1)
                                .header("Authorization", token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(livroDTORequest)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(
                        result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException)
                );
    }

    @Test
    @DisplayName("Erro ForBidden ao atualizar, livro nao pertence ao autor")
    void atualizaLivroErroDePermissao() throws Exception {
        AutorEntity autorEntity1 = criaAutorTest(null, null);
        String token = criaTokenTest(autorEntity1);
        AutorEntity autorEntity2 = criaAutorTest("test de update", "update@test.com");
        LivroEntity livroEntity = criaLivroTest(autorEntity2);
        LivroInDTO livroDTO = LivroInDTO.builder()
                .dataPublicacao(null)
                .descricao(null)
                .nome("livro test update")
                .build();
        String livroDTORequest = mapper.writeValueAsString(livroDTO);
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/livro/{id}", livroEntity.getId())
                                .header("Authorization", token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(livroDTORequest)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(
                        result -> Assertions.assertTrue(result.getResolvedException() instanceof ForbiddenException)
                );
    }

    @Test
    @DisplayName("deleta livro com sucesso")
    void deletaLivro() throws Exception {
        AutorEntity autorEntity = criaAutorTest(null, null);
        LivroEntity livroEntity = criaLivroTest(autorEntity);
        String token = criaTokenTest(autorEntity);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/livro/{id}", livroEntity.getId())
                        .header("Authorization", token)
        )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("erro NotFound ao deletar, id livro nao existe")
    void deletaLivroErroNotFound() throws Exception {
        AutorEntity autorEntity = criaAutorTest(null, null);
        String token = criaTokenTest(autorEntity);
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/livro/{id}", 1)
                                .header("Authorization", token)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(
                        result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException)
                );
    }

        @Test
        @DisplayName("erro Forbidden ao deletar, livro nao pertence ao autor")
        void deletaLivroForbidden() throws Exception {
            AutorEntity autorEntity1 = criaAutorTest(null, null);
            String token = criaTokenTest(autorEntity1);
            AutorEntity autorEntity2 = criaAutorTest("test de update", "update@test.com");
            LivroEntity livroEntity = criaLivroTest(autorEntity2);
            mockMvc.perform(
                            MockMvcRequestBuilders.delete("/api/livro/{id}", livroEntity.getId())
                                    .header("Authorization", token)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(
                            result -> Assertions.assertTrue(result.getResolvedException() instanceof ForbiddenException)
                    );
    }
}