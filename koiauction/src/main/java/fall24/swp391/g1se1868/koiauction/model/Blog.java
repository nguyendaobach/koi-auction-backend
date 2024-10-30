package fall24.swp391.g1se1868.koiauction.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "BlogPost")
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")  // Match the column name in the database
    private Long id;  // ID bài viết

    @Column(name = "user_id", nullable = false)
    private Integer userId;  // ID người dùng tạo bài viết

    @Column(nullable = false, length = 255)
    private String title;  // Tiêu đề bài viết

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;  // Nội dung bài viết

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt = new Date();  // Ngày tạo bài viết, mặc định là thời điểm hiện tại

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<BlogImage> images;

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<BlogImage> getImages() {
        return images;
    }

    public void setImages(List<BlogImage> images) {
        this.images = images;
    }
}
