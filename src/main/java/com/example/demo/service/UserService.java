package com.example.demo.service;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.UserRequestDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

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

    public String login(LoginRequestDTO request){
        User user =
                userRepository
                        .findByUsername(request.getUsername())
                        .orElseThrow(
                                ()->new RuntimeException
                                        ("Cannot find user(사용자를 찾을 수 없습니다.)")
                        );

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword() ) ){
            throw new RuntimeException("cannot match PW(비밀번호가 일치하지 않습니다.)");
        }

        return jwtTokenProvider.generateToken(user.getUsername());
    }
}
