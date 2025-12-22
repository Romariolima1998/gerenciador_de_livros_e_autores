package com.romario.gerenciador_livro_autor.business;

import com.romario.gerenciador_livro_autor.business.converter.ConverterMapper;
import com.romario.gerenciador_livro_autor.business.converter.UpdateMapper;
import com.romario.gerenciador_livro_autor.business.dtos.AutorInDTO;
import com.romario.gerenciador_livro_autor.business.dtos.AutorOutDTO;
import com.romario.gerenciador_livro_autor.business.dtos.LoginTDO;
import com.romario.gerenciador_livro_autor.business.exceptions.BadRequestException;
import com.romario.gerenciador_livro_autor.business.exceptions.ConflictException;
import com.romario.gerenciador_livro_autor.business.exceptions.ResourceNotFoundException;
import com.romario.gerenciador_livro_autor.business.exceptions.UnaltorizedException;
import com.romario.gerenciador_livro_autor.infrastructure.entity.AutorEntity;
import com.romario.gerenciador_livro_autor.infrastructure.repository.AutorRepository;
import com.romario.gerenciador_livro_autor.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AutorService {

    private final AutorRepository repository;
    private final ConverterMapper converter;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UpdateMapper updateMapper;


    public String autenticaAutor(LoginTDO loginDTO){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getSenha())
            );
            return "Bearer " + jwtUtil.generateToken(authentication.getName());
        } catch (BadCredentialsException | UsernameNotFoundException | AuthorizationDeniedException e) {
            throw new UnaltorizedException("usuario ou senha invalidos", e.getCause());
        }
    }



    public AutorOutDTO criaAutor(AutorInDTO dto){
        validaCamposObrigatorio(dto);
        erroSeNomeJaExiste(dto.getNome());
        erroSeEmailJaExiste(dto.getEmail());
        validaEmail(dto.getEmail());
        cpfObrigatorio(dto);
        dto.setCpf(validacpf(dto.getCpf()));
        dto.setSenha(passwordEncoder.encode(dto.getSenha()));
        AutorEntity  entity = converter.paraAutorEntity(dto);
        return converter.paraAutorDTO(repository.save(entity));
    }


    public AutorOutDTO atualizaAutor(AutorInDTO dto, String token){
        erroSeNomeJaExiste(dto.getNome());
        erroSeEmailJaExiste(dto.getEmail());
        validaEmail(dto.getEmail());
        cpfObrigatorio(dto);
        dto.setCpf(validacpf(dto.getCpf()));
        dto.setSenha(passwordEncoder.encode(dto.getSenha()));

        String email = jwtUtil.extractUsernameToken(token.substring(7));

        AutorEntity entity = repository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("usuario pode ter sido deletado do banco de dados")
        );
        updateMapper.updateAutor(dto, entity);
        return converter.paraAutorDTO(entity);
    }


    public List<AutorOutDTO> buscaAutores(int page, int limit){
        Pageable pageable = PageRequest.of(page, limit);
        Page<AutorEntity> entities = repository.findAll(pageable);
        return converter.paraListAutorDTO(entities.getContent());
    }


    public void deleteAutor(String token){
        String email = jwtUtil.extractUsernameToken(token.substring(7));
        AutorEntity entity = repository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("Usuário nao encontrado, pode ja ter sido deletado")
        );

        repository.delete(entity);
    }

    //_____________VALIDACOES__________________________________________

    public void validaCamposObrigatorio(AutorInDTO dto){
        if(dto.getNome() == null || dto.getPaisOrigem() == null || dto.getEmail() ==null){
            throw new BadRequestException("campos nome, paisOrigem e email obrigatorios");
        }
    }

    public void erroSeNomeJaExiste(String nome){
        Boolean existe = repository.existsByNome(nome);

        if(existe){
            throw new ConflictException("Esse nome ja foi cadastrado: " + nome );
        }
    }

    public void erroSeEmailJaExiste(String email){
        Boolean existe = repository.existsByEmail(email);

        if(existe){
            throw new ConflictException("Esse email ja foi cadastrado: " + email );
        }
    }

    public void validaEmail(String email) {
        Pattern PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$");

        if (email != null) {
            if (!PATTERN.matcher(email).matches()) {
                throw new BadRequestException("email incorreto ou ausente");
            }
        }
    }

    public String validacpf(String cpf){
        Pattern PATTERN = Pattern.compile("\\d{11}");

        if(cpf == null){
            return null;
        }

        cpf = cpf.replace(".", "").replace("-", "");

        if(!PATTERN.matcher(cpf).matches()){
            throw new BadRequestException("cpf incorreto");
        }

        return cpf;
    }

    public void cpfObrigatorio(AutorInDTO dto) {
        if (dto.getPaisOrigem() != null) {
            if (dto.getPaisOrigem().toUpperCase().equals("BRASIL")) {
                if (dto.getCpf() == null) {
                    throw new BadRequestException("Cpf obrigatorio para nacionalidade brasileira");
                }
            }
        }

    }
}
