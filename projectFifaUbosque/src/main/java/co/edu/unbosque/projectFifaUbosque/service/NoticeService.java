package co.edu.unbosque.projectFifaUbosque.service;

import co.edu.unbosque.projectFifaUbosque.model.Notice;
import co.edu.unbosque.projectFifaUbosque.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoticeService {
	@Autowired
	private NoticeRepository noticeRepo;

	public List<Notice> getAllNotices() {
		return noticeRepo.findAllByOrderByCreatedAtDesc();
	}

	public Notice saveNotice(Notice notice) {
		notice.setCreatedAt(LocalDateTime.now());
		return noticeRepo.save(notice);
	}

	public void deleteNotice(Long id) {
		noticeRepo.deleteById(id);
	}
}