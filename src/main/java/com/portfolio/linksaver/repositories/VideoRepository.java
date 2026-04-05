package com.portfolio.linksaver.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.portfolio.linksaver.entities.User;
import com.portfolio.linksaver.entities.Video;
import java.util.List;



@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findAllByUser(User user);
} 
