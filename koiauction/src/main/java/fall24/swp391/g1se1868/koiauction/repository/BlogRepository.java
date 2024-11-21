package fall24.swp391.g1se1868.koiauction.repository;

import fall24.swp391.g1se1868.koiauction.model.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog,Long> {
    @Query("SELECT b FROM Blog b")
    Page<Blog> findAllBlogsWithoutImages(Pageable pageable);

    @Query("SELECT b FROM Blog b WHERE b.userId = :userId")
    List<Blog> findBlogsByUserIdWithoutImages(@Param("userId") Integer userId);
}
