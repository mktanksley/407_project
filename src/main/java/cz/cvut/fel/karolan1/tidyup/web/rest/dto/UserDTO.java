package cz.cvut.fel.karolan1.tidyup.web.rest.dto;

import cz.cvut.fel.karolan1.tidyup.config.Constants;
import cz.cvut.fel.karolan1.tidyup.domain.Authority;
import cz.cvut.fel.karolan1.tidyup.domain.Flat;
import cz.cvut.fel.karolan1.tidyup.domain.User;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO representing a user, with his authorities.
 */
public class UserDTO {

    @NotNull
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 100)
    private String login;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Email
    @Size(min = 5, max = 100)
    private String email;

    private boolean activated = false;

    @Size(min = 2, max = 5)
    private String langKey;

    private Set<String> authorities;

    @Min(value = 0)
    private Integer points;

    @Size(max = 100000)
    private byte[] avatar;

    private String avatarContentType;

    private Flat isAdminOf;

    private Flat memberOf;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this(user.getLogin(), user.getFirstName(), user.getLastName(),
            user.getEmail(), user.getActivated(), user.getLangKey(),
            user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet()),
            user.getPoints(), user.getAvatar(), user.getAvatarContentType(), user.getIsAdminOf(), user.getMemberOf());
    }

    public UserDTO(String login, String firstName, String lastName, String email, boolean activated, String langKey,
                   Set<String> authorities, Integer points, byte[] avatar, String avatarContentType, Flat adminOf, Flat memberOf) {

        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.activated = activated;
        this.langKey = langKey;
        this.authorities = authorities;
        this.points = points;
        this.avatar = avatar;
        this.avatarContentType = avatarContentType;
        this.isAdminOf = adminOf;
        this.memberOf = memberOf;
    }

    public String getLogin() {
        return login;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActivated() {
        return activated;
    }

    public String getLangKey() {
        return langKey;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public String getAvatarContentType() {
        return avatarContentType;
    }

    public void setAvatarContentType(String avatarContentType) {
        this.avatarContentType = avatarContentType;
    }

    public Flat getIsAdminOf() {
        return isAdminOf;
    }

    public void setIsAdminOf(Flat isAdminOf) {
        this.isAdminOf = isAdminOf;
    }

    public Flat getMemberOf() {
        return memberOf;
    }

    public void setMemberOf(Flat memberOf) {
        this.memberOf = memberOf;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
            "login='" + login + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", activated=" + activated +
            ", langKey='" + langKey + '\'' +
            ", authorities=" + authorities +
            ", points=" + points +
            ", avatar=" + Arrays.toString(avatar) +
            ", avatarContentType='" + avatarContentType + '\'' +
            ", isAdminOf=" + isAdminOf +
            ", memberOf=" + memberOf +
            '}';
    }
}
