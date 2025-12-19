package com.romario.gerenciador_livro_autor.controller;

import com.romario.gerenciador_livro_autor.business.dtos.AutorInDTO;
import com.romario.gerenciador_livro_autor.business.dtos.LoginTDO;
import com.romario.gerenciador_livro_autor.business.exceptions.BadRequestException;
import com.romario.gerenciador_livro_autor.business.exceptions.ConflictException;
import com.romario.gerenciador_livro_autor.infrastructure.entity.AutorEntity;
import com.romario.gerenciador_livro_autor.infrastructure.enums.SexoEnum;
import com.romario.gerenciador_livro_autor.infrastructure.repository.AutorRepository;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestContainerConfig.class)
class GerenciadorAutorControllerTest {

    @Autowired
    AutorRepository autorRepository;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    AutorEntity criaAutorTest(){
        String senha = "1234";
        AutorEntity autor = AutorEntity.builder()
                .id(null)
                .nome("test")
                .cpf("11111111111")
                .senha(passwordEncoder.encode(senha))
                .dataNascimento(LocalDate.now())
                .email("test@test.com")
                .livros(null)
                .paisOrigem("brasil")
                .sexo(SexoEnum.M)
                .build();
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

    @AfterEach
    void down(){
        autorRepository.deleteAll();
    }

    @Test
    @DisplayName("cria token com sucesso")
    void login() throws Exception {
        AutorEntity autorEntity = criaAutorTest();
        LoginTDO loginTDO = LoginTDO.builder()
                .email(autorEntity.getEmail())
                .senha(autorEntity.getSenha())
                .build();
        String loginRequest = mapper.writeValueAsString(loginTDO);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                .content(loginRequest)
        )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("cria autor com sucesso")
    void criaAutor() throws Exception {
        AutorInDTO autorDTO = AutorInDTO.builder()
                .nome("teste de criacao")
                .email("test@test.com")
                .senha("1234")
                .sexo(SexoEnum.M)
                .dataNascimento(LocalDate.now())
                .paisOrigem("brasil")
                .cpf("111.222.333-44")
                .build();
        String autorJson = mapper.writeValueAsString(autorDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/autor")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(autorJson)
        )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value(autorDTO.getNome()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Erro BadRequest ao criar autor, nome obrigatorio ")
    void criaAutorErrorNomeObrigatorio() throws Exception {
        AutorInDTO autorDTO = AutorInDTO.builder()
                .nome(null)
                .email("test@test.com")
                .senha("1234")
                .sexo(SexoEnum.M)
                .dataNascimento(LocalDate.now())
                .paisOrigem("brasil")
                .cpf("111.222.333-44")
                .build();
        String autorJson = mapper.writeValueAsString(autorDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/autor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(autorJson)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Erro BadRequest ao criar autor, paisOrigem obrigatorio ")
    void criaAutorErrorPaisOrigemObrigatorio() throws Exception {
        AutorInDTO autorDTO = AutorInDTO.builder()
                .nome("test")
                .email("test@test.com")
                .senha("1234")
                .sexo(SexoEnum.M)
                .dataNascimento(LocalDate.now())
                .paisOrigem(null)
                .cpf("111.222.333-44")
                .build();
        String autorJson = mapper.writeValueAsString(autorDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/autor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(autorJson)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Erro BadRequest ao criar autor, email obrigatorio ")
    void criaAutorErrorEmailObrigatorio() throws Exception {
        AutorInDTO autorDTO = AutorInDTO.builder()
                .nome("test")
                .email(null)
                .senha("1234")
                .sexo(SexoEnum.M)
                .dataNascimento(LocalDate.now())
                .paisOrigem("pais")
                .cpf("111.222.333-44")
                .build();
        String autorJson = mapper.writeValueAsString(autorDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/autor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(autorJson)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Erro ConflictException ao cria autor, nome ja existe")
    void criaAutorErroNomeJaExiste() throws Exception {
        AutorEntity autorEntity = criaAutorTest();

        AutorInDTO autorDTO = AutorInDTO.builder()
                .nome(autorEntity.getNome())
                .email("test@test.com")
                .senha("1234")
                .sexo(SexoEnum.M)
                .dataNascimento(LocalDate.now())
                .paisOrigem("brasil")
                .cpf("111.222.333-44")
                .build();

        String autorJson = mapper.writeValueAsString(autorDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/autor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(autorJson)
                )
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ConflictException))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Erro BadRequest ao cria autor, email invalido")
    void criaAutorErroEmailInvalido() throws Exception {
        AutorInDTO autorDTO = AutorInDTO.builder()
                .nome("test")
                .email("testtest.com")
                .senha("1234")
                .sexo(SexoEnum.M)
                .dataNascimento(LocalDate.now())
                .paisOrigem("brasil")
                .cpf("111.222.333-44")
                .build();

        String autorJson = mapper.writeValueAsString(autorDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/autor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(autorJson)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Erro BadRequest ao cria autor, cpf invalido")
    void criaAutorErroCpfInvalido() throws Exception {
        AutorInDTO autorDTO = AutorInDTO.builder()
                .nome("test")
                .email("test@test.com")
                .senha("1234")
                .sexo(SexoEnum.M)
                .dataNascimento(LocalDate.now())
                .paisOrigem("brasil")
                .cpf("111.222.333-44invalido")
                .build();

        String autorJson = mapper.writeValueAsString(autorDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/autor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(autorJson)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Erro ConflictException ao cria autor, email ja existe")
    void criaAutorErroEmailJaExiste() throws Exception {
        AutorEntity autorEntity = criaAutorTest();

        AutorInDTO autorDTO = AutorInDTO.builder()
                .nome("test error")
                .email(autorEntity.getEmail())
                .senha("1234")
                .sexo(SexoEnum.M)
                .dataNascimento(LocalDate.now())
                .paisOrigem("brasil")
                .cpf("111.222.333-44")
                .build();

        String autorJson = mapper.writeValueAsString(autorDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/autor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(autorJson)
                )
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ConflictException))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Erro BadRequest ao cria autor, cpf obrigatorio para regiao do brasil")
    void criaAutorErroCpfObrigatorioBrasil() throws Exception {
        AutorInDTO autorDTO = AutorInDTO.builder()
                .nome("test")
                .email("test@test.com")
                .senha("1234")
                .sexo(SexoEnum.M)
                .dataNascimento(LocalDate.now())
                .paisOrigem("Brasil")
                .cpf(null)
                .build();

        String autorJson = mapper.writeValueAsString(autorDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/autor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(autorJson)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("busca autores com sucesso")
    void buscaAutores() throws Exception {
        AutorEntity autorEntity = criaAutorTest();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/autor")
                .param("page", "0")
                .param("limit", "10")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Atualiza autor com sucesso")
    void atualizaAutor() throws Exception {
        AutorEntity autorEntity = criaAutorTest();
        String token = criaTokenTest(autorEntity);
        AutorInDTO autorDTO = AutorInDTO.builder()
                .nome("test de update")
                .email(null)
                .senha(null)
                .sexo(null)
                .dataNascimento(null)
                .paisOrigem(null)
                .cpf(null)
                .build();
        String autorJson = mapper.writeValueAsString(autorDTO);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/autor")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(autorJson)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value(autorDTO.getNome()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("erro BadRequest ao atualizar autor, email incorreto")
    void atualizaAutorErrorEmail() throws Exception {
        AutorEntity autorEntity = criaAutorTest();
        String token = criaTokenTest(autorEntity);
        AutorInDTO autorDTO = AutorInDTO.builder()
                .nome(null)
                .email("testtest.com")
                .senha(null)
                .sexo(null)
                .dataNascimento(null)
                .paisOrigem(null)
                .cpf(null)
                .build();
        String autorJson = mapper.writeValueAsString(autorDTO);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/autor")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(autorJson)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void deletaAutor() throws Exception {
        AutorEntity autorEntity = criaAutorTest();
        String token = criaTokenTest(autorEntity);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/autor")
                .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}