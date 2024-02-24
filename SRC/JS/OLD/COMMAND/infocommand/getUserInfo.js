// @ts-check
import { client } from "../../../MODULES/loadClient.js";
import { MessageEmbed } from "discord.js";
import { NULLCHECK } from "../../../MODULES/NULLCHECK.js";
import { RND_COLOR } from "../../../MODULES/RND_COLOR.js";
import { toDateFormatted } from "../../../MODULES/dateformat.js";
import { SlashCommandBuilder } from "@discordjs/builders";

export const command = new SlashCommandBuilder()
	.setName("info_user")
	.setDescription("ユーザーの情報を取得")
	.addMentionableOption(o => o.setName("user").setDescription("ユーザーを指定しろ").setRequired(true));

/**
 * ユーザーの情報を取得する(インタラクションを使って)
 * @param {import("discord.js").CommandInteraction<import("discord.js").CacheType>} interaction
 */

export async function getUserInfo(interaction) {
	/** @type {import("discord.js").GuildMember}*/
	// @ts-expect-error 型アサーションができないので、
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
