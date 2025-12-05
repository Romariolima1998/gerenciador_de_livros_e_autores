package com.romario.gerenciador_livro_autor.infrastructure.security;



import com.romario.gerenciador_livro_autor.infrastructure.entity.AutorEntity;
import com.romario.gerenciador_livro_autor.infrastructure.repository.AutorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // Repositório para acessar dados de usuário no banco de dados
    @Autowired
    private AutorRepository autorRepository;

    // Implementação do método para carregar detalhes do usuário pelo e-mail
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busca o usuário no banco de dados pelo e-mail
        AutorEntity autor = autorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        // Cria e retorna um objeto UserDetails com base no usuário encontrado
        return org.springframework.security.core.userdetails.User
                .withUsername(autor.getEmail()) // Define o nome de usuário como o e-mail
                .password(autor.getSenha()) // Define a senha do usuário
                .authorities("ROLE_USER")
                .build(); // Constrói o objeto UserDetails
    }
}
