package fall24.swp391.g1se1868.koiauction.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "BlogImage")
public class BlogImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ImageId")  // Match the column name in the database
    private Long id;  // ID của hình ảnh

    @Column(name = "ImageUrl", nullable = false, length = 500)
    private String imageUrl;  // URL hoặc đường dẫn tới hình ảnh

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PostId", nullable = false)
    @JsonIgnore  // Ngăn không cho serializa trường này
    private Blog blog;

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }
}
