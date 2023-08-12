package com.wanted.internship.entity;

import jakarta.persistence.*;
import lombok.Getter;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "POST")
@Getter
public class Post {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    protected Post() {
    }

    private Post(String content, User user) {
        this.content = content;
        this.user = user;
    }

    public static Post of(String content, User user) {
        return new Post(content, user);
    }
}
