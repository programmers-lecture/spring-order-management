package prgms.lecture.order_management.security;

import org.springframework.util.Assert;

public class JwtAuthentication {

    public final Long id;

    public final String name;

    public JwtAuthentication(Long id, String name) {
        Assert.notNull(id, "id must be provided");
        Assert.notNull(name, "name must be provided");

        this.id = id;
        this.name = name;
    }

}
