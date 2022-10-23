package prgms.lecture.order_management.user.domain;

public enum Role {

    USER("ROLE_USER");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public static Role of(String name) {
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(name)) {
                return role;
            }
        }
        return null;
    }

    public String value() {
        return value;
    }

}
