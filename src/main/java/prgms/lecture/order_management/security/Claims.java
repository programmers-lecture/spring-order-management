package prgms.lecture.order_management.security;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Arrays;
import java.util.Date;

public class Claims {
    Long userKey;
    String name;
    String[] roles;
    Date iat;
    Date exp;

    private Claims() {}

    Claims(DecodedJWT decodedJWT) {
        Claim userKey = decodedJWT.getClaim("userKey");
        Claim name = decodedJWT.getClaim("name");
        Claim roles = decodedJWT.getClaim("roles");
        if (!userKey.isNull()) {
            this.userKey = userKey.asLong();
        }
        if (!name.isNull()) {
            this.name = name.asString();
        }
        if (!roles.isNull()) {
            this.roles = roles.asArray(String.class);
        }
        this.iat = decodedJWT.getIssuedAt();
        this.exp = decodedJWT.getExpiresAt();
    }

    public static Claims of(long userKey, String name, String[] roles) {
        Claims claims = new Claims();
        claims.userKey = userKey;
        claims.name = name;
        claims.roles = roles;
        return claims;
    }

    @Override
    public String toString() {
        return "Claims{" +
                "userKey=" + userKey +
                ", name='" + name + '\'' +
                ", roles=" + Arrays.toString(roles) +
                ", iat=" + iat +
                ", exp=" + exp +
                '}';
    }
}

