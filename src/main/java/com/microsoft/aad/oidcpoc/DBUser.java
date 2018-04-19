package com.microsoft.aad.oidcpoc;

import java.io.Serializable;

public class DBUser  implements Serializable {
	private static final long serialVersionUID = -7136067107910290726L;
	public int UserId;
	public String Email;
	public String UniqueId;
	public String FName;
	public String LName;
}
