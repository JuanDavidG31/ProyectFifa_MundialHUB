package co.edu.unbosque.projectFifaUbosque.service;

import java.util.List;

import co.edu.unbosque.projectFifaUbosque.dto.LoginUserDTO;
import co.edu.unbosque.projectFifaUbosque.model.User;

public interface CRUDOperation<T, E> {

	public int create(T data, String rol);

	public List<T> getAll();

	public int deleteById(Long id);

	public int updateById(Long id, T newData);

	public long count();

	public boolean exist(Long id);

	public E encrypt(T data);

	public String decrypt(T data);

	public int updateImage(Long id, T newData);
	
	public int updateStatusTutorial(Long id);

	User encryptLogin(LoginUserDTO data);

}
