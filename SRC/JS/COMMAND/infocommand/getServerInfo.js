// @ts-check

import { client } from "../../MODULES/loadClient.js";
import { MessageEmbed } from "discord.js";
import { NULLCHECK } from "../../MODULES/NULLCHECK.js";
import { RND_COLOR } from "../../MODULES/RND_COLOR.js";
import { SlashCommandBuilder } from "@discordjs/builders";
import { MAP_KILLER } from "../../MODULES/MAP_KILLER.js";

//鯖情報取得

export const command = new SlashCommandBuilder().setName("info_server").setDescription("鯖の情報を取得");
/**
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
			inline: true
		});

		//認証レベル
		embed.addFields({
			name: "認証レベル",
			value: NULLCHECK(GLID.verificationLevel),
			inline: true
		});

		//鯖のオーナー
		const OWNER = await GLID.fetchOwner();
		embed.addFields({
			name: "所有者",
			value: "<@" + OWNER.id + ">",
			inline: true
		});

		//AFK
		if (GLID.afkChannel) {
			embed.addFields({
				name: "AFKチャンネル",
				value: "<#" + GLID.afkChannel.id.toString() + ">",
				inline: true
			});
		}

		//作成日
		if (GLID.createdAt) {
			const DATE = GLID.createdAt;
			embed.addFields({
				name: "鯖作成日",
				value:
					DATE.getFullYear() +
					"年" +
					DATE.getMonth() +
					"月" +
					DATE.getDate() +
					"日" +
					DATE.getDay() +
					"曜日" +
					DATE.getHours() +
					"時" +
					DATE.getMinutes() +
					"分" +
					DATE.getSeconds() +
					"秒" +
					DATE.getMilliseconds() +
					"ミリ秒",
				inline: true
			});
		}

		//JSがクソすぎて、addFieldsを使おうと「nameなんても物は型XXXXにありません」とかほざきやがるのでこうなった、死ね
		embed.addField(
			"BANされたユーザー",
			Object.keys(MAP_KILLER(await GLID.bans.fetch())).length.toString() + "人",
			true
		);

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

		processed_emojis.forEach((row, i) => {
			embed.addFields({
				name: "絵文字リスト: " + (i + 1) + "番目",
				value: row,
				inline: false
			});
		});

		await interaction.editReply({ embeds: [embed] });
	} catch (EX) {
		console.log("[ ERR ][ INFO ]");
		console.log(EX);
		await interaction.editReply("エラー\n" + EX);
	}
}
