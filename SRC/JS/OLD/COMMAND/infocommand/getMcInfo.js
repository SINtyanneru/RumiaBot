// @ts-check
import { MessageEmbed } from "discord.js";
import { RND_COLOR } from "../../../MODULES/RND_COLOR.js";
import { SlashCommandBuilder } from "@discordjs/builders";

export const command = new SlashCommandBuilder()
	.setName("info_mine")
	.setDescription("マイクラのユーザーの情報を盗みます")
	.addStringOption(o => o.setName("mcid").setDescription("マイクラのID").setRequired(true));

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

			const RES_GET_BASE64 = await fetch(
				"https://sessionserver.mojang.com/session/minecraft/profile/" + RESULT_GET_UUID.id,
				{
					method: "GET",
					headers: {
						"Content-Type": "application/json"
					}
				}
			);

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
