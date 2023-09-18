export function sanitize(str) {
	return str
		.replaceAll("@everyone", "[全体メンション]")
		.replaceAll("@here", "[全体メンション]")
		.replaceAll(/<@&[^>]*>/g, "[ロールメンション]");
}
