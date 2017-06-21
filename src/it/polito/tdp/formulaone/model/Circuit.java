package it.polito.tdp.formulaone.model;

public class Circuit {
	
	private int circuitId ;
	private String circuitRef ;
	private String name ;
	private String location ;
	private String country ;
	private double lat ;
	private double lng ;
	private int alt ;
	private String url ;
	
	public Circuit (int circuitId, String name) {
		this. circuitId = circuitId;
		this. name = name;
	}
	
	public Circuit(int circuitId, String circuitRef, String name, String location, String country, double lat,
			double lng, int alt, String url) {
		super();
		this.circuitId = circuitId;
		this.circuitRef = circuitRef;
		this.name = name;
		this.location = location;
		this.country = country;
		this.lat = lat;
		this.lng = lng;
		this.alt = alt;
		this.url = url;
	}
	public int getCircuitId() {
		return circuitId;
	}
	public void setCircuitId(int circuitId) {
		this.circuitId = circuitId;
	}
	public String getCircuitRef() {
		return circuitRef;
	}
	public void setCircuitRef(String circuitRef) {
		this.circuitRef = circuitRef;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public int getAlt() {
		return alt;
	}
	public void setAlt(int alt) {
		this.alt = alt;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String toString() {
		return this.name ;
	}
	

}
