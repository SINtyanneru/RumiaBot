// @ts-check
import { client } from "../MODULES/loadClient.js";
import { MessageEmbed } from "discord.js";
import { NULLCHECK } from "../MODULES/NULLCHECK.js";
import { RND_COLOR } from "../MODULES/RND_COLOR.js";

//鯖情報取得
/**
 *
 * @param {import("discord.js").CommandInteraction<import("discord.js").CacheType>} interaction
 */
export async function getServerInfo(interaction) {
	try {
		const GLID = client.guilds.cache.get(interaction.guildId);

		const embed = new MessageEmbed();
		embed.setTitle(GLID.name);
		if (GLID.description) {
			embed.setDescription(GLID.description);
		}
		embed.setColor(RND_COLOR());

		embed.setThumbnail(`https://cdn.discordapp.com/icons/${GLID.id}/${GLID.icon}.png`);

		//鯖のID
		embed.addFields({
			name: "ID",
			value: NULLCHECK(GLID.id),
			inline: false
		});

		//認証レベル
		embed.addFields({
			name: "認証レベル",
			value: NULLCHECK(GLID.verificationLevel),
			inline: false
		});

		//鯖のオーナー
		const OWNER = await GLID.fetchOwner();
		embed.addFields({
			name: "所有者",
			value: "<@" + OWNER.id + ">",
			inline: false
		});

		//AFK
		if (GLID.afkChannel) {
			embed.addFields({
				name: "AFKチャンネル",
				value: "<#" + GLID.afkChannel.id.toString() + ">",
				inline: false
			});
		}

		//作成日
		if (GLID.createdAt) {
			const DATE = GLID.createdAt;
			embed.addFields({
				name: "鯖作成日",
				value: DATE.getFullYear() + "年" + DATE.getMonth() + "月" + DATE.getDate() + "日" + DATE.getDay() + "曜日" + DATE.getHours() + "時" + DATE.getMinutes() + "分" + DATE.getSeconds() + "秒" + DATE.getMilliseconds() + "ミリ秒",
				inline: false
			});
		}

		//絵文字を配列にするやつ
		const fetched_emoji = await GLID.emojis.fetch();
		/**@type {string[]} */
		const processed_emojis = [];
		let i = 0;
		// 全ての絵文字を列挙したら終わる
		while (fetched_emoji.size < i) {
			// ループの初めの旅にtmpは初期化
			let tmp = "";
			// 全ての絵文字を列挙したら終わる
			while (fetched_emoji.size < i) {
				const emoji = fetched_emoji[i];
				/**@type {string} */
				let text;
				if (emoji.animated) {
					text = `<a:${emoji.name}:${emoji.id}>`;
				} else {
					text = `<:${emoji.name}:${emoji.id}>`;
				}
				// 上限を超えそうなら
				if ((tmp + text).length > 1000) {
					// 配列に追加
					processed_emojis.push(tmp);
					break;
				}
				// tmpにまだ増やせるから増やす
				tmp += text;
				i++;
			}
		}

		console.log(processed_emojis);
		processed_emojis.forEach((row, i) => {
			embed.addFields({
				name: "絵文字リスト: " + (i + 1) + "番目",
				value: row,
				inline: false
			});
		});

		await interaction.editReply({ embeds: [embed] });
	} catch (error) {
		console.log("[ ERR ][ INFO ]" + error);
		await interaction.editReply("エラー\n" + error);
	}
}

/**
 * ユーザーの情報を取得する(インタラクションを使って)
 * @param {import("discord.js").CommandInteraction<import("discord.js").CacheType>} interaction
 */
export async function getUserInfo(interaction) {
	/** @type {import("discord.js").GuildMember}*/
	// @ts-ignore
	const MEMBER = interaction.options.getMentionable("user");
	const USER = client.users.cache.get(MEMBER.user.id);

	//埋め込み
	const embed = new MessageEmbed();

	//ユーザー名
	if (MEMBER.nickname !== undefined && MEMBER.nickname !== null) {
		embed.setTitle(NULLCHECK(MEMBER.nickname));
	} else {
		embed.setTitle(NULLCHECK(USER.username));
	}

	embed.setDescription(NULLCHECK(USER.username));
	embed.setColor(RND_COLOR());

	embed.setThumbnail(`https://cdn.discordapp.com/avatars/${USER.id}/${USER.avatar}.png`);

	embed.addFields({
		name: "ID",
		value: NULLCHECK(USER.id),
		inline: false
	});

	//アカウント作成日
	embed.addFields({
		name: "アカウント作成日",
		value: toDateFormatted(USER.createdAt),
		inline: false
	});

	//鯖に参加した日付
	embed.addFields({
		name: "鯖に参加した日付",
		value: toDateFormatted(MEMBER.joinedAt),
		inline: false
	});

	//ニトロブースト開始日
	if (MEMBER.premiumSince !== null) {
		embed.addFields({
			name: "ブースト開始日",
			//日本表記
			value: toDateFormatted(MEMBER.joinedAt),
			inline: false
		});
	}

	//BOTか
	embed.addFields({
		name: "BOTか",
		value: USER.bot ? "はい" : "いいえ",
		inline: false
	});

	//キック可能か
	embed.addFields({
		name: "わたしはこのユーザーを",
		value: `追放でき${MEMBER.kickable ? "ます" : "ません"}`,
		inline: false
	});

	//BAN可能か
	embed.addFields({
		name: "わたしはこのユーザーを",
		value: `BANでき${MEMBER.bannable ? "ます" : "ません"}`,
		inline: false
	});

	await interaction.editReply({ embeds: [embed] });
}
/**
 * minecraftユーザーの情報を取得する(インタラクションを使って)
 * @param {import("discord.js").CommandInteraction<import("discord.js").CacheType>} interaction
 */
export async function getMcInfo(interaction) {
	try {
		const MCID = interaction.options.getString("mcid");

		const RES_GET_UUID = await fetch("https://api.mojang.com/users/profiles/minecraft/" + MCID, {
			method: "GET",
			headers: {
				"Content-Type": "application/json"
			}
		});

		if (RES_GET_UUID.ok) {
			const RESULT_GET_UUID = await RES_GET_UUID.json();

			const RES_GET_BASE64 = await fetch("https://sessionserver.mojang.com/session/minecraft/profile/" + RESULT_GET_UUID.id, {
				method: "GET",
				headers: {
					"Content-Type": "application/json"
				}
			});

			if (RES_GET_BASE64.ok) {
				const RESULT_GET_BASE64 = await RES_GET_BASE64.json();
				const RESULT_JSON = JSON.parse(atob(RESULT_GET_BASE64.properties[0].value));

				//埋め込み
				const embed = new MessageEmbed();

				//ユーザー名とスキン
				embed.setTitle("マイクラユーザーの情報");
				embed.setDescription(RESULT_GET_BASE64.name);
				embed.setColor(RND_COLOR());
				embed.setThumbnail(RESULT_JSON.textures.SKIN.url);

				//UUID
				embed.addFields({
					name: "UUID",
					value: RESULT_GET_BASE64.id,
					inline: false
				});

				//プロフィールID
				embed.addFields({
					name: "PFID",
					value: RESULT_JSON.profileId,
					inline: false
				});

				//結果を出力
				await interaction.editReply({ embeds: [embed] });
			} else {
				await interaction.editReply("MojangAPIに拒否られた｡ﾟ･（>Д<）･ﾟ｡");
			}
		} else {
			//エラーを返されたので
			switch (RES_GET_UUID.status) {
				case 404:
					await interaction.editReply("そんなユーザー居ないらしいよ");
					return;

				default:
					await interaction.editReply("MojangAPIに拒否られた｡ﾟ･（>Д<）･ﾟ｡");
					return;
			}
		}
	} catch (EX) {
		console.error("[ ERR ][ MINE ]" + EX);
		await interaction.editReply("エラー");
	}
}

/**
 *
 * @param {Date} date
 * @param {Date} NOW_DATE
 * @returns {string}
 */
function toDateFormatted(date, NOW_DATE = new Date()) {
	const DAY_FORMAT = ["日", "月", "火", "水", "木", "金", "土"];
	return (
		date.getFullYear().toString() +
		"年 " +
		(date.getMonth() + 1).toString() +
		"月 " +
		date.getDate().toString() +
		"日 " +
		DAY_FORMAT[date.getDay()] +
		"曜日 " +
		date.getHours().toString() +
		"時 " +
		date.getMinutes().toString() +
		"分 " +
		date.getSeconds().toString() +
		"秒 " +
		date.getMilliseconds().toString() +
		"ミリ秒\n" +
		//アメリカ表記
		Math.floor((NOW_DATE.valueOf() - date.valueOf()) / (1000 * 60 * 60 * 24)).toString() +
		"日前"
	);
}
