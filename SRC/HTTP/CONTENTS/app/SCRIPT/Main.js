let GUILD_LIST_EL = document.getElementById("GUILD_LIST");

window.addEventListener("load", async (E) => {
	const GUILD_LIST = await GUILD_LIST_GET();
	if (GUILD_LIST.STATUS) {
		for (let I = 0; I < GUILD_LIST.GUILDS.length; I++) {
			const GUILD = GUILD_LIST.GUILDS[I];

			//鯖のアイコンのエレメントを作る
			let BUTTON = document.createElement("button");
			BUTTON.onclick = function(){OPEN_GUILD(GUILD.ID);};

			//アイコンがあるか
			if(GUILD.ICON_URL){
				//ある
				BUTTON.innerHTML = "<IMG SRC=\"" + GUILD.ICON_URL + "\">";
			}else{
				//ない
				BUTTON.innerHTML = GUILD.NAME.slice(0, 3);
			}

			GUILD_LIST_EL.appendChild(BUTTON);
		}
	}
});

function OPEN_GUILD(ID){
	alert(ID);
}

async function GUILD_LIST_GET() {
	const AJAX = await fetch("/API/GUILD_LIST_GET");
	if (AJAX.ok) {
		return AJAX.json();
	} else {
		return false;
	}
}
