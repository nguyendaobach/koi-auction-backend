package fall24.swp391.g1se1868.koiauction.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Register")
public class Register {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer otp;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Long expirationTime;

}
