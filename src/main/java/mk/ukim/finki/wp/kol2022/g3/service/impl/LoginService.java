package mk.ukim.finki.wp.kol2022.g3.service.impl;

import mk.ukim.finki.wp.kol2022.g3.model.ForumUser;
import mk.ukim.finki.wp.kol2022.g3.repository.ForumUserRepository;
import mk.ukim.finki.wp.kol2022.g3.service.ForumUserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LoginService implements UserDetailsService {
    private final ForumUserRepository forumUser;

    public LoginService(ForumUserRepository forumUserService) {
        this.forumUser = forumUserService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ForumUser found = forumUser.findByEmail(username);
        return new User(
                found.getEmail(),
                found.getPassword(),
                Stream.of(new SimpleGrantedAuthority("ROLE_" + found.getType())).collect(Collectors.toList())
        );
    }
}
