package co.edu.unbosque.projectFifaUbosque.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "useraccount")
public class User implements UserDetails {

	private static final long serialVersionUID = 1L;
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
	@Column(unique = true, name = "username")
	private String user;
	private String password;
	private String name;
	@Column(unique = true)
	private String personalId;
	private String coutry;
	private String avatar;
	@Column(unique = true)
	private String email;
	private int age;
	private boolean albumCompleteReward = false;
	private boolean tutorialView = false;

	@Column(name = "is_verified")
	private boolean isVerified = false;

	@Column(name = "verificationCode")
	private int verificationCode;

	private boolean countActive = false;

	@Enumerated(EnumType.STRING)
	private Role role;

	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;
	@Column(name = "available_packs")
	private int availablePacks = 5;
	@Column(name = "coins")
	private int coins = 0;

	public User() {
		this.accountNonExpired = true;
		this.accountNonLocked = true;
		this.credentialsNonExpired = true;
		this.enabled = true;
		this.role = null;
	}

	public User(String user, String password, String name, String personalId, String coutry, String avatar,
			String email, boolean albumCompleteReward, int verificationCode, boolean isVerified, boolean tutorialView,
			boolean countActive) {
		this();
		this.user = user;
		this.password = password;
		this.name = name;
		this.personalId = personalId;
		this.coutry = coutry;
		this.avatar = avatar;
		this.email = email;
		this.albumCompleteReward = albumCompleteReward;
		this.verificationCode = verificationCode;
		this.isVerified = isVerified;
		this.tutorialView = tutorialView;
		this.countActive = countActive;
	}

	public enum Role {
		USER, ADMIN, SUPPORT
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public boolean isCountActive() {
		return countActive;
	}

	public void setCountActive(boolean countActive) {
		this.countActive = countActive;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public boolean isVerified() {
		return isVerified;
	}

	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}

	public int getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(int verificationCode) {
		this.verificationCode = verificationCode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCoins() {
		return coins;
	}

	public void setCoins(int coins) {
		this.coins = coins;
	}

	public String getPersonalId() {
		return personalId;
	}

	public void setPersonalId(String personalId) {
		this.personalId = personalId;
	}

	public String getCoutry() {
		return coutry;
	}

	public void setCoutry(String coutry) {
		this.coutry = coutry;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public int getAvailablePacks() {
		return availablePacks;
	}

	public void setAvailablePacks(int availablePacks) {
		this.availablePacks = availablePacks;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isAlbumCompleteReward() {
		return albumCompleteReward;
	}

	public void setAlbumCompleteReward(boolean albumCompleteReward) {
		this.albumCompleteReward = albumCompleteReward;
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, password, user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(id, other.id) && Objects.equals(password, other.password)
				&& Objects.equals(user, other.user);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
	}

	@Override
	public String getUsername() {
		return user;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", user=" + user + ", password=" + password + ", name=" + name + ", personalId="
				+ personalId + ", coutry=" + coutry + ", email=" + email + ", age=" + age + "]";
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public boolean isTutorialView() {
		return tutorialView;
	}

	public void setTutorialView(boolean tutorialView) {
		this.tutorialView = tutorialView;
	}

}
