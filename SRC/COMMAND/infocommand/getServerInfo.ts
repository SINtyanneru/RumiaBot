// @ts-check

import { type CacheType, CommandInteraction, MessageEmbed } from "discord.js";
import { NULLCHECK } from "../../MODULES/NULLCHECK.js";
import { RND_COLOR } from "../../MODULES/RND_COLOR.js";
import { SlashCommandBuilder } from "@discordjs/builders";

//鯖情報取得

export const command = new SlashCommandBuilder().setName("info_server").setDescription("鯖の情報を取得");
export async function getServerInfo(interaction: CommandInteraction<CacheType>) {
	try {
		const guild = interaction.guild;
		if (!guild) return;

		const embed = new MessageEmbed();
		embed.setTitle(guild.name);
		if (guild.description) {
			embed.setDescription(guild.description);
		}
		embed.setColor(RND_COLOR());

		embed.setThumbnail(`https://cdn.discordapp.com/icons/${guild.id}/${guild.icon}.png`);

		//鯖のID
		embed.addFields({
			name: "ID",
			value: NULLCHECK(guild.id),
			inline: false
		});

		//認証レベル
		embed.addFields({
			name: "認証レベル",
			value: NULLCHECK(guild.verificationLevel),
			inline: false
		});

		//鯖のオーナー
		const OWNER = await guild.fetchOwner();
		embed.addFields({
			name: "所有者",
			value: "<@" + OWNER.id + ">",
			inline: false
		});

		//AFK
		if (guild.afkChannel) {
			embed.addFields({
				name: "AFKチャンネル",
				value: "<#" + guild.afkChannel.id.toString() + ">",
				inline: false
			});
		}

		//作成日
		if (guild.createdAt) {
			const DATE = guild.createdAt;
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
				inline: false
			});
		}

		//絵文字を配列にするやつ
		const fetched_emoji = await guild.emojis.fetch();
		const processed_emojis: string[] = [];
		// 全ての絵文字を列挙したら終わる
		let tmp = "";
		for (const [, emoji] of fetched_emoji) {
			// 全ての絵文字を列挙したら終わる
			let text: string;
			if (emoji.animated) {
				text = `<a:${emoji.name}:${emoji.id}>`;
			} else {
				text = `<:${emoji.name}:${emoji.id}>`;
			}
			// 上限を超えそうなら
			if ((tmp + text).length > 1000) {
				// 配列に追加
				processed_emojis.push(tmp);
				tmp = text;
				continue;
			}
			// tmpにまだ増やせるから増やす
			tmp += text;
		}

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
