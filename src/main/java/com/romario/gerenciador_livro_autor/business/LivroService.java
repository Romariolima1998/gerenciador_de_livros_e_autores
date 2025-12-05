package com.romario.gerenciador_livro_autor.business;

import com.romario.gerenciador_livro_autor.business.converter.ConverterMapper;
import com.romario.gerenciador_livro_autor.business.converter.UpdateMapper;
import com.romario.gerenciador_livro_autor.business.dtos.LivroInDTO;
import com.romario.gerenciador_livro_autor.business.dtos.LivroOutDTO;
import com.romario.gerenciador_livro_autor.business.exceptions.BadRequestException;
import com.romario.gerenciador_livro_autor.business.exceptions.ForbiddenException;
import com.romario.gerenciador_livro_autor.business.exceptions.ResourceNotFoundException;
import com.romario.gerenciador_livro_autor.infrastructure.entity.AutorEntity;
import com.romario.gerenciador_livro_autor.infrastructure.entity.LivroEntity;
import com.romario.gerenciador_livro_autor.infrastructure.repository.AutorRepository;
import com.romario.gerenciador_livro_autor.infrastructure.repository.LivroRepository;
import com.romario.gerenciador_livro_autor.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;
    private final JwtUtil jwtUtil;
    private final ConverterMapper converter;
    private final AutorRepository autorRepository;
    private final UpdateMapper updateMapper;

    public LivroOutDTO criaLivro(LivroInDTO dto, String token){
        validaNomeNotNull(dto.getNome());
        String email = jwtUtil.extractUsernameToken(token.substring(7));
        AutorEntity autor = autorRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("autor nao existe, pode ter sido deletado")
        );
        LivroEntity livro = converter.paraLivroEntity(dto);
        livro.setAutor(autor);
        return converter.paraLivroOutDTO(livroRepository.save(livro));
    }

    public List<LivroOutDTO> buscaLivros(int page, int limit){
        Pageable pageable = PageRequest.of(page, limit);
        Page<LivroEntity> livros = livroRepository.findAll(pageable);
        return converter.paraListLivroOutDTO(livros.getContent());
    }

    public LivroOutDTO atualizaLivro(Long id, String token, LivroInDTO dto){
        String email = jwtUtil.extractUsernameToken(token.substring(7));
        LivroEntity livro = livroRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("nenhum livro encontrado para esse id: " + id)
        );

        if(!livro.getAutor().getEmail().equals(email)){
            throw new ForbiddenException("esse livro nao pertence a voce, permissao negada");
        }

        updateMapper.updateLivro(dto, livro);

        return converter.paraLivroOutDTO(livroRepository.save(livro));
    }

    public void deletaLivro(Long id, String token){
        String email = jwtUtil.extractUsernameToken(token.substring(7));
        LivroEntity livro = livroRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("nenhum livro encontrado para esse id: " + id)
        );

        if(!livro.getAutor().getEmail().equals(email)){
            throw new ForbiddenException("esse livro nao pertence a voce, permissao negada");
        }

        livroRepository.delete(livro);
    }

// --------------------------validacoes------------------------------------------------

    public void validaNomeNotNull(String nome){
        if(nome == null){
            throw new BadRequestException("o campo nome e obrigatorio");
        }
    }
}
