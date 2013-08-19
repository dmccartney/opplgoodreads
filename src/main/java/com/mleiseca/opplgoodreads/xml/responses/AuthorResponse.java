package com.mleiseca.opplgoodreads.xml.responses;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mleiseca.opplgoodreads.xml.objects.Author;

@Root(name = "GoodreadsResponse", strict = false)
public class AuthorResponse {

	@Element
	private Author author;

	public Author getAuthor() {
		return author;
	}

}