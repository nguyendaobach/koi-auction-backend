package fall24.swp391.g1se1868.koiauction.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.tokens.KeyToken;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtService {
    private String secrectKey="9c9d7cc5c9fe5579d1b7b0b0d4ff4b951da90529a7859ac17a6cb0ec63290fcb";

    public String generateToken(String username, int userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        return Jwts.builder()
                .setClaims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+60*60*60*30))
                .signWith(getKey())
                .compact();

    }

    public String extractUserName(String token) {
        // extract the username from jwt token
        return extractClaim(token, Claims::getSubject);
    }
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build().
                parseSignedClaims(token).
                getPayload();
    }
    public int getUserIdFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Integer.class);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private SecretKey getKey(){
        byte[] keyByte= Decoders.BASE64URL.decode(secrectKey);
        return Keys.hmacShaKeyFor(keyByte);
    }

    public static void main(String[] args) {
        JwtService jwtService = new JwtService();
        System.out.println(jwtService.getUserIdFromToken("eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImhhaSIsImlhdCI6MTcyNzgwNjc3MiwiZXhwIjoxNzI3ODEzMjUyfQ.Ht7152qlvN3K8M9Jdfok2K1gMYrWbAbP-BII-bXGcT2gU3NHwV2s-dtpic5KCmxs"));
    }
}
