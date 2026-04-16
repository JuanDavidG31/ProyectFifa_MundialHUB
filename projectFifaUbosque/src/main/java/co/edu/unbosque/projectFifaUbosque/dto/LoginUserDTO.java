package co.edu.unbosque.projectFifaUbosque.dto;

import java.util.Objects;

public class LoginUserDTO {
	private String user;
	private String password;

	public LoginUserDTO() {
	}

	public LoginUserDTO(String user, String password) {
		this.user = user;
		this.password = password;
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

	@Override
	public int hashCode() {
		return Objects.hash(password, user);
	}

	@Override
	public String toString() {
		return "LoginUserDTO [user=" + user + ", password=" + password + "]";
	}

}
