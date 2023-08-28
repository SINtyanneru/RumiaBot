class INFO{
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}

	//鯖情報取得
	async sv_main(){
		let E = this.E;
		const GLID = client.guilds.cache.get(E.guildId);

		const EB = new MessageEmbed();
		EB.setTitle(NULLCHECK(GLID.name))
		EB.setDescription(NULLCHECK(GLID.description))
		EB.setColor(RND_COLOR());

		EB.setThumbnail("https://cdn.discordapp.com/icons/" + GLID.id + "/" + GLID.icon + ".png")

		EB.addFields({
			name: "ID",
			value: NULLCHECK(GLID.id),
			inline: false
		});
		EB.addFields({
			name: "verificationLevel",
			value: NULLCHECK(GLID.verificationLevel),
			inline: false
		});

		
		const OWNER = client.users.cache.get(GLID.ownerId);

		EB.addFields({
			name: "所有者",
			value: NULLCHECK(OWNER.name),
			inline: false
		});

		await E.editReply({embeds:[EB]});
	}

	//ユーザー情報取得
	async usr_main(){
		let E = this.E;

		const MEMBER = E.options.getMentionable("user");
		const USER = client.users.cache.get(E.options.getMentionable("user").user.id);

		const EB = new MessageEmbed();
		EB.setTitle(NULLCHECK(MEMBER.nickname));
		EB.setDescription(NULLCHECK(USER.username));
		EB.setColor(RND_COLOR());

		EB.setThumbnail("https://cdn.discordapp.com/avatars/" + USER.id + "/" + USER.avatar + ".png");

		EB.addFields({
			name: "ID",
			value: NULLCHECK(USER.id),
			inline: false
		});


		if(USER.bot){
			EB.addFields({
				name: "BOTか",
				value: "はい",
				inline: false
			});
		}else{
			EB.addFields({
				name: "BOTか",
				value: "いいえ",
				inline: false
			});
		}

		await E.editReply({embeds:[EB]});
	}
}