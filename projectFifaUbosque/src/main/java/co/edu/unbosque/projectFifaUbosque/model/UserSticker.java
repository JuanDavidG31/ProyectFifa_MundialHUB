package co.edu.unbosque.projectFifaUbosque.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidad de asociación intermedia que modela el inventario de láminas
 * coleccionadas por cada usuario.
 * <p>
 * Relaciona de manera n-m a un usuario con los cromos del catálogo maestro,
 * controlando la cantidad física total de copias poseídas por la cuenta.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@Entity
@Table(name = "user_stickers")
public class UserSticker {

	/** Llave primaria autoincremental de la relación de inventario. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Usuario propietario de las láminas registradas. */
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	/** Lámina o cromo del catálogo maestro asociado al inventario del cliente. */
	@ManyToOne
	@JoinColumn(name = "sticker_id", nullable = false)
	private Sticker sticker;

	/**
	 * Cantidad neta total de copias de este cromo específico poseídas por el
	 * usuario.
	 */
	@Column(nullable = false)
	private int quantity;

	/**
	 * Constructor por defecto para cumplir con los estándares operacionales de JPA
	 * e Hibernate.
	 */
	public UserSticker() {
	}

	/**
	 * Constructor completo parametrizado para la asignación y mutación de cromos a
	 * usuarios.
	 *
	 * @param user     Usuario coleccionista.
	 * @param sticker  Cromo del catálogo.
	 * @param quantity Cantidad física asignada.
	 */
	public UserSticker(User user, Sticker sticker, int quantity) {
		this.user = user;
		this.sticker = sticker;
		this.quantity = quantity;
	}

	/** @return El ID de la relación. */
	public Long getId() {
		return id;
	}

	/** @param id El nuevo ID de persistencia a establecer. */
	public void setId(Long id) {
		this.id = id;
	}

	/** @return El usuario propietario. */
	public User getUser() {
		return user;
	}

	/** @param user El nuevo usuario propietario a configurar. */
	public void setUser(User user) {
		this.user = user;
	}

	/** @return El cromo asociado. */
	public Sticker getSticker() {
		return sticker;
	}

	/** @param sticker El nuevo cromo de catálogo a asociar. */
	public void setSticker(Sticker sticker) {
		this.sticker = sticker;
	}

	/** @return La cantidad de unidades poseídas. */
	public int getQuantity() {
		return quantity;
	}

	/** @param quantity La nueva cantidad de unidades a registrar. */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}