package su.rumishistem.rumisanbot.base_system.Type;

public class User {
	public final String id;
	public final String uid;
	public final String name;
	public final String host;
	public final String icon_url;

	public User(String id, String uid, String host, String name, String icon_url) {
		this.id = id;
		this.uid = uid;
		this.host = host;
		this.name = name;
		this.icon_url = icon_url;
	}
}
