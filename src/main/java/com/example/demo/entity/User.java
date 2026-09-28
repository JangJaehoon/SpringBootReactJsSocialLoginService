package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username; // id
    private String password; // password
    private String role; // 권한 authority
    @Column(length = 500)
    private String refreshToken;
}
