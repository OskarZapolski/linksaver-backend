package com.portfolio.linksaver.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.portfolio.linksaver.dto.VideoResponse;
import com.portfolio.linksaver.entities.User;
import com.portfolio.linksaver.entities.Video;
import java.util.List;
import com.portfolio.linksaver.dto.VideoResponse;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    @Query("SELECT v.category, COUNT(v), v.imageUrl, v.saveDate " +
            "FROM Video v " +
            "WHERE v.user.userId = :userId " +
            "GROUP BY v.category")
    List<Object[]> findCategoryStatsByUserId(@Param("userId") Long userId);

    @Query("SELECT new com.portfolio.linksaver.dto.VideoResponse(v.category, v.saveDate, v.imageUrl, v.url)  " +
            "FROM Video v " +
            "WHERE v.category = :category AND v.user = :user " +
            "ORDER BY v.saveDate DESC")
    List<VideoResponse> findVideosByCategory(@Param("category") String category, @Param("user") User user);

    List<Video> findAllByUser(User user);
}
