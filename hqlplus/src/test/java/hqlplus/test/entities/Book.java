package hqlplus.test.entities;

import javax.persistence.Entity;

@Entity
public class Book {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
