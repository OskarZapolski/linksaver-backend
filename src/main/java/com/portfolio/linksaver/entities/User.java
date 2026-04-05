package com.portfolio.linksaver.entities;

import java.util.ArrayList;
import java.util.List;
import com.portfolio.linksaver.enums.AuthProvider;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long userId;
    
    private String userName;
    private String password;

    @Column(unique = true)
    private String email;

    private String role = "Role_User";

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<Video> videos = new ArrayList<>();

    public User(){}

    public void addVideo(Video video) { //updating videos in method so you always will have updated videos
        this.videos.add(video);
        video.setUser(this);
    }
    
}
