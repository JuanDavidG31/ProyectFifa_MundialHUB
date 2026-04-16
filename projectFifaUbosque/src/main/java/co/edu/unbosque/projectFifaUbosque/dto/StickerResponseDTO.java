package co.edu.unbosque.projectFifaUbosque.dto;

public class StickerResponseDTO {
	private String code;
	private String title;
	private String sectionId;
	private String pageTitle;
	private String imageUrl;
	private boolean owned;
	private int duplicates;
	private String rarity;

	public StickerResponseDTO(String code, String title, String sectionId, String pageTitle, String imageUrl,
			boolean owned, int duplicates, String rarity) {
		super();
		this.code = code;
		this.title = title;
		this.sectionId = sectionId;
		this.pageTitle = pageTitle;
		this.imageUrl = imageUrl;
		this.owned = owned;
		this.duplicates = duplicates;
		this.rarity = rarity;
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

	public boolean isOwned() {
		return owned;
	}

	public void setOwned(boolean owned) {
		this.owned = owned;
	}

	public int getDuplicates() {
		return duplicates;
	}

	public void setDuplicates(int duplicates) {
		this.duplicates = duplicates;
	}

	public String getRarity() {
		return rarity;
	}

	public void setRarity(String rarity) {
		this.rarity = rarity;
	}

}