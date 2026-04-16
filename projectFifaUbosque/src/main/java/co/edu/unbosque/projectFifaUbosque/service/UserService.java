package co.edu.unbosque.projectFifaUbosque.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.unbosque.projectFifaUbosque.dto.LoginUserDTO;
import co.edu.unbosque.projectFifaUbosque.dto.UserDTO;
import co.edu.unbosque.projectFifaUbosque.model.User;
import co.edu.unbosque.projectFifaUbosque.model.User.Role;
import co.edu.unbosque.projectFifaUbosque.repository.TransactionRepository;
import co.edu.unbosque.projectFifaUbosque.repository.UserRepository;
import co.edu.unbosque.projectFifaUbosque.repository.UserStickerRepository;
import co.edu.unbosque.projectFifaUbosque.util.AESUtil;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;

@Service
public class UserService implements CRUDOperation<UserDTO, User> {

	@Autowired
	private TransactionRepository transactionRepo;

	@Autowired
	private UserStickerRepository userStickerRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public UserService() {
	}

	private User encryptUserFields(User entity) {
		if (entity.getUser() != null)
			entity.setUser(AESUtil.encrypt(entity.getUser()));
		if (entity.getName() != null)
			entity.setName(AESUtil.encrypt(entity.getName()));
		if (entity.getPersonalId() != null)
			entity.setPersonalId(AESUtil.encrypt(entity.getPersonalId()));
		if (entity.getCoutry() != null)
			entity.setCoutry(AESUtil.encrypt(entity.getCoutry()));
		if (entity.getEmail() != null)
			entity.setEmail(AESUtil.encrypt(entity.getEmail()));
		if (entity.getAvatar() != null)
			entity.setAvatar(AESUtil.encrypt(entity.getAvatar()));

		return entity;
	}

	private void decryptDtoFields(UserDTO dto, Long idForLog) {
		try {
			if (dto.getUser() != null && !dto.getUser().isEmpty())
				dto.setUser(AESUtil.decrypt(dto.getUser()));
		} catch (Exception e) {
			if (idForLog != null)
				System.err.println("No se pudo desencriptar el campo 'user' del ID: " + idForLog);
		}
		try {
			if (dto.getName() != null && !dto.getName().isEmpty())
				dto.setName(AESUtil.decrypt(dto.getName()));
		} catch (Exception e) {
			if (idForLog != null)
				System.err.println("No se pudo desencriptar el campo 'name' del ID: " + idForLog);
		}
		try {
			if (dto.getEmail() != null && !dto.getEmail().isEmpty())
				dto.setEmail(AESUtil.decrypt(dto.getEmail()));
		} catch (Exception e) {
			if (idForLog != null)
				System.err.println("No se pudo desencriptar el campo 'email' del ID: " + idForLog);
		}
		try {
			if (dto.getCoutry() != null && !dto.getCoutry().isEmpty())
				dto.setCoutry(AESUtil.decrypt(dto.getCoutry()));
		} catch (Exception e) {
			if (idForLog != null)
				System.err.println("No se pudo desencriptar el campo 'pais' del ID: " + idForLog);
		}
	}

	private boolean isValidCedula(String cedula) {
		if (cedula == null || cedula.length() != 10)
			return false;
		for (char ch : cedula.toCharArray()) {
			if (!Character.isDigit(ch))
				return false;
		}
		return true;
	}

	private boolean isValidPassword(String password) {
		if (password == null || password.length() < 8)
			return false;

		boolean hasLower = false, hasUpper = false, hasDigit = false, hasValidSymbol = false;
		String disallowedSymbols = "<>&\"'/= ";

		for (char ch : password.toCharArray()) {
			if (Character.isLowerCase(ch))
				hasLower = true;
			else if (Character.isUpperCase(ch))
				hasUpper = true;
			else if (Character.isDigit(ch))
				hasDigit = true;
			else if (disallowedSymbols.indexOf(ch) != -1)
				return false;
			else
				hasValidSymbol = true;
		}
		return hasLower && hasUpper && hasDigit && hasValidSymbol;
	}

	@Override
	public long count() {
		return userRepo.count();
	}

	@Override
	public boolean exist(Long id) {
		return userRepo.existsById(id);
	}

	@Override
	public int create(UserDTO data, String rol) {
		User entity = modelMapper.map(data, User.class);

		if (entity.getUser().isBlank() || entity.getPassword().isBlank() || entity.getName().isBlank()
				|| entity.getPersonalId().isBlank() || entity.getCoutry() == null) {
			return 1;
		}

		if (!isValidCedula(entity.getPersonalId())) {
			throw new IllegalArgumentException("La cédula debe tener exacta y únicamente 10 caracteres numéricos.");
		}

		if (!isValidPassword(entity.getPassword())) {
			throw new IllegalArgumentException("La contraseña no cumple con el estándar "
					+ "(mínimo 8 caracteres, al menos una letra minúscula, al menos una letra mayúscula, "
					+ "al menos un número y al menos un símbolo que no sea < > :).");
		}

		if (findUsernameAlreadyTaken(entity.getUsername())) {
			return 1;
		}

		encryptUserFields(entity);
		entity.setPassword(passwordEncoder.encode(data.getPassword()));

		if ("ADMIN".equals(rol)) {
			entity.setRole(Role.ADMIN);
		} else if ("USER".equals(rol)) {
			entity.setRole(Role.USER);
		} else if ("SUPPORT".equals(rol)) {
			entity.setRole(Role.SUPPORT);
		}

		SecureRandom random = new SecureRandom();

		int minimo = 100000;

		int numeroAleatorio = random.nextInt(900000) + minimo;

		entity.setVerificationCode(numeroAleatorio);
		

		userRepo.save(entity);
		return 0;
	}

	@Override
	public User encrypt(UserDTO data) {
		return encryptUserFields(modelMapper.map(data, User.class));
	}

	@Override
	public User encryptLogin(LoginUserDTO data) {
		return encryptUserFields(modelMapper.map(data, User.class));
	}

	@Override
	public String decrypt(UserDTO data) {
		UserDTO dto = modelMapper.map(data, UserDTO.class);
		decryptDtoFields(dto, null);
		return dto.getUser();
	}

	@Override
	public List<UserDTO> getAll() {
		Iterable<User> listaEntity = userRepo.findAll();
		List<UserDTO> listaDto = new ArrayList<>();

		for (User user : listaEntity) {
			UserDTO dto = modelMapper.map(user, UserDTO.class);
			decryptDtoFields(dto, user.getId());
			listaDto.add(dto);
		}

		return listaDto;
	}

	@Override
	@Transactional
	public int deleteById(Long id) {
		Optional<User> found = userRepo.findById(id);

		if (found.isPresent()) {
			User user = found.get();

			try {
				transactionRepo.deleteByUser(user);
				userStickerRepo.deleteByUser(user);

				userRepo.delete(user);
				return 0;
			} catch (Exception e) {
				e.printStackTrace();
				return 2;
			}
		}
		return 1;
	}

	public int deleteByUsername(String username) {
		Optional<User> found = userRepo.findByUser(username);
		if (found.isPresent()) {
			userRepo.delete(found.get());
			return 0;
		}
		return 1;
	}

	@Override
	public int updateImage(Long id, UserDTO newData) {
		Optional<User> found = userRepo.findById(id);
		if (found.isPresent()) {
			found.get().setAvatar(newData.getAvatar());
			userRepo.save(found.get());
			return 0;
		}
		return 2;
	}

	@Override
	public int updateById(Long id, UserDTO newData) {
		Optional<User> found = userRepo.findById(id);

		if (!found.isPresent()) {
			return 2;
		}

		User user = found.get();

		if (newData.getUser() != null && !newData.getUser().isBlank()) {
			String encryptedNewUsername = AESUtil.encrypt(newData.getUser());
			Optional<User> existingUser = userRepo.findByUser(encryptedNewUsername);

			if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
				return 1;
			}
			user.setUser(encryptedNewUsername);
		}

		if (newData.getName() != null && !newData.getName().isBlank()) {
			user.setName(AESUtil.encrypt(newData.getName()));
		}

		if (newData.getEmail() != null && !newData.getEmail().isBlank()) {
			user.setEmail(AESUtil.encrypt(newData.getEmail()));
		}

		if (newData.getPassword() != null && !newData.getPassword().isBlank()) {
			user.setPassword(passwordEncoder.encode(newData.getPassword()));
		}

		if (newData.getCoutry() != null && !newData.getCoutry().isBlank()) {
			user.setCoutry(AESUtil.encrypt(newData.getCoutry()));
		}

		if (newData.getUser() != null && !newData.getUser().isBlank()) {
			String encryptedNewUsername = AESUtil.encrypt(newData.getUser());
			user.setUser(encryptedNewUsername);
		}
		if (newData.getAvatar() != null && !newData.getAvatar().isBlank()) {
			user.setAvatar(newData.getAvatar());
		}
		if (newData.getRole() != null) {
			user.setRole(newData.getRole());
		}

		userRepo.save(user);
		return 0;
	}

	public UserDTO getById(Long id) {
		Optional<User> found = userRepo.findById(id);
		if (found.isPresent()) {
			UserDTO dto = modelMapper.map(found.get(), UserDTO.class);
			decryptDtoFields(dto, id);
			return dto;
		}
		return null;
	}

	public UserDTO getByUser(String user) {
		Optional<User> found = userRepo.findByUser(AESUtil.encrypt(user));
		if (found.isPresent()) {
			UserDTO dto = modelMapper.map(found.get(), UserDTO.class);
			decryptDtoFields(dto, found.get().getId());
			return dto;
		}
		return null;
	}

	public boolean findUsernameAlreadyTaken(String username) {
		return userRepo.findByUser(AESUtil.encrypt(username)).isPresent();
	}

	public int validateCredentials(String username, String password) {
		return userRepo.findByUser(username).filter(u -> passwordEncoder.matches(password, u.getPassword())).map(u -> 0)
				.orElse(1);
	}

	@Override
	public int updateStatusTutorial(Long id) {
		Optional<User> found = userRepo.findById(id);
		if (found.isPresent()) {
			found.get().setTutorialView(true);
			userRepo.save(found.get());
			return 0;
		}
		return 2;
	}

	@Override
	public int updateStatusConnectTrue(Long id) {
		Optional<User> found = userRepo.findById(id);
		if (found.isPresent()) {
			found.get().setCountActive(true);
			userRepo.save(found.get());
			return 0;
		}
		return 2;
	}
	
	@Override
	public int updateStatusConnectFalse(Long id) {
		Optional<User> found = userRepo.findById(id);
		if (found.isPresent()) {
			found.get().setCountActive(false);
			userRepo.save(found.get());
			return 0;
		}
		return 2;
	}
	
	public boolean hasActiveSupport() {
		List<User> users = userRepo.findAll();
		for (User u : users) {
			if (u.getRole() != null && u.getRole().name().equals("SUPPORT") && u.isCountActive()) {
				return true; // Encontró al menos uno
			}
		}
		return false; // No hay nadie conectado
	}
}