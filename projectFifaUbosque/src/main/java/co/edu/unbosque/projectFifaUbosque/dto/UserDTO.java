package co.edu.unbosque.projectFifaUbosque.dto;

import java.util.Objects;

import co.edu.unbosque.projectFifaUbosque.model.User.Role;
import jakarta.persistence.Column;

public class UserDTO {
	private Long id;
	private String user;
	private String password;
	private String name;
	private String personalId;
	private String coutry;
	private String avatar;
	private Role role;
	private String email;
	private boolean albumCompleteReward = false;
	private boolean isVerified = false;
	private int verificationCode;
	private boolean tutorialView;
	private boolean countActive;

	public UserDTO() {
	}

	public UserDTO(String user, String password, String name, String personalId, String coutry, String avatar,
			String email, boolean albumCompleteReward, int verificationCode, boolean isVerified, boolean tutorialView,
			boolean countActive) {
		this.user = user;
		this.password = password;
		this.name = name;
		this.personalId = personalId;
		this.coutry = coutry;
		this.avatar = avatar;
		this.albumCompleteReward = albumCompleteReward;
		this.verificationCode = verificationCode;
		this.isVerified = isVerified;
		this.tutorialView = tutorialView;
		this.countActive = countActive;

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isCountActive() {
		return countActive;
	}

	public void setCountActive(boolean countActive) {
		this.countActive = countActive;
	}

	public boolean isTutorialView() {
		return tutorialView;
	}

	public void setTutorialView(boolean tutorialView) {
		this.tutorialView = tutorialView;
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

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
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

	public String getPersonalId() {
		return personalId;
	}

	public boolean isAlbumCompleteReward() {
		return albumCompleteReward;
	}

	public void setAlbumCompleteReward(boolean albumCompleteReward) {
		this.albumCompleteReward = albumCompleteReward;
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

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, password, role, user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserDTO other = (UserDTO) obj;
		return Objects.equals(id, other.id) && Objects.equals(password, other.password) && role == other.role
				&& Objects.equals(user, other.user);
	}

	@Override
	public String toString() {
		return "UserDTO [id=" + id + ", user=" + user + ", password=" + password + ", name=" + name + ", personalId="
				+ personalId + ", coutry=" + coutry + ", role=" + role + ", email=" + email + "]";
	}

}