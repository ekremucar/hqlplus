package hqlplus.test.parameters;

import java.util.Locale;

import hqlplus.HqlParameter;
import hqlplus.annotation.ClazzInfo;
import hqlplus.annotation.FieldInfo;
import hqlplus.enumeration.CompareMethod;
import hqlplus.test.entities.Book;

@ClazzInfo(clazz = Book.class, alias = "book")
public class BookParameter extends HqlParameter {

    public BookParameter(Locale locale) {
		super(locale);
	}

	@FieldInfo(compareMethod = CompareMethod.LIKE)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
