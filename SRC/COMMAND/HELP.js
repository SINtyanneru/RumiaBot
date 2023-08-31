class HELP{
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}
	
	async main(){
		let E = this.E;
		const CMD = E.options.getString("mode");
		if(CMD === "slash"){
			const EB = new MessageEmbed();
			EB.setTitle("るみさんBOTのヘルプ");
			EB.setDescription("スラッシュコマンド");
			EB.setColor(RND_COLOR());
	
			EB.addFields({
				name: "/ping",
				value: "pignをします、\n使用方法：/ping host:pingする先",
				inline: false
			});

			EB.addFields({
				name: "/test",
				value: "テストで作った恒例のコマンドです、\n使用方法：/test",
				inline: false
			});

			EB.addFields({
				name: "/ferris",
				value: "フェリスの画像を出します、\n使用方法：/ferris type:フェリスのタイプ",
				inline: false
			});

			EB.addFields({
				name: "/ws",
				value: "ウェブサイトをスクショする、\n使用方法：/ws url:スクショ先のURL、httpsを省ける",
				inline: false
			});

			EB.addFields({
				name: "/info_server",
				value: "よく有るやつ、鯖の情報を取ります、\n使用方法：/info_server",
				inline: false
			});

			EB.addFields({
				name: "/info_user",
				value: "よく有るやつ、ユーザーの情報を取ります、\n使用方法：/info_user",
				inline: false
			});

			EB.addFields({
				name: "/kanji",
				value: "漢字の変換ができます、\n使用方法：/kanji text:漢字が含まれた文字列 mode:変換方法",
				inline: false
			});

			EB.addFields({
				name: "/letter",
				value: "文字の変換ができます、\n使用方法：/letter text:文字列 old:元の文字 new:変換後の文字",
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
				value: "ググります、\n使用方法：検索 検索キーワード",
				inline: false
			});

			await E.editReply({embeds:[EB]});
		}
	}
}