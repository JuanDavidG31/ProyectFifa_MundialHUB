package co.edu.unbosque.projectFifaUbosque.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.projectFifaUbosque.dto.TeamDTO;
import co.edu.unbosque.projectFifaUbosque.model.Team;
import co.edu.unbosque.projectFifaUbosque.model.Team.GroupName;
import co.edu.unbosque.projectFifaUbosque.repository.TeamRepository;
import co.edu.unbosque.projectFifaUbosque.repository.TicketRepository;
@Service
public class TeamService {
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private TeamRepository teamRepository;

	public int create(TeamDTO data, String group) {

		Team entity = modelMapper.map(data, Team.class);

		if (entity.getCountry().isBlank() || entity.getImage().isBlank() || entity.getShortName().isBlank()
				|| entity.getTeamName().isBlank()) {
			return 1;
		}

		if ("A".equals(group)) {
			entity.setGroupName(GroupName.A);
		} else if ("B".equals(group)) {
			entity.setGroupName(GroupName.B);
		} else if ("C".equals(group)) {
			entity.setGroupName(GroupName.C);
		} else if ("D".equals(group)) {
			entity.setGroupName(GroupName.D);
		} else if ("E".equals(group)) {
			entity.setGroupName(GroupName.E);
		} else if ("F".equals(group)) {
			entity.setGroupName(GroupName.F);
		} else if ("G".equals(group)) {
			entity.setGroupName(GroupName.G);
		} else if ("H".equals(group)) {
			entity.setGroupName(GroupName.H);
		} else if ("I".equals(group)) {
			entity.setGroupName(GroupName.I);
		} else if ("J".equals(group)) {
			entity.setGroupName(GroupName.J);
		} else if ("K".equals(group)) {
			entity.setGroupName(GroupName.K);
		} else if ("L".equals(group)) {
			entity.setGroupName(GroupName.L);
		}

		teamRepository.save(entity);

		return 0;

	}
}
