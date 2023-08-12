package com.wanted.internship.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "REFRESHTOKEN")
public class RefreshToken {
    @Id
    private String id;

    @JoinColumn(name = "user_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private String keyValue;

}
