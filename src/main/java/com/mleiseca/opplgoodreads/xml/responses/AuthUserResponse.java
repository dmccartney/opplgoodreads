package com.mleiseca.opplgoodreads.xml.responses;

import com.mleiseca.opplgoodreads.xml.objects.AuthUser;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(name = "GoodreadsResponse", strict = false)
public class AuthUserResponse {

	@Element
	private AuthUser user;

	public AuthUser getAuthUser() {
		return user;
	}
}
