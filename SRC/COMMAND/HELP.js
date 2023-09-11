import { RND_COLOR } from "../MODULES/RND_COLOR";
import { MessageEmbed } from "discord.js";
export class HELP{
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}
	
	async main(){
		let E = this.E;
		const CMD = E.options.getString("mode");
		if(CMD === "slash"){
			const EB = new MessageEmbed();
			EB.setTitle("RumiBOT help");
			EB.setDescription("Sla¡sh komand");
			EB.setColor(RND_COLOR());
	
			EB.addFields({
				name: "/ping",
				value: "Ти а Ping ӱи эру;\nЭрйлйки：/ping host:Hosto Neymu",
				inline: false
			});

			EB.addFields({
				name: "/test",
				value: "Ти а testo komand;\nЭрйлйки：/test",
				inline: false
			});

			EB.addFields({
				name: "/ferris",
				value: "Ти а Feris ӱи komand;\nЭрйлйки：/ferris type:Ferris ӱу форма",
				inline: false
			});

			EB.addFields({
				name: "/ws",
				value: "Ти а WebuSayto ӱи Suksho komand\nЭрйлйки：/ws url:スクショ先のURL、httpsを省ける",
				inline: false
			});

			EB.addFields({
				name: "/info_server",
				value: "よく有るやつ、鯖の情報を取ります、\nЭрйлйки：/info_server",
				inline: false
			});

			EB.addFields({
				name: "/info_user",
				value: "よく有るやつ、ユーザーの情報を取ります、\nЭрйлйки：/info_user",
				inline: false
			});

			EB.addFields({
				name: "/kanji",
				value: "漢字の変換ができます、\nЭрйлйки：/kanji text:漢字が含まれた文字列 mode:変換方法",
				inline: false
			});

			EB.addFields({
				name: "/letter",
				value: "文字の変換ができます、\nЭрйлйки：/letter text:文字列 old:元の文字 new:変換後の文字",
				inline: false
			});

			await E.editReply({embeds:[EB]});
		}else{
			const EB = new MessageEmbed();
			EB.setTitle("るみさんBOTのヘルプ");
			EB.setDescription("メッセージコマンド");
			EB.setColor(RND_COLOR());

			EB.addFields({
				name: "検索",
				value: "ググります、\nЭрйлйки：検索 検索キーワード",
				inline: false
			});

			await E.editReply({embeds:[EB]});
		}
	}
}