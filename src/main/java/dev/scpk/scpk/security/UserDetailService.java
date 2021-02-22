package dev.scpk.scpk.security;

import dev.scpk.scpk.services.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.transaction.Transactional;

public class UserDetailService implements UserDetailsService {
    @Autowired
    private UserService userService;

    @SneakyThrows
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return UserService.convertToExtendedUser(
                this.userService.findOne(s)
        );
    }
}
