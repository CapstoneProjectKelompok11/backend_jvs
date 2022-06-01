package com.capstone.booking.service;

import com.capstone.booking.domain.dao.User;
import com.capstone.booking.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //find user by its email
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if(userOptional.isEmpty()){
            throw new UsernameNotFoundException("email not found");
        }
        return userOptional.get();
    }
}
