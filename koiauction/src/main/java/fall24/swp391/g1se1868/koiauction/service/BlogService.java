package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.Blog;
import fall24.swp391.g1se1868.koiauction.model.BlogImage;
import fall24.swp391.g1se1868.koiauction.model.UserPrinciple;
import fall24.swp391.g1se1868.koiauction.repository.BlogImageRepository;
import fall24.swp391.g1se1868.koiauction.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private SaveFileService saveFileService;

    @Autowired
    private BlogImageRepository blogImageRepository;

    // 1. Lấy tất cả bài viết blog
    public List<Blog> getAllBlogs() {
        return blogRepository.findAllBlogsWithoutImages();
    }

    // 2. Lấy một bài viết blog theo ID
    public Optional<Blog> getBlogById(Long id) {
        return blogRepository.findById(id);
    }

    // 3. Tạo một bài viết blog mới
    public String createBlog(Blog blog, List<MultipartFile> files) throws IOException {
        // Authenticate user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        // Check allowed image formats
        List<String> allowedFormats = Arrays.asList("image/png", "image/jpeg", "image/jpg", "image/gif", "image/bmp", "image/webp", "image/tiff");
        StringBuilder errorMessages = new StringBuilder();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                errorMessages.append("Each image in the detail must not be empty. ");
            } else {
                String contentType = file.getContentType();
                if (!allowedFormats.contains(contentType.toLowerCase())) {
                    errorMessages.append("Each detail image must be in PNG, JPEG, GIF, BMP, WEBP, or TIFF format. ");
                }
                // Optional: Check file size (e.g., max 5MB)
                if (file.getSize() > 5 * 1024 * 1024) { // 5MB limit
                    errorMessages.append("Each image must not exceed 5MB. ");
                }
            }
        }

        // Get user ID from authentication
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        Integer userId = userPrinciple.getId();
        blog.setUserId(userId);

        // Save the blog
        Blog savedBlog = blogRepository.save(blog);
        List<BlogImage> blogImages = new ArrayList<>();

        // Upload images and create BlogImage instances
        for (MultipartFile file : files) {
            String imageUrl = saveFileService.uploadImage(file); // Upload and get URL
            BlogImage blogImage = new BlogImage();
            blogImage.setImageUrl(imageUrl);
            blogImage.setBlog(savedBlog); // Link BlogImage to Blog
            blogImages.add(blogImage);
        }

        // Save all BlogImage instances to the database
        blogImageRepository.saveAll(blogImages);
        savedBlog.setImages(blogImages); // Update the blog with images
        blogRepository.save(savedBlog); // Return the saved blog
        return "Upload Blog success";
    }



    // 4. Cập nhật một bài viết blog theo ID
    public Optional<Blog> updateBlog(Long id, Blog blogDetails) {
        return blogRepository.findById(id).map(blog -> {
            blog.setTitle(blogDetails.getTitle());
            blog.setContent(blogDetails.getContent());
            blog.setUserId(blogDetails.getUserId());
            blog.setImages(blogDetails.getImages());
            return blogRepository.save(blog);
        });
    }

    // 5. Xóa một bài viết blog theo ID
    public boolean deleteBlog(Long id) {
        if (blogRepository.existsById(id)) {
            blogRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
