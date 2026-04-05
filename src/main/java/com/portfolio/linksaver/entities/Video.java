package com.portfolio.linksaver.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // automaticly postgresql will add Id
    //without creating new db table to calculate new Id
    @Setter(AccessLevel.NONE)
    private Long videoId;
    @Column(columnDefinition = "TEXT")
    private String url;
    private String category;
    private LocalDateTime saveDate;
    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Video(){}
}