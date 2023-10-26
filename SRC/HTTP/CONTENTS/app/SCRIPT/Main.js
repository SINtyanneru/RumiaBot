// @ts-nocheck
const GUILD_LISTVIEW = document.getElementById("GUILD_LIST");
const GUILD_INFO = document.getElementById("GUILD_INFO");
const CHANNEL_LIST = document.getElementById("CHANNEL_LIST");
const CHANNEL_INFO = document.getElementById("CHANNEL_INFO");
const MESSAGE_LIST = document.getElementById("MESSAGE_LIST");
const MESSAGE_SEND_FORM_TEXT = document.getElementById("MESSAGE_SEND_FORM_TEXT");
// @ts-ignore
const selectedGuild = document.forms.namedItem("guild-select-form").elements.namedItem("guild-select")


// TSCを黙らせるコーナー
if (GUILD_LISTVIEW === null) throw "何かがおかしい";
if (GUILD_INFO === null) throw "何かがおかしい";
if (CHANNEL_LIST === null) throw "何かがおかしい";
if (CHANNEL_INFO === null) throw "何かがおかしい";
if (MESSAGE_LIST === null) throw "何かがおかしい";
if (MESSAGE_SEND_FORM_TEXT === null) throw "何かがおかしい";
if (selectedGuild === null) throw "何かがおかしい";

MESSAGE_SEND_FORM_TEXT.addEventListener("keydown", (event) => {
	MSG_SEND(event)
})

// 作りかけ
class DiscordBotApp {
	constructor() {
		this.selected_channel_id = "";
		this.selected_guild_id = "";
	}
}

let SELECT_CHANNEL_ID = "";
let SELECT_GUILD_ID = "";

/**
 * @typedef channelInfo
 * @prop {true} STATUS
 * @prop {Object} CHANNEL
 * @prop {string} CHANNEL.ID
 * @prop {string} CHANNEL.NAME
 * @prop {never[]} CHANNEL.MESSAGES

 * @typedef channelList
 * @prop {true} STATUS
 * @prop {import("../../../HTTP_SERVER.js").CHANNEL[]} CHANNELS

 * @typedef guildList
 * @prop {API_RES_GUILD[]} GUILDS
 * @prop {true} STATUS

 * @typedef guildInfo
 * @prop {true} STATUS
 * @prop {Object} GUILD
 * @prop {string} GUILD.ID
 * @prop {string} GUILD.NAME

 * @typedef API_RES_GUILD
 * @prop {string} ID
 * @prop {string | null} ICON_URL
 * @prop {string} NAME
 */

const GUILD_LIST = await GUILD_LIST_GET();
if (GUILD_LIST === false) throw "なんかおかしい";
GUILD_LIST.GUILDS.forEach(guild => {
	const radio = createElement("input", null, {
		type: "radio",
		value: guild.ID,
		name: "guild-select"
	})
	radio.dataset.guildid = guild.ID;
	const label = document.createElement("label");
	radio.addEventListener("click", () => OPEN_GUILD(guild.ID))
	if (guild.ICON_URL) {
		// アイコンは存在
		label.append(createElement("img", null, {
			src: guild.ICON_URL
		}))
	} else {
	}
	label.append(radio)
	GUILD_LISTVIEW.append(label)
}) /*
	for (let I = 0; I < GUILD_LIST.GUILDS.length; I++) {
		const GUILD = GUILD_LIST.GUILDS[I];

		//鯖のアイコンのエレメントを作る
		let BUTTON = document.createElement("button");
		BUTTON.onclick = () => OPEN_GUILD(GUILD.ID);

		//アイコンがあるか
		if (GUILD.ICON_URL) {
			//ある
			BUTTON.innerHTML = "<IMG SRC=\"" + GUILD.ICON_URL + "\">";
		} else {
			//ない
			BUTTON.innerHTML = GUILD.NAME.slice(0, 3);
		}

		GUILD_LIST.appendChild(BUTTON);
	}
	*/

/**
 * @param {string} ID
 */
async function OPEN_GUILD(ID) {
	const GUILD_AJAX = await fetch("/API/GUILD_INFO_GET?ID=" + ID);
	if (GUILD_AJAX.ok) {
		/**@type {ApiResponse<guildInfo>} */
		const RESULT = await GUILD_AJAX.json();
		if (RESULT.STATUS) {
			GUILD_INFO.innerText = RESULT.GUILD.NAME;
			SELECT_GUILD_ID = RESULT.GUILD.ID;
		}
	}

	const CHANNEL_AJAX = await fetch("/API/CHANNEL_LIST_GET?ID=" + ID);
	if (CHANNEL_AJAX.ok) {
		/**@type {ApiResponse<channelList>} */
		const RESULT = await CHANNEL_AJAX.json();
		if (RESULT.STATUS) {
			//チャンネル一覧をリセット
			CHANNEL_LIST.innerHTML = "";

			/*
			 * カテゴリチャンネル
			 */
			for (let I = 0; I < RESULT.CHANNELS.length; I++) {
				const CHANNEL = RESULT.CHANNELS[I];
				if (CHANNEL.TYPE === "GUILD_CATEGORY") {
					//カテゴリチャンネルのエレメント
					let CATEGORY_CHANNEL = document.createElement("DIV");
					CATEGORY_CHANNEL.id = "__GUILD_CHANNEL_" + CHANNEL.ID;
					CATEGORY_CHANNEL.className = "GUILD_CATEGORY_CHANNEL";
					CATEGORY_CHANNEL.innerText = "┌[" + CHANNEL.NAME + "]";

					CHANNEL_LIST.appendChild(CATEGORY_CHANNEL);
				}
			}

			/*
			 * テキストチャンネル
			 */
			for (let I = 0; I < RESULT.CHANNELS.length; I++) {
				const CHANNEL = RESULT.CHANNELS[I];
				if (CHANNEL.TYPE === "GUILD_TEXT") {
					if (CHANNEL.PARENT) {
						let PARENT = document.getElementById("__GUILD_CHANNEL_" + CHANNEL.PARENT.id);
						if (!PARENT) return;

						const CHANNEL_Elm = createElement("div", "├# [" + CHANNEL.NAME + "]", {
							id: "__GUILD_CHANNEL_" + CHANNEL.ID,
							className: "GUILD_TEXT_CHANNEL",
							async onclick() {
								const AJAX = await fetch("/API/CHANNEL_INFO_GET?GID=" + ID + "&CID=" + CHANNEL.ID);
								if (AJAX.ok) {
									/**@type {ApiResponse<channelInfo>} */
									const RESULT = await AJAX.json();
									if (RESULT.STATUS) {
										console.log(RESULT);
										CHANNEL_SELECT(RESULT);
									}
								}
							}
						})


						PARENT.appendChild(CHANNEL_Elm);
					} else {
						const CHANNEL_Elm = createElement("div", "├# [" + CHANNEL.NAME + "]", {
							id: "__GUILD_CHANNEL_" + CHANNEL.ID,
							className: "GUILD_TEXT_CHANNEL",
							async onclick() {
								const AJAX = await fetch("/API/CHANNEL_INFO_GET?GID=" + ID + "&CID=" + CHANNEL.ID);
								if (AJAX.ok) {
									/** @type {ApiResponse<channelInfo>} */
									const RESULT = await AJAX.json();
									if (RESULT.STATUS) {
										console.log(RESULT);
										CHANNEL_SELECT(RESULT);
									}
								}
							}
						})

						CHANNEL_LIST.appendChild(CHANNEL_Elm);
					}
				}
			}
		}
	} else {
		alert("エラーだよ");
	}
}
/**
 * @returns {Promise<guildList| false>}
 */
async function GUILD_LIST_GET() {
	const AJAX = await fetch("/API/GUILD_LIST_GET");
	if (AJAX.ok) {
		return await AJAX.json();
	} else {
		return false;
	}
}
/**
 * 
 * @param {channelInfo} DATA
 */
function CHANNEL_SELECT(DATA) {
	//見ているチャンネルを設定
	SELECT_CHANNEL_ID = DATA.CHANNEL.ID;

	CHANNEL_INFO.innerText = DATA.CHANNEL.NAME;

	MESSAGE_LIST.innerHTML = "";
}

/**
 * @param {KeyboardEvent} event
 */
async function MSG_SEND(event) {
	if (event.key === "Enter" && event.ctrlKey) {
		if (MESSAGE_SEND_FORM_TEXT.value !== "") {
			const AJAX = await fetch("/API/MSG_SEND", {
				body: JSON.stringify({
					GID: SELECT_GUILD_ID,
					CID: SELECT_CHANNEL_ID,
					TEXT: MESSAGE_SEND_FORM_TEXT.value
				}),
				method: "POST"
			});

			if (AJAX.ok) {
				const RESULT = await AJAX.json();
				if (RESULT.STATUS) {
					//成功
					MESSAGE_SEND_FORM_TEXT.value = "";
				}
			}
		}
	}
}

/**
 * @description API叩くやつ
 */



/**
 * @param {string} name
 * @param {HTMLElement | string | null} child
 * @param {Object.<string,any>} attribute
 * @returns {HTMLElement}
 * */
function createElement(name, child = null, attribute = {}) {
	if (name == undefined)
		throw new TypeError("nameは必ず指定する必要があります");
	const element = document.createElement(name);
	for (const [key, value] of Object.entries(attribute)) {
		// @ts-ignore
		element[key] = value;
	}
	if (child != null) {
		if (Array.isArray(child)) {
			child.map((childlen) => {
				element.append(childlen);
			});
		} else {
			element.append(child);
		}
	}
	return element;
}


// from WS.js
const WS = new WebSocket("ws://localhost:3001/");
WS.addEventListener("open", (E) => {
	console.log("WebSocketに接続しました");
});

WS.addEventListener("message", (event) => {
	const MESSAGE = JSON.parse(event.data);
	console.log(MESSAGE);

	console.log(SELECT_CHANNEL_ID);
	console.log(MESSAGE.CHANNEL.ID);

	//メッセージ受信
	if (SELECT_CHANNEL_ID === MESSAGE.CHANNEL.ID) {
		let MESSAGE_EL = document.createElement("DIV");
		MESSAGE_EL.className = "MESSAGE_ITEM";

		let AUTHOR_EL = document.createElement("DIV");
		AUTHOR_EL.className = "MESSAGE_AUTHOR";
		AUTHOR_EL.innerHTML = "<IMG SRC=\"" + MESSAGE.AUTHOR.ICON + "\">" + MESSAGE.AUTHOR.NAME;

		let TEXT_EL = document.createElement("DIV");
		TEXT_EL.className = "MESSAGE_TEXT";
		TEXT_EL.innerText = MESSAGE.MSG.TEXT;


		MESSAGE_EL.appendChild(AUTHOR_EL);
		MESSAGE_EL.appendChild(TEXT_EL);

		//TODO:画像表示を付ける

		MESSAGE_LIST.appendChild(MESSAGE_EL);

		MESSAGE_LIST.scrollTo(0, MESSAGE_LIST.scrollHeight);
	}
});
// tscを黙らせる
export { }