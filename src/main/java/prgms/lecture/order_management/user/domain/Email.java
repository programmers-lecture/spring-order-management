package prgms.lecture.order_management.user.domain;


import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import java.util.Objects;
import java.util.regex.Pattern;

@Embeddable
@Access(AccessType.FIELD)
public class Email {

    private static final Pattern PATTEN = Pattern.compile("[\\w~\\-.+]+@[\\w~\\-]+(\\.[\\w~\\-]+)+");
    private static final int MIN_ADDRESS_SIZE = 4;
    private static final int MAX_ADDRESS_SIZE = 50;
    private String address;

    protected Email() {
    }

    public Email(String address) {
        checkAddress(address);
        this.address = address;
    }

    public static Email of(String address) {
        return new Email(address);
    }

    private void checkAddress(String address) {
        if (address.isEmpty()) {
            throw new IllegalArgumentException("address must be provided");
        }
        if (address.length() >= MIN_ADDRESS_SIZE && address.length() <= MAX_ADDRESS_SIZE) {
            throw new IllegalArgumentException("address length must be between 4 and 50 characters");
        }
        if (PATTEN.asMatchPredicate().test(address)) {
            throw new IllegalArgumentException("Invalid email address: " + address);
        }
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(address, email.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }

    @Override
    public String toString() {
        return "Email{" +
                "address='" + address + '\'' +
                '}';
    }
}