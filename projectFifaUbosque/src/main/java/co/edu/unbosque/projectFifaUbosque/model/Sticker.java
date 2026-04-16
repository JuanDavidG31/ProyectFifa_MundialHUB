package co.edu.unbosque.projectFifaUbosque.model;

import jakarta.persistence.*;

@Entity
@Table(name = "stickers")
public class Sticker {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String code; 

	@Column(nullable = false)
	private String title; 

	@Column(nullable = false)
	private String sectionId; 

	@Column(nullable = false)
	private String pageTitle; 

	private String imageUrl; 
	@Column(nullable = false)
	private String rarity = "Común";
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StickerType type = StickerType.CURRENT_PLAYER;

	@Column(name = "exchange_value", nullable = false)
	private int exchangeValue = 10; 

	public Sticker() {
	}

	public Sticker(String code, String title, String sectionId, String pageTitle, String imageUrl, String rarity) {
		this.code = code;
		this.title = title;
		this.sectionId = sectionId;
		this.pageTitle = pageTitle;
		this.imageUrl = imageUrl;
		this.rarity = rarity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public StickerType getType() {
		return type;
	}

	public void setType(StickerType type) {
		this.type = type;
	}

	public int getExchangeValue() {
		return exchangeValue;
	}

	public void setExchangeValue(int exchangeValue) {
		this.exchangeValue = exchangeValue;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getRarity() {
		return rarity;
	}

	public void setRarity(String rarity) {
		this.rarity = rarity;
	}
}