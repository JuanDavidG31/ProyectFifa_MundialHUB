package co.edu.unbosque.projectFifaUbosque.controller;

import co.edu.unbosque.projectFifaUbosque.model.Notice;
import co.edu.unbosque.projectFifaUbosque.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@CrossOrigin(origins = "*")
public class NoticeController {
	@Autowired
	private NoticeService noticeService;

	@GetMapping("/all")
	public List<Notice> getAll() {
		return noticeService.getAllNotices();
	}

	@PostMapping("/create")
	public Notice create(@RequestBody Notice notice) {
		return noticeService.saveNotice(notice);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		noticeService.deleteNotice(id);
		return ResponseEntity.ok().build();
	}
}