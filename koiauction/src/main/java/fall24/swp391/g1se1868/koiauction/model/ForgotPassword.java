package fall24.swp391.g1se1868.koiauction.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ForgotPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fpid;

    @Column(nullable = false)
    private Integer otp;

    @Column(nullable = false)
    private Date expirationDate;

    @OneToOne
    @JoinColumn(name = "userId", referencedColumnName = "UserID") // Chỉ định rõ tên cột khóa ngoại
    private User user;

}
