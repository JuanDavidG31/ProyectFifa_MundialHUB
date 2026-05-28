package co.edu.unbosque.projectFifaUbosque.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Entidad de persistencia que registra los movimientos financieros e inventario
 * de un usuario.
 * <p>
 * Almacena el historial atómico de compras de paquetes, recargas de monedas o
 * reembolsos por láminas duplicadas.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@Data
@Entity
@Table(name = "notices") // Nota: Mapeado en base a anotaciones relacionales del código.
public class Transaction {

	/** Llave primaria autoincremental de la transacción en base de datos. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Usuario propietario y responsable de la transacción realizada. */
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	/**
	 * Categoría o tipo de movimiento realizado (ej: RECHARGE, BUY_PACK,
	 * REIMBURSEMENT).
	 */
	@Column(nullable = false)
	private String transactionType;

	/** Monto o cantidad de valor involucrado en el movimiento financiero. */
	private int amount;

	/** Descripción detallada o justificación contextual de la transacción. */
	private String description;

	/**
	 * Marca temporal exacta (Fecha y Hora) en la cual se consolidó la operación.
	 */
	@Column(nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	/**
	 * Constructor por defecto para cumplir con los requerimientos relacionales de
	 * JPA.
	 */
	public Transaction() {
	}

	/**
	 * Constructor parametrizado completo para la creación manual de transacciones
	 * operativas.
	 *
	 * @param user            Usuario involucrado.
	 * @param transactionType Tipo de transacción.
	 * @param amount          Monto del movimiento.
	 * @param description     Detalle informativo.
	 */
	public Transaction(User user, String transactionType, int amount, String description) {
		this.user = user;
		this.transactionType = transactionType;
		this.amount = amount;
		this.description = description;
	}

	/** @return El identificador de la transacción. */
	public Long getId() {
		return id;
	}

	/** @param id El nuevo ID de persistencia. */
	public void setId(Long id) {
		this.id = id;
	}

	/** @return El usuario de la operación. */
	public User getUser() {
		return user;
	}

	/** @param user El nuevo usuario a asociar. */
	public void setUser(User user) {
		this.user = user;
	}

	/** @return El tipo de movimiento. */
	public String getTransactionType() {
		return transactionType;
	}

	/** @param transactionType El nuevo tipo de movimiento. */
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	/** @return El valor numérico de la transacción. */
	public int getAmount() {
		return amount;
	}

	/** @param amount El nuevo monto a asignar. */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/** @return La descripción contextual. */
	public String getDescription() {
		return description;
	}

	/** @param description La nueva descripción. */
	public void setDescription(String description) {
		this.description = description;
	}

	/** @return La fecha de creación de la marca temporal. */
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	/** @param createdAt La nueva fecha de creación. */
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}