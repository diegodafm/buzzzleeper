package br.com.dafm.android.buzzzleeper.entity;

public class BlrAddress {
	private Long id;

	private String name;

	private String address;

	private Double lat;

	private Double lng;

	private String ringtone;

	private Boolean status;
	
	private Integer buffer;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public String getRingtone() {
		return ringtone;
	}

	public void setRingtone(String ringtone) {
		this.ringtone = ringtone;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Integer getBuffer() {
		return buffer;
	}

	public void setBuffer(Integer buffer) {
		this.buffer = buffer;
	}
}
