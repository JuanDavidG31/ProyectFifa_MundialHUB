package co.edu.unbosque.projectFifaUbosque.service;

import co.edu.unbosque.projectFifaUbosque.dto.ItineraryEventDTO;
import co.edu.unbosque.projectFifaUbosque.model.ItineraryEvent;
import co.edu.unbosque.projectFifaUbosque.repository.ItineraryEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItineraryEventService {

	@Autowired
	private ItineraryEventRepository repository;

	public List<ItineraryEventDTO> getUserItinerary(String userEmail) {
		return repository.findByUserEmail(userEmail).stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	public List<ItineraryEventDTO> saveEvents(List<ItineraryEventDTO> dtos) {
		List<ItineraryEvent> events = dtos.stream().map(this::convertToEntity).collect(Collectors.toList());

		List<ItineraryEvent> savedEvents = repository.saveAll(events);

		return savedEvents.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	public void deleteEvent(Long id) {
		repository.deleteById(id);
	}

	private ItineraryEventDTO convertToDTO(ItineraryEvent entity) {
		ItineraryEventDTO dto = new ItineraryEventDTO();
		dto.setId(entity.getId());
		dto.setUserEmail(entity.getUserEmail());
		dto.setEventType(entity.getEventType());
		dto.setTitle(entity.getTitle());
		dto.setEventDate(entity.getEventDate());
		dto.setLocation(entity.getLocation());
		return dto;
	}

	private ItineraryEvent convertToEntity(ItineraryEventDTO dto) {
		ItineraryEvent entity = new ItineraryEvent();
		entity.setUserEmail(dto.getUserEmail());
		entity.setEventType(dto.getEventType());
		entity.setTitle(dto.getTitle());
		entity.setEventDate(dto.getEventDate());
		entity.setLocation(dto.getLocation());
		return entity;
	}
}