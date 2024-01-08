import { MessageEmbed } from "discord.js";
import { NULLCHECK } from "../../MODULES/NULLCHECK.js";
import { RND_COLOR } from "../../MODULES/RND_COLOR.js";
import { toDateFormatted } from "../../MODULES/dateformat.js";
import { SlashCommandBuilder } from "@discordjs/builders";

export const command = new SlashCommandBuilder()
	.setName("info_user")
	.setDescription("ユーザーの情報を取得")
	.addUserOption(o => o.setName("user").setDescription("ユーザーを指定しろ").setRequired(true));

/**
 * ユーザーの情報を取得する(インタラクションを使って)
 * @param {import("discord.js").CommandInteraction<import("discord.js").CacheType>} interaction
 */

export async function getUserInfo(
	interaction: import("discord.js").CommandInteraction<import("discord.js").CacheType>
) {
	const User = interaction.options.getUser("user")!;
	const guildMember = interaction.guild!.members.cache.get(User.id)!;

	//埋め込み
	const embed = new MessageEmbed();

	//ユーザー名
	if (guildMember.nickname !== undefined && guildMember.nickname !== null) {
		embed.setTitle(NULLCHECK(guildMember.nickname));
	} else {
		embed.setTitle(User.username);
	}

	embed.setDescription(User.username);
	embed.setColor(RND_COLOR());

	embed.setThumbnail(`https://cdn.discordapp.com/avatars/${User.id}/${User.avatar}.png`);

	embed.addFields({
		name: "ID",
		value: NULLCHECK(User.id),
		inline: false
	});

	//アカウント作成日
	embed.addFields({
		name: "アカウント作成日",
		value: toDateFormatted(User.createdAt),
		inline: false
	});

	//鯖に参加した日付
	embed.addFields({
		name: "鯖に参加した日付",
		value: guildMember.joinedAt ? toDateFormatted(guildMember.joinedAt) : "N/A",
		inline: false
	});

	//ニトロブースト開始日
	if (guildMember.premiumSince !== null) {
		embed.addFields({
			name: "ブースト開始日",
			//日本表記
			value: toDateFormatted(guildMember.premiumSince),
			inline: false
		});
	}

	//BOTか
	embed.addFields({
		name: "BOTか",
		value: User.bot ? "はい" : "いいえ",
		inline: false
	});

	//キック可能か
	embed.addFields({
		name: "わたしはこのユーザーを",
		value: `追放でき${guildMember.kickable ? "ます" : "ません"}`,
		inline: false
	});

	//BAN可能か
	embed.addFields({
		name: "わたしはこのユーザーを",
		value: `BANでき${guildMember.bannable ? "ます" : "ません"}`,
		inline: false
	});

	await interaction.editReply({ embeds: [embed] });
}
