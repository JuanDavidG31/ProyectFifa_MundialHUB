package co.edu.unbosque.projectFifaUbosque.configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.edu.unbosque.projectFifaUbosque.model.Sticker;
import co.edu.unbosque.projectFifaUbosque.model.Team;
import co.edu.unbosque.projectFifaUbosque.model.Team.GroupName;
import co.edu.unbosque.projectFifaUbosque.model.User;
import co.edu.unbosque.projectFifaUbosque.model.User.Role;
import co.edu.unbosque.projectFifaUbosque.repository.StickerRepository;
import co.edu.unbosque.projectFifaUbosque.repository.TeamRepository;
import co.edu.unbosque.projectFifaUbosque.repository.UserRepository;
import co.edu.unbosque.projectFifaUbosque.util.AESUtil;

@Configuration
public class LoadDatabase {
	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

	@Bean
	CommandLineRunner initDatabase(UserRepository userRepo, PasswordEncoder passwordEncoder,
			StickerRepository stickerRepo, TeamRepository teamRepository) {

		return args -> {
			Optional<User> found = userRepo.findByUser(AESUtil.encrypt("admin"));
			if (found.isPresent()) {
				log.info("El administrador ya existe, omitiendo la creación del administrador...");
			} else {
				User adminUser = new User(AESUtil.encrypt("admin"), passwordEncoder.encode("1234567890"), null, null,
						null, null, null, false, 0, true, true);
				adminUser.setRole(Role.ADMIN);
				adminUser.setAvailablePacks(50);
				userRepo.save(adminUser);
				userRepo.save(adminUser);
				log.info("Precargando usuario administrador");
			}

			Optional<User> found2 = userRepo.findByUser(AESUtil.encrypt("normaluser"));
			if (found2.isPresent()) {
				log.info("El usuario normal ya existe, omitiendo la creación del usuario normal...");
			} else {
				User normalUser = new User(AESUtil.encrypt("normaluser"), passwordEncoder.encode("1234567890"), null,
						null, null, null, null, false, 0, true, true);
				normalUser.setRole(Role.USER);
				normalUser.setAvailablePacks(50);
				userRepo.save(normalUser);
				userRepo.save(normalUser);
				log.info("Precargando usuario normal");
			}
			
			Optional<User> found3 = userRepo.findByUser(AESUtil.encrypt("support"));
			if (found3.isPresent()) {
				log.info("El soporte ya existe, omitiendo la creación del soporte...");
			} else {
				User supportUser = new User(AESUtil.encrypt("support"), passwordEncoder.encode("1234567890"), null, null,
						null, null, null, false, 0, true, true);
				supportUser.setRole(Role.SUPPORT);
				supportUser.setAvailablePacks(50);
				userRepo.save(supportUser);
				userRepo.save(supportUser);
				log.info("Precargando usuario soporte");
			}

			if (stickerRepo.count() == 0) {
				log.info("Iniciando carga del Catálogo del Álbum 2026...");

				List<Sticker> catalogoInicial = Arrays.asList(
						// ==========================================
						// 🇦🇷 ARGENTINA (ARG)
						// ==========================================
						new Sticker("ARG-01", "Lionel Messi", "selecciones", "Argentina",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRkEY3UvCo4XJRw9MpLHQZwPIETextJ7JlzTg&s",
								"Legendaria"),
						new Sticker("ARG-02", "Emiliano Martínez", "selecciones", "Argentina",
								"https://www.ole.com.ar/2021/11/25/8UYMlyhW1_720x0__1.jpg", "Épica"),
						new Sticker("ARG-03", "Julián Álvarez", "selecciones", "Argentina",
								"https://www.ole.com.ar/2021/10/20/QYAIbyABH_720x0__1.jpg", "Épica"),
						new Sticker("ARG-04", "Rodrigo De Paul", "selecciones", "Argentina",
								"https://game-assets.fut.gg/cdn-cgi/image/quality=85,format=auto,width=200/2025/futgg-player-item-card/25-67321480.acabaa4898ca2c4926034cddb1fce952622c98cd52db6bb6e4630880427fbe17.webp",
								"Épica"),
						new Sticker("ARG-05", "Cristian Romero", "selecciones", "Argentina",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/232488_es.webp",
								"Épica"),
						new Sticker("ARG-06", "Nicolás Otamendi", "selecciones", "Argentina",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/192366_es.webp",
								"Común"),
						new Sticker("ARG-07", "Alexis Mac Allister", "selecciones", "Argentina",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/239837_en.webp",
								"Épica"),
						new Sticker("ARG-08", "Enzo Fernández", "selecciones", "Argentina",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/247090_es.webp",
								"Épica"),
						new Sticker("ARG-09", "Lautaro Martínez", "selecciones", "Argentina",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRv_p9kcdpCh8Bbbge_h65tB8e2RUxwGRenAA&s",
								"Épica"),
						new Sticker("ARG-10", "Nahuel Molina", "selecciones", "Argentina",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/233084_en.webp",
								"Común"),
						new Sticker("ARG-11", "Nicolás Tagliafico", "selecciones", "Argentina",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/211256_en.webp",
								"Común"),
						new Sticker("ARG-12", "Alejandro Garnacho", "selecciones", "Argentina",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/268438_es.webp",
								"Común"),

						// ==========================================
						// 🇧🇷 BRASIL (BRA)
						// ==========================================
						new Sticker("BRA-01", "Vinícius Júnior", "selecciones", "Brasil",
								"https://media.tycsports.com/files/2021/04/06/254434/fifa-21-vinicius-junior_w862.png",
								"Legendaria"),
						new Sticker("BRA-02", "Rodrygo", "selecciones", "Brasil",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ3VvXP49lzi_LPDptkPWB9hrimlCBguVhf8w&s",
								"Épica"),
						new Sticker("BRA-03", "Alisson Becker", "selecciones", "Brasil",
								"https://game-assets.fut.gg/cdn-cgi/image/quality=85,format=auto,width=200/2026/futgg-player-item-card/26-212831.35dc70150fef8b461657f65b4c7dbc0cd17c0e7afb0f62546bda806845d0f9a6.webp",
								"Épica"),
						new Sticker("BRA-04", "Marquinhos", "selecciones", "Brasil",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/207865_es.webp",
								"Épica"),
						new Sticker("BRA-05", "Lucas Paquetá", "selecciones", "Brasil",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/233927_es.webp",
								"Común"),
						new Sticker("BRA-06", "Ederson", "selecciones", "Brasil",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/210257_es.webp",
								"Épica"),
						new Sticker("BRA-07", "Gabriel Magalhães", "selecciones", "Brasil",
								"https://game-assets.fut.gg/cdn-cgi/image/quality=85,format=auto,width=200/2026/futgg-player-item-card/26-232580.cb3250e5d4fd5ce2d6c795adc1f6e0a07bff95c87136333ef4b44b5da5cacdd4.webp",
								"Común"),
						new Sticker("BRA-08", "Bruno Guimarães", "selecciones", "Brasil",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/247851_es.webp",
								"Épica"),
						new Sticker("BRA-09", "Raphinha", "selecciones", "Brasil",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/233419_es.webp",
								"Épica"),
						new Sticker("BRA-10", "Endrick", "selecciones", "Brasil",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/272505_es.webp",
								"Épica"),
						new Sticker("BRA-11", "Gabriel Martinelli", "selecciones", "Brasil",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/251566_es.webp",
								"Común"),

						// ==========================================
						// 🇨🇴 COLOMBIA (COL)
						// ==========================================
						new Sticker("COL-01", "Luis Díaz", "selecciones", "Colombia",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/241084_es.webp",
								"Épica"),
						new Sticker("COL-02", "James Rodríguez", "selecciones", "Colombia",
								"https://pbs.twimg.com/media/GXK0p5-W8AAWCh1.jpg", "Épica"),
						new Sticker("COL-03", "Camilo Vargas", "selecciones", "Colombia",
								"https://game-assets.fut.gg/cdn-cgi/image/quality=85,format=auto,width=200/2022/cards/212067.webp",
								"Común"),
						new Sticker("COL-04", "Richard Ríos", "selecciones", "Colombia",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/262881_en.webp",
								"Común"),
						new Sticker("COL-05", "Jhon Arias", "selecciones", "Colombia",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/241602_es.webp",
								"Común"),
						new Sticker("COL-06", "Daniel Muñoz", "selecciones", "Colombia",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/237646_es.webp",
								"Común"),
						new Sticker("COL-07", "Davinson Sánchez", "selecciones", "Colombia",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTdZPo9xHzlQwjo_8T-MZ1VlcfGnGOtSvD5Cw&s",
								"Común"),
						new Sticker("COL-08", "Jhon Lucumí", "selecciones", "Colombia",
								"https://game-assets.fut.gg/cdn-cgi/image/quality=85,format=auto,width=200/2026/futgg-player-item-card/26-231207.cdb762e645ebe4eed6aba94895b6720d86d8a760ccfded413cf05af5664bfd9d.webp",
								"Común"),
						new Sticker("COL-09", "Johan Mojica", "selecciones", "Colombia",
								"https://game-assets.fut.gg/cdn-cgi/image/quality=85,format=auto,width=200/2026/futgg-player-item-card/26-50545674.282fa056e7175ae56ce857fb6544b6f13141300e63952457f86cdb5fce9bd252.webp",
								"Común"),
						new Sticker("COL-10", "Jefferson Lerma", "selecciones", "Colombia",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/213991_es.webp",
								"Común"),
						new Sticker("COL-11", "Jhon Córdoba", "selecciones", "Colombia",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSjoG7zgkVvtFbWS-A4Q2mKlmN0vIIr2JrnXw&s",
								"Común"),
						new Sticker("COL-12", "Kevin Castaño", "selecciones", "Colombia",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/272951_en.webp",
								"Común"),

						// ==========================================
						// 🇫🇷 FRANCIA (FRA)
						// ==========================================
						new Sticker("FRA-01", "Kylian Mbappé", "selecciones", "Francia",
								"https://pbs.twimg.com/media/E_MCCOuX0AkU2GQ.jpg", "Legendaria"),
						new Sticker("FRA-02", "Antoine Griezmann", "selecciones", "Francia",
								"https://i.pinimg.com/564x/b7/e5/f0/b7e5f09295db6a766eaf467a5b1909d9.jpg", "Épica"),
						new Sticker("FRA-03", "Eduardo Camavinga", "selecciones", "Francia",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/248243_es.webp",
								"Épica"),
						new Sticker("FRA-04", "Mike Maignan", "selecciones", "Francia",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/215698_mx.webp",
								"Épica"),
						new Sticker("FRA-05", "Jules Koundé", "selecciones", "Francia",
								"https://pbs.twimg.com/media/FYdC_PFXkAUuYtP.png", "Épica"),
						new Sticker("FRA-06", "Dayot Upamecano", "selecciones", "Francia",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQdz0VJKUwuszFFfcZ_vch-bk3y6dQTCnyPQg&s",
								"Común"),
						new Sticker("FRA-07", "William Saliba", "selecciones", "Francia",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/243715_es.webp",
								"Épica"),
						new Sticker("FRA-08", "Theo Hernández", "selecciones", "Francia",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/232656_es.webp",
								"Épica"),
						new Sticker("FRA-09", "Aurélien Tchouaméni", "selecciones", "Francia",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ9QwqP03kWGoPkc8egK2GOQ5ylycKcfSDhUg&s",
								"Épica"),
						new Sticker("FRA-10", "Ousmane Dembélé", "selecciones", "Francia",
								"https://game-assets.fut.gg/cdn-cgi/image/quality=85,format=auto,width=200/2025/futgg-player-item-card/25-151226387.0155089c0e4589bcd5081c264f1946905104acb01fea9b6501fb93c16904045f.webp",
								"Épica"),
						new Sticker("FRA-11", "Marcus Thuram", "selecciones", "Francia",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/228093_es.webp",
								"Común"),

						// ==========================================
						// 🇪🇸 ESPAÑA (ESP)
						// ==========================================
						new Sticker("ESP-01", "Lamine Yamal", "selecciones", "España",
								"https://i.pinimg.com/736x/32/9c/0f/329c0fdfa80e6ea7b2693fbd9e623c39.jpg", "Épica"),
						new Sticker("ESP-02", "Nico Williams", "selecciones", "España",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/256516_es.webp",
								"Épica"),
						new Sticker("ESP-03", "Rodri", "selecciones", "España",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRtJPRkHrThiijbD78ecGvRWAcPe0tsTdJXnA&s",
								"Legendaria"),

						new Sticker("ESP-04", "Pedri", "selecciones", "España",
								"https://preview.redd.it/i-think-this-should-be-pedris-card-for-fc26-i-wasnt-really-v0-7ngasef4yiue1.jpeg?width=640&crop=smart&auto=webp&s=08f480fbc8fb4fb41222d510a00c5e4285c60aab",
								"Épica"),
						new Sticker("ESP-05", "Unai Simón", "selecciones", "España",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/230869_en.webp",
								"Común"),
						new Sticker("ESP-06", "Dani Carvajal", "selecciones", "España",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT0oZYh7Mp7IX891WjdmNst2GuL61O8zoSPlg&s",
								"Épica"),
						new Sticker("ESP-07", "Aymeric Laporte", "selecciones", "España",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/212218_es.webp",
								"Común"),
						new Sticker("ESP-08", "Marc Cucurella", "selecciones", "España",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/239231_es.webp",
								"Común"),
						new Sticker("ESP-09", "Fabián Ruiz", "selecciones", "España",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/226271_en.webp",
								"Épica"),
						new Sticker("ESP-10", "Dani Olmo", "selecciones", "España",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/244260_en.webp",
								"Épica"),
						new Sticker("ESP-11", "Álvaro Morata", "selecciones", "España",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQAB7hsa0CADOL4BK_5GZGsfEw5emvA_25MBg&s",
								"Común"),

						// ==========================================
						// 🏴󠁧󠁢󠁥󠁮󠁧󠁿 INGLATERRA (ENG)
						// ==========================================
						new Sticker("ENG-01", "Jude Bellingham", "selecciones", "Inglaterra",
								"https://preview.redd.it/jude-bellingham-tots-card-fair-or-not-v0-0sboi1b83xvc1.png?auto=webp&s=fa139a010e15010e7ebb81bd990b830056566b28",
								"Legendaria"),
						new Sticker("ENG-02", "Harry Kane", "selecciones", "Inglaterra",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTNrmiyjyy7MOBj6KCr6Afb9o9veTKfM_BPEQ&s",
								"Épica"),
						new Sticker("ENG-03", "Phil Foden", "selecciones", "Inglaterra",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/237692_es.webp",
								"Épica"),
						new Sticker("ENG-04", "Bukayo Saka", "selecciones", "Inglaterra",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/246669_es.webp",
								"Épica"),
						new Sticker("ENG-05", "Declan Rice", "selecciones", "Inglaterra",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/234378_es.webp",
								"Épica"),
						new Sticker("ENG-06", "Jordan Pickford", "selecciones", "Inglaterra",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/204935_es.webp",
								"Común"),
						new Sticker("ENG-07", "Kyle Walker", "selecciones", "Inglaterra",
								"https://pbs.twimg.com/media/FyGmZmWXgAANQqg.png", "Épica"),
						new Sticker("ENG-08", "John Stones", "selecciones", "Inglaterra",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/203574_en.webp",
								"Común"),
						new Sticker("ENG-09", "Luke Shaw", "selecciones", "Inglaterra",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/205988_en.webp",
								"Común"),
						new Sticker("ENG-10", "Cole Palmer", "selecciones", "Inglaterra",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/257534_es.webp",
								"Épica"),
						new Sticker("ENG-11", "Trent Alexander-Arnold", "selecciones", "Inglaterra",
								"https://game-assets.fut.gg/cdn-cgi/image/quality=85,format=auto,width=200/2025/futgg-player-item-card/25-84117361.2694c6ad813dd24d10c0423d50f07c5e645adfbef959e716afe4ec0bcbf7eeee.webp",
								"Épica"),

						// ==========================================
						// 🇺🇸 ESTADOS UNIDOS (USA) - Anfitrión
						// ==========================================
						new Sticker("USA-01", "Christian Pulisic", "selecciones", "Estados Unidos",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/227796_mx.webp",
								"Épica"),
						new Sticker("USA-02", "Weston McKennie", "selecciones", "Estados Unidos",
								"https://game-assets.fut.gg/cdn-cgi/image/quality=85,format=auto,width=300/2026/player-item-card/26-67347608.501cef5381a61be551c7ef4b9ac00f0f849b60ddfe5bf5bb450e19d65de7a83f.webp",
								"Común"),
						new Sticker("USA-03", "Tyler Adams", "selecciones", "Estados Unidos",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/232999_mx.webp",
								"Común"),
						new Sticker("USA-04", "Gio Reyna", "selecciones", "Estados Unidos",
								"https://game-assets.fut.gg/cdn-cgi/image/quality=85,format=auto,width=300/2026/futgg-player-item-card/26-50577189.0e2c478101223f20814f10174cf6d8df77751c742b63588812f4e2b099967a14.webp",
								"Común"),
						new Sticker("USA-05", "Matt Turner", "selecciones", "Estados Unidos",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQJSINt_x-IYHD4l-ksF9PoK5vgxR7koJtdcA&s",
								"Común"),
						new Sticker("USA-06", "Sergiño Dest", "selecciones", "Estados Unidos",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/251804_en.webp",
								"Común"),
						new Sticker("USA-07", "Antonee Robinson", "selecciones", "Estados Unidos",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/229348_es.webp",
								"Común"),
						new Sticker("USA-08", "Yunus Musah", "selecciones", "Estados Unidos",
								"https://game-assets.fut.gg/cdn-cgi/image/quality=85,format=auto,width=200/2023/cards/futgg-cards/67362041.webp",
								"Común"),
						new Sticker("USA-09", "Tim Weah", "selecciones", "Estados Unidos",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/241496_en.webp",
								"Común"),
						new Sticker("USA-10", "Folarin Balogun", "selecciones", "Estados Unidos",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/247463_es.webp",
								"Común"),
						new Sticker("USA-11", "Chris Richards", "selecciones", "Estados Unidos",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/250954_en.webp",
								"Común"),

						// ==========================================
						// 🇲🇽 MÉXICO (MEX) - Anfitrión
						// ==========================================
						new Sticker("MEX-01", "Edson Álvarez", "selecciones", "México",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTEv9p58xSM1DNgnpVpO-zWI5LDD3RRWNRn6w&s",
								"Épica"),
						new Sticker("MEX-02", "Santiago Giménez", "selecciones", "México",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/245152_mx.webp",
								"Épica"),
						new Sticker("MEX-03", "Hirving Lozano", "selecciones", "México",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTQrS5ojh_8ABrfCQMGeeOh9TtR6lOcQQxQ4w&s",
								"Épica"),
						new Sticker("MEX-04", "Guillermo Ochoa", "selecciones", "México",
								"https://i.pinimg.com/736x/8e/56/af/8e56afaa3f170c17f810f99146e56508.jpg", "Épica"),

						new Sticker("MEX-05", "César Montes", "selecciones", "México",
								"https://game-assets.fut.gg/cdn-cgi/image/quality=85,format=auto,width=200/2024/futgg-player-item-card/24-229980.3d7fdafffae07bf4000b3c6e9ab7e01229384493f40674d48394cba2ae117f56.webp",
								"Común"),
						new Sticker("MEX-06", "Johan Vásquez", "selecciones", "México",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/244349_es.webp",
								"Común"),
						new Sticker("MEX-07", "Jorge Sánchez", "selecciones", "México",
								"https://pbs.twimg.com/media/E_rmBCXWUAAb3QZ.jpg", "Común"),
						new Sticker("MEX-08", "Luis Chávez", "selecciones", "México",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSHjJyV3Uc61NUkgNnmz2X95k52s7Am3ahdLA&s",
								"Común"),
						new Sticker("MEX-09", "Luis Romo", "selecciones", "México",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTmdHtZb1_eY8cTcWtpz-7nhe38IHp38GvbbQ&s",
								"Común"),
						new Sticker("MEX-10", "Uriel Antuna", "selecciones", "México",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSSb8MWszhfqeN-YaFElWzovWcC-HAp3lB6YQ&s",
								"Común"),
						new Sticker("MEX-11", "Julián Quiñones", "selecciones", "México",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRWT1ujeL6OPXuoEF-M7F6pgPmzLhmJH7qWZw&s",
								"Común"),

						// ==========================================
						// 🇩🇪 ALEMANIA (GER)
						// ==========================================
						new Sticker("GER-01", "Jamal Musiala", "selecciones", "Alemania",
								"https://game-assets.fut.gg/cdn-cgi/image/quality=85,format=auto,width=200/2026/futgg-player-item-card/26-50588438.9e26035e5856b01b049de9b214efe27f3432e34b36e5c6006d1103a1f0c3406d.webp",
								"Épica"),
						new Sticker("GER-02", "Florian Wirtz", "selecciones", "Alemania",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTcJnbqT2BiyrJ6ff122d19xKmyoPu_7nXLKw&s",
								"Épica"),
						new Sticker("GER-03", "Antonio Rüdiger", "selecciones", "Alemania",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ0hXeziRfoyZxqtxj5q0sjoOkrOAoigsVa1w&s",
								"Épica"),
						new Sticker("GER-04", "Joshua Kimmich", "selecciones", "Alemania",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRfB9I6bcEZonmqoyGEKthMkIEV-yxq_BCeqA&s",
								"Épica"),
						new Sticker("GER-05", "Marc-André ter Stegen", "selecciones", "Alemania",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS0PxXQNEeY3MdohzvDY8yJp18-EpMr4Rh2PQ&s",
								"Épica"),
						new Sticker("GER-06", "Jonathan Tah", "selecciones", "Alemania",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTH8bix58k6uY0T2Pkf--3OUlbi33jSy3escA&s",
								"Común"),
						new Sticker("GER-07", "Robert Andrich", "selecciones", "Alemania",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTSNGw6CwGk534gwV_-PIY4ofHWDG3HC_yQUA&s",
								"Común"),
						new Sticker("GER-08", "Leroy Sané", "selecciones", "Alemania",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSmu4hAlKWPJNAM8YKe_m34gLJsb-FwLb_gHA&s",
								"Épica"),
						new Sticker("GER-09", "Kai Havertz", "selecciones", "Alemania",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ3rrKnJe6K8HfKuhOr9p6BXl8-vGx7LlWiYg&s",
								"Épica"),
						new Sticker("GER-10", "Niclas Füllkrug", "selecciones", "Alemania",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQwkOUKoWHal6YICAwkUr04xbcADQeypk6L-Q&s",
								"Común"),

						// ==========================================
						// 🇵🇹 PORTUGAL (POR)
						// ==========================================
						new Sticker("POR-01", "Cristiano Ronaldo", "selecciones", "Portugal",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQtsl89IdKWy9VI1V875dxtNZ46PiBiu9PfYQ&s",
								"Legendaria"),
						new Sticker("POR-02", "Bruno Fernandes", "selecciones", "Portugal",
								"https://pbs.twimg.com/media/EECoAF0XsAE4d7P.png", "Épica"),
						new Sticker("POR-03", "Bernardo Silva", "selecciones", "Portugal",
								"https://mirrorcdn.soccerguru.live/cards/master/8a974245-5b84-4353-9619-58832c370ef9.png",
								"Épica"),
						new Sticker("POR-04", "Rúben Dias", "selecciones", "Portugal",
								"https://i.redd.it/after-seeing-ruben-dias-reaction-to-his-fifa-23-card-i-v0-caa49ave3ap91.jpg?width=828&format=pjpg&auto=webp&s=98609da9d8de27400215e1fc05a1c0c81a87162b",
								"Épica"),
						new Sticker("POR-05", "Diogo Costa", "selecciones", "Portugal",
								"https://game-assets.fut.gg/cdn-cgi/image/quality=85,format=auto,width=200/2025/futgg-player-item-card/25-84120657.daf88f6efd93b60f98bfc69cf10277b6adb7cdc9056b2a455b8ecb99e72c8613.webp",
								"Épica"),
						new Sticker("POR-06", "João Cancelo", "selecciones", "Portugal",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQwawTAl4g4c-sbgE0AHI0AEmMgIMva_UeLrg&s",
								"Épica"),
						new Sticker("POR-07", "Nuno Mendes", "selecciones", "Portugal",
								"https://ratings-images-prod.pulse.ea.com/FC26/components/items/252145_en.webp",
								"Común"),
						new Sticker("POR-08", "Vitinha", "selecciones", "Portugal",
								"https://game-assets.fut.gg/cdn-cgi/image/quality=85,format=auto,width=200/2025/futgg-player-item-card/25-67364117.891b077e2769af18c7c9be3acce4170f1f7685a0791acb6c98d5b590cd7215af.webp",
								"Épica"),
						new Sticker("POR-09", "Rafael Leão", "selecciones", "Portugal",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS8YDC7ezNSTZ_oQTb-70IKc-nXVzg6aMsDoA&s",
								"Épica"),
						new Sticker("POR-10", "Diogo Jota", "selecciones", "Portugal",
								"https://pbs.twimg.com/media/Gu69AG1W4AAMHuo.jpg", "Común"),

						// ==========================================
						// 🏟️ ESTADIOS OFICIALES 2026
						// ==========================================
						new Sticker("EST-01", "Estadio Azteca (MEX)", "estadios", "Estadios Oficiales",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRJafCPgRXZGY8lSmNfk-wonKU-FaaKsCqwsQ&s",
								"Legendaria"),
						new Sticker("EST-02", "MetLife Stadium (USA)", "estadios", "Estadios Oficiales",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRCYAp6jPr0LZZOsaQg88bo1iSfjMQltGIjqw&s",
								"Épica"),
						new Sticker("EST-03", "SoFi Stadium (USA)", "estadios", "Estadios Oficiales",
								"https://www.isecinc.com/wp-content/uploads/2023/04/SoFi-Stadium-CF-133181_02.jpg",
								"Épica"),
						new Sticker("EST-04", "BMO Field (CAN)", "estadios", "Estadios Oficiales",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT8tOGcs2AbdguAFYooof3-rydTh0qb3d2Qcg&s",
								"Común"),
						new Sticker("EST-05", "Hard Rock Stadium (USA)", "estadios", "Estadios Oficiales",
								"https://thumbs.dreamstime.com/b/vista-a%C3%A9rea-fotograf%C3%ADa-con-drones-del-estadio-hard-rock-de-los-delfines-miami-en-el-super-bowl-liv-gardens-florida-usa-noviembre-165328583.jpg",
								"Común"),
						new Sticker("EST-06", "Mercedes-Benz Stadium (USA)", "estadios", "Estadios Oficiales",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTdtZRUTbzxuRmjvJkWfCNDj_x9-ls63AUaJA&s",
								"Legendaria"),
						new Sticker("EST-07", "Estadio BBVA (MEX)", "estadios", "Estadios Oficiales",
								"https://upload.wikimedia.org/wikipedia/commons/5/57/Mexico_Guadalupe_Monterrey_Estadio_BBVA_Bancomer_fifa_world_cup_2026_6.JPG",
								"Común"),

						// ==========================================
						// ⭐ LEYENDAS DEL MUNDIAL
						// ==========================================
						new Sticker("CAT-01", "Pelé (BRA)", "categorias", "Leyendas del Mundial",
								"https://i.pinimg.com/736x/d5/65/61/d56561a3469c43e52add047005eecc80.jpg",
								"Legendaria"),
						new Sticker("CAT-02", "Diego Maradona (ARG)", "categorias", "Leyendas del Mundial",
								"https://i.pinimg.com/736x/51/17/24/51172433899594f811fde587883d8603.jpg",
								"Legendaria"),
						new Sticker("CAT-03", "Zinedine Zidane (FRA)", "categorias", "Leyendas del Mundial",
								"https://i.pinimg.com/736x/da/98/6c/da986cfa5ba2e984c271312735aeef6b.jpg",
								"Legendaria"),
						new Sticker("CAT-04", "Ronaldo Nazário (BRA)", "categorias", "Leyendas del Mundial",
								"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTpuwWr4EvInYuMdOAAsZEoXByEQw7ihEhUsw&s",
								"Legendaria"),
						new Sticker("CAT-05", "Andrés Iniesta (ESP)", "categorias", "Leyendas del Mundial",
								"https://preview.redd.it/since-iniesta-one-of-the-best-ever-who-played-this-game-has-v0-ha7ceoddi4sd1.jpeg?auto=webp&s=daec6c1f99c3aadcaccf20d554dc624c1b896505",
								"Legendaria"),
						new Sticker("CAT-06", "Miroslav Klose (GER)", "categorias", "Leyendas del Mundial",
								"https://game-assets.fut.gg/cdn-cgi/image/quality=85,format=auto,width=200/2026/futgg-player-item-card/26-242510.a7d8c620012005ba4aaa3cc4193acd226a5f3cc984e13fb2e6cd7d37e68da829.webp",
								"Legendaria"));

				stickerRepo.saveAll(catalogoInicial);
				log.info("Catálogo cargado exitosamente: " + catalogoInicial.size() + " láminas.");
			} else {
				log.info("El catálogo del álbum ya estaba cargado previamente.");
			}

			if (teamRepository.count() == 0) {
				log.info("Iniciando carga del Catálogo de equipos...");

				List<Team> catalogoInicial = Arrays.asList(

						// GRUPO A: México, Sudáfrica, Corea del Sur, Chequia
						new Team("México", "MEX", "https://crests.football-data.org/769.svg", "México", GroupName.A, 15,
								1681),
						new Team("Sudáfrica", "RSA", "https://crests.football-data.org/9396.svg", "Sudáfrica",
								GroupName.A, 60, 1429),
						new Team("Corea del Sur", "KOR", "https://crests.football-data.org/772.png", "Corea del Sur",
								GroupName.A, 25, 1588),
						new Team("Chequia", "CZE", "https://crests.football-data.org/798.svg", "Chequia", GroupName.A,
								41, 1501),

						// GRUPO B: Canadá, Bosnia y Herzegovina, Catar, Suiza
						new Team("Canadá", "CAN", "https://crests.football-data.org/canada.svg", "Canadá", GroupName.B,
								30, 1556),
						new Team("Bosnia y Herzegovina", "BIH", "https://crests.football-data.org/bosnia.svg",
								"Bosnia y Herzegovina", GroupName.B, 65, 1385),
						new Team("Catar", "QAT", "https://crests.football-data.org/8030.svg", "Catar", GroupName.B, 55,
								1454),
						new Team("Suiza", "SUI", "https://crests.football-data.org/788.svg", "Suiza", GroupName.B, 19,
								1649),

						// GRUPO C: Brasil, Marruecos, Haití, Escocia
						new Team("Brasil", "BRA", "https://crests.football-data.org/764.svg", "Brasil", GroupName.C, 6,
								1761),
						new Team("Marruecos", "MAR", "https://crests.football-data.org/morocco.svg", "Marruecos",
								GroupName.C, 8, 1755),
						new Team("Haití", "HAI", "https://crests.football-data.org/haiti.svg", "Haití", GroupName.C, 83,
								1291),
						new Team("Escocia", "SCO", "https://crests.football-data.org/814.svg", "Escocia", GroupName.C,
								43, 1498),

						// GRUPO D: Estados Unidos, Paraguay, Australia, Turquía
						new Team("Estados Unidos", "USA", "https://crests.football-data.org/usa.svg", "Estados Unidos",
								GroupName.D, 16, 1673),
						new Team("Paraguay", "PAR", "https://crests.football-data.org/761.svg", "Paraguay", GroupName.D,
								40, 1503),
						new Team("Australia", "AUS", "https://crests.football-data.org/779.svg", "Australia",
								GroupName.D, 27, 1580),
						new Team("Turquía", "TUR", "https://crests.football-data.org/803.svg", "Turquía", GroupName.D,
								22, 1599),

						// GRUPO E: Alemania, Curazao, Costa de Marfil, Ecuador
						new Team("Alemania", "GER", "https://crests.football-data.org/759.svg", "Alemania", GroupName.E,
								10, 1730),
						new Team("Curazao", "CUW", "https://crests.football-data.org/curacao.svg", "Curazao",
								GroupName.E, 82, 1294),
						new Team("Costa de Marfil", "CIV", "https://crests.football-data.org/787.svg",
								"Costa de Marfil", GroupName.E, 34, 1532),
						new Team("Ecuador", "ECU", "https://crests.football-data.org/791.svg", "Ecuador", GroupName.E,
								23, 1594),

						// GRUPO F: Países Bajos, Japón, Suecia, Túnez
						new Team("Países Bajos", "NED", "https://crests.football-data.org/8601.svg", "Países Bajos",
								GroupName.F, 7, 1757),
						new Team("Japón", "JPN", "https://crests.football-data.org/766.svg", "Japón", GroupName.F, 18,
								1660),
						new Team("Suecia", "SWE", "https://crests.football-data.org/792.svg", "Suecia", GroupName.F, 38,
								1514),
						new Team("Túnez", "TUN", "https://crests.football-data.org/tunisia.svg", "Túnez", GroupName.F,
								44, 1483),

						// GRUPO G: Bélgica, Egipto, Irán, Nueva Zelanda
						new Team("Bélgica", "BEL", "https://crests.football-data.org/805.svg", "Bélgica", GroupName.G,
								9, 1734),
						new Team("Egipto", "EGY", "https://crests.football-data.org/825.svg", "Egipto", GroupName.G, 29,
								1563),
						new Team("Irán", "IRN", "https://crests.football-data.org/iran.svg", "Irán", GroupName.G, 21,
								1615),
						new Team("Nueva Zelanda", "NZL", "https://crests.football-data.org/783.svg", "Nueva Zelanda",
								GroupName.G, 85, 1281),

						// GRUPO H: España, Cabo Verde, Arabia Saudita, Uruguay
						new Team("España", "ESP", "https://crests.football-data.org/760.svg", "España", GroupName.H, 2,
								1876),
						new Team("Cabo Verde", "CPV", "https://crests.football-data.org/cape_verde.svg", "Cabo Verde",
								GroupName.H, 69, 1366),
						new Team("Arabia Saudita", "KSA", "https://crests.football-data.org/saudi_arabia.svg",
								"Arabia Saudita", GroupName.H, 61, 1421),
						new Team("Uruguay", "URU", "https://crests.football-data.org/758.svg", "Uruguay", GroupName.H,
								17, 1673),

						// GRUPO I: Francia, Senegal, Irak, Noruega
						new Team("Francia", "FRA", "https://crests.football-data.org/773.svg", "Francia", GroupName.I,
								1, 1877),
						new Team("Senegal", "SEN", "https://crests.football-data.org/senegal.svg", "Senegal",
								GroupName.I, 14, 1688),
						new Team("Irak", "IRQ", "https://crests.football-data.org/iraq.svg", "Irak", GroupName.I, 57,
								1447),
						new Team("Noruega", "NOR", "https://crests.football-data.org/813.svg", "Noruega", GroupName.I,
								31, 1550),

						// GRUPO J: Argentina, Argelia, Austria, Jordania
						new Team("Argentina", "ARG", "https://crests.football-data.org/762.png", "Argentina",
								GroupName.J, 3, 1874),
						new Team("Argelia", "ALG", "https://crests.football-data.org/algeria.svg", "Argelia",
								GroupName.J, 28, 1564),
						new Team("Austria", "AUT", "https://crests.football-data.org/816.svg", "Austria", GroupName.J,
								24, 1593),
						new Team("Jordania", "JOR", "https://crests.football-data.org/8049.png", "Jordania",
								GroupName.J, 63, 1391),

						// GRUPO K: Portugal, RD Congo, Uzbekistán, Colombia
						new Team("Portugal", "POR", "https://crests.football-data.org/765.svg", "Portugal", GroupName.K,
								5, 1763),
						new Team("RD Congo", "COD", "https://crests.football-data.org/congo_dr.svg", "RD Congo",
								GroupName.K, 46, 1478),
						new Team("Uzbekistán", "UZB", "https://crests.football-data.org/8070.png", "Uzbekistán",
								GroupName.K, 50, 1465),
						new Team("Colombia", "COL", "https://crests.football-data.org/818.svg", "Colombia", GroupName.K,
								13, 1693),

						// GRUPO L: Inglaterra, Croacia, Ghana, Panamá
						new Team("Inglaterra", "ENG", "https://crests.football-data.org/770.svg", "Inglaterra",
								GroupName.L, 4, 1825),
						new Team("Croacia", "CRO", "https://crests.football-data.org/799.svg", "Croacia", GroupName.L,
								11, 1717),
						new Team("Ghana", "GHA", "https://crests.football-data.org/ghana.svg", "Ghana", GroupName.L, 74,
								1346),
						new Team("Panamá", "PAN", "https://crests.football-data.org/panama.svg", "Panamá", GroupName.L,
								33, 1540));
				teamRepository.saveAll(catalogoInicial);
				log.info("Catálogo cargado exitosamente: " + catalogoInicial.size() + " equipos.");
			} else {
				log.info("El catálogo de equipos ya estaba cargado previamente.");
			}
		};
	}
}