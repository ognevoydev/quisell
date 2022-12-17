package com.ognevoydev.quisell.repository;

import com.ognevoydev.quisell.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findActivePostsByDeletedAtIsNull();
@Query(value = """
            SELECT CASE WHEN
            (SELECT id FROM Post WHERE id = :postId) IS NULL
            THEN NULL
            WHEN (SELECT accountId FROM Post WHERE id = :postId) = :accountId
            THEN 'TRUE'
            ELSE 'FALSE' END AS active_status
            """)
    Optional<Boolean> isPostOwner(UUID postId, UUID accountId);
    @Modifying
    @Query(value = "UPDATE Post SET deletedAt = :deletedAt where id = :postId")
    int setDeletedAt(Instant deletedAt, UUID postId);
    @Transactional //saving test post with custom fields
    @Modifying
    @Query(value = """
            INSERT INTO Post (id, accountId, title, description, used, createdAt, updatedAt)
            VALUES (:id, :accountId, :title, :description, :used, :createdAt, :updatedAt)
            """)
    void saveTestPost(UUID id, UUID accountId, String title, String description, boolean used, Instant createdAt, Instant updatedAt);

}
