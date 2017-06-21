package it.polito.tdp.formulaone.model;

public class Constructor {

	private int constructorId;
	private String name;

	public Constructor(int constructorId, String name) {
		this.constructorId = constructorId;
		this.name = name;
	}

	public int getConstructorId() {
		return constructorId;
	}

	public void setConstructorId(int constructorId) {
		this.constructorId = constructorId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}
}
