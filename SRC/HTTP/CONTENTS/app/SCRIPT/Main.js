let GUILD_LIST_EL = document.getElementById("GUILD_LIST");

window.addEventListener("load", E => {
	const GUILD_LIST = GUILD_LIST_GET();
});

async function GUILD_LIST_GET() {
	const AJAX = await fetch("/API/GUILD_LIST_GET");
	if (AJAX.ok) {
		return AJAX.json();
	} else {
		return false;
	}
}
