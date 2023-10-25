let GUILD_LIST_EL = document.getElementById("GUILD_LIST");
let GUILD_INFO_EL = document.getElementById("GUILD_INFO");
let CHANNEL_LIST_EL = document.getElementById("CHANNEL_LIST");
let CHANNEL_INFO_EL = document.getElementById("CHANNEL_INFO");
let MESSAGE_LIST_EL = document.getElementById("MESSAGE_LIST");

let SELECT_CHANNEL_ID = "";

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

async function OPEN_GUILD(ID){
	const GUILD_AJAX = await fetch("/API/GUILD_INFO_GET?ID=" + ID);
	if (GUILD_AJAX.ok) {
		const RESULT = await GUILD_AJAX.json();
		if(RESULT.STATUS){
			GUILD_INFO_EL.innerText = RESULT.GUILD.NAME;
		}
	}

	const CHANNEL_AJAX = await fetch("/API/CHANNEL_LIST_GET?ID=" + ID);
	if (CHANNEL_AJAX.ok) {
		const RESULT = await CHANNEL_AJAX.json();
		if(RESULT.STATUS){
			//チャンネル一覧をリセット
			CHANNEL_LIST_EL.innerHTML = "";

			/**
			 * カテゴリチャンネル
			 */
			for (let I = 0; I < RESULT.CHANNELS.length; I++) {
				const CHANNEL = RESULT.CHANNELS[I];
				if(CHANNEL.TYPE === "GUILD_CATEGORY"){
					//カテゴリチャンネルのエレメント
					let CATEGORY_CHANNEL_EL = document.createElement("DIV");
					CATEGORY_CHANNEL_EL.id = "__GUILD_CHANNEL_" + CHANNEL.ID;
					CATEGORY_CHANNEL_EL.className = "GUILD_CATEGORY_CHANNEL";
					CATEGORY_CHANNEL_EL.innerText = "┌[" + CHANNEL.NAME + "]";

					CHANNEL_LIST_EL.appendChild(CATEGORY_CHANNEL_EL);
				}
			}

			/**
			 * テキストチャンネル
			 */
			for (let I = 0; I < RESULT.CHANNELS.length; I++) {
				const CHANNEL = RESULT.CHANNELS[I];
				if(CHANNEL.TYPE === "GUILD_TEXT"){
					if(CHANNEL.FORALDER){
						let PARENT = document.getElementById("__GUILD_CHANNEL_" + CHANNEL.FORALDER.id);
						if(PARENT){
							let CHANNEL_EL = document.createElement("DIV");
							CHANNEL_EL.id = "__GUILD_CHANNEL_" + CHANNEL.ID;
							CHANNEL_EL.className = "GUILD_TEXT_CHANNEL";
							CHANNEL_EL.innerHTML = "├# [" + CHANNEL.NAME + "]";
							CHANNEL_EL.onclick = async function(){
								const AJAX = await fetch("/API/CHANNEL_INFO_GET?GID=" + ID + "&CID=" + CHANNEL.ID);
								if (AJAX.ok) {
									const RESULT = await AJAX.json();
									if(RESULT.STATUS){
										console.log(RESULT);
										CHANNEL_SELECT(RESULT);
									}
								}
							};
	
							PARENT.appendChild(CHANNEL_EL);
						}
					}else{
						let CHANNEL_EL = document.createElement("DIV");
						CHANNEL_EL.id = "__GUILD_CHANNEL_" + CHANNEL.ID;
						CHANNEL_EL.className = "GUILD_TEXT_CHANNEL";
						CHANNEL_EL.innerHTML = "├# [" + CHANNEL.NAME + "]";
						CHANNEL_EL.onclick = async function(){
							const AJAX = await fetch("/API/CHANNEL_INFO_GET?GID=" + ID + "&CID=" + CHANNEL.ID);
							if (AJAX.ok) {
								const RESULT = await AJAX.json();
								if(RESULT.STATUS){
									console.log(RESULT);
									CHANNEL_SELECT(RESULT);
								}
							}
						};
	
						CHANNEL_LIST_EL.appendChild(CHANNEL_EL);
					}
				}
			}
		}
	} else {
		alert("エラーだよ");
	}
}

async function GUILD_LIST_GET() {
	const AJAX = await fetch("/API/GUILD_LIST_GET");
	if (AJAX.ok) {
		return await AJAX.json();
	} else {
		return false;
	}
}

function CHANNEL_SELECT(DATA){
	//見ているチャンネルを設定
	SELECT_CHANNEL_ID = DATA.CHANNEL.ID;

	CHANNEL_INFO_EL.innerText = DATA.CHANNEL.NAME;

	MESSAGE_LIST_EL.innerHTML = "";
}