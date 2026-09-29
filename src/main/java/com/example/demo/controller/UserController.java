package com.example.demo.controller;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.LoginResponseDto;
import com.example.demo.dto.UserRequestDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDTO request,
                                                  HttpServletResponse response) {
        LoginResponseDto tokens = userService.login(request);

        // Refresh Token을 HttpOnly쿠키로 설정
        // :응답에서 refreshToken은 본문에 담지 않고, HttpOnly 쿠키로만 전달합니다.
        Cookie refreshCookie = new Cookie("refreshToken", tokens.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(refreshCookie);

        // Access Token은 클라이언트가 저장
        return ResponseEntity.ok(new LoginResponseDto(tokens.getAccessToken(), null));
    }


//    @PostMapping("/login")
//    public LoginResponseDto login(@RequestBody LoginRequestDTO request,
//                                  HttpServletResponse response){
//        return userService.login(request); // JWT single 토큰 반환
//    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request){
        // Cookie에서 Refresh Token추출
        String refreshToken = null;
        if(request.getCookies() != null){
            for(Cookie cookie : request.getCookies()){
                if(cookie.getName().equals("refreshToken")){
                    refreshToken = cookie.getValue();
                }
            }
        }

        if(refreshToken == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("유효하지 않은 리프레시 토큰입니다.");
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        if(!refreshToken.equals(user.getRefreshToken())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("서버에 저장된 리프레시 토큰과 다릅니다.");
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(username);
        return ResponseEntity.ok(new LoginResponseDto(newAccessToken, null));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String accessToken,
            HttpServletResponse response){

        String token = accessToken.replace("Bearer ", "");
        String username = jwtTokenProvider.getUsernameFromToken(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow( () -> new RuntimeException("There is no user with username: " + username) );

        user.setRefreshToken(null);
        userRepository.save(user);

        // Delete Cookie
        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setMaxAge(0);
        refreshCookie.setPath("/");
        response.addCookie(refreshCookie);

        return ResponseEntity.ok("Logout Success!");
    }



//    @PostMapping("/refresh")
//    public LoginResponseDto refresh(@RequestHeader("Authorization") String refreshToken){
//        String token = refreshToken.replace("Bearer ", "");
//                        // token값 앞의 Bearer를 없애줌.
//
//        if(!jwtTokenProvider.validateToken(token)){
//            throw new RuntimeException("Refresh token이 유효하지 않습니다.");
//        }
//
//        String username = jwtTokenProvider.getUsernameFromToken(token);
//        User user = userRepository.findByUsername(username).orElseThrow(
//                () -> new RuntimeException("사용자가 존재하지 않습니다.")
//        );
//
//        if(!token.equals(user.getRefreshToken())){
//            throw new RuntimeException("서버에 저장된 Refresh Token과 일치하지 않습니다.");
//        }
//
//        String newAccessToken = jwtTokenProvider.generateAccessToken(username);
//        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);
//
//        user.setRefreshToken(newRefreshToken);
//        userRepository.save(user);
//
//        return ResponseEntity.ok(new LoginResponseDto(newAccessToken, newRefreshToken))
//                .getBody();
//    }

//    @PostMapping("/logout")
//    public String logout(@RequestHeader("Authorization") String accessToken){
//        String token = accessToken.replace("Bearer ", "");
//        String username = jwtTokenProvider.getUsernameFromToken(token);
//        User user = userRepository.findByUsername(username).orElseThrow( ()
//                -> new RuntimeException("There is no user with username: " + username) );
//
//        user.setRefreshToken(null);
//        userRepository.save(user);
//
//        return "LogOut Success !";
//    } // The End of logout



}
