package com.portfolio.linksaver.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.portfolio.linksaver.dto.VideoResponse;
import com.portfolio.linksaver.entities.User;
import com.portfolio.linksaver.entities.Video;
import java.util.List;
import java.util.Optional;

import com.portfolio.linksaver.dto.VideoResponse;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    @Query("SELECT v.category, " +
            "(SELECT COUNT(v2) FROM Video v2 WHERE v2.category = v.category AND v2.user.userId = :userId), " +
            "v.imageUrl, v.saveDate " +
            "FROM Video v " +
            "WHERE v.user.userId = :userId AND v.saveDate = (" +
            "    SELECT MAX(v3.saveDate) FROM Video v3 WHERE v3.category = v.category AND v3.user.userId = :userId" +
            ")")
    List<Object[]> findCategoryStatsByUserId(@Param("userId") Long userId);

    @Query("SELECT new com.portfolio.linksaver.dto.VideoResponse(v.videoId, v.category, v.saveDate, v.imageUrl, v.url)  "
            +
            "FROM Video v " +
            "WHERE v.category = :category AND v.user = :user ")
    Page<VideoResponse> findVideosByCategory(@Param("category") String category, @Param("user") User user,
            Pageable pageable);

    List<Video> findAllByUser(User user);

    Optional<Video> findByVideoIdAndUser(Long videoId, User user);
}
