package com.example.demo.controller;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.LoginResponseDto;
import com.example.demo.dto.UserRequestDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth") // http://localhost:8080/api/auth/ --다양한 요청받음.
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    public String signup(@RequestBody UserRequestDTO userRequestDTO ){
        userService.signUp(userRequestDTO);
        return "SignUp Success! (회원가입 성공!)";
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDTO request,
                                  HttpServletResponse response){
        return userService.login(request); // JWT single 토큰 반환
    }

    @PostMapping("/refresh")
    public LoginResponseDto refresh(@RequestHeader("Authorization") String refreshToken){
        String token = refreshToken.replace("Bearer ", "");
                        // token값 앞의 Bearer를 없애줌.

        if(!jwtTokenProvider.validateToken(token)){
            throw new RuntimeException("Refresh token이 유효하지 않습니다.");
        }

        String username = jwtTokenProvider.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new RuntimeException("사용자가 존재하지 않습니다.")
        );

        if(!token.equals(user.getRefreshToken())){
            throw new RuntimeException("서버에 저장된 Refresh Token과 일치하지 않습니다.");
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(username);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);

        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return ResponseEntity.ok(new LoginResponseDto(newAccessToken, newRefreshToken))
                .getBody();
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String accessToken){
        String token = accessToken.replace("Bearer ", "");
        String username = jwtTokenProvider.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username).orElseThrow( ()
                -> new RuntimeException("There is no user with username: " + username) );

        user.setRefreshToken(null);
        userRepository.save(user);

        return "LogOut Success !";
    }
}
