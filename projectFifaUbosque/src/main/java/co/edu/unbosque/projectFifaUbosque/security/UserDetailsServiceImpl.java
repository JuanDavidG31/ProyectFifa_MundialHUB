package co.edu.unbosque.projectFifaUbosque.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import co.edu.unbosque.projectFifaUbosque.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String user) throws UsernameNotFoundException {
		return userRepository.findByUser(user)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + user));
	}
}