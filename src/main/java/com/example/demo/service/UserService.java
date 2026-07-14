package com.example.demo.service;

import com.example.demo.dto.UserRequestDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signUp(UserRequestDTO userRequestDTO){
//        User user = new User();
//        user.setUsername(userRequestDTO.getUsername());
        if(userRepository.findByUsername( userRequestDTO.getUsername() ).isPresent() ){
            // 이미 존재하는 사용자
            throw new RuntimeException("이미 존재하는 사용자 입니다.");
        }

        User user = User.builder()
                .username(userRequestDTO.getUsername() )
                .password( passwordEncoder.
                        encode(userRequestDTO.getPassword() ) )
                .role("ROLE_USER")
                .build();

        userRepository.save(user);
    }
}
