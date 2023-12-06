import * as FS from "node:fs";
import { joinVoiceChannel, createAudioResource, StreamType, createAudioPlayer, NoSubscriberBehavior } from "@discordjs/voice";

export class VC_MUSIC {
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}
	async main() {
		const FILE = this.E.options.getString("file");
		FS.access("./DATA/MUSIC/" + FILE.replaceAll("../", "").replaceAll("./", ""), FS.constants.F_OK, async ERR => {
			if (ERR) {
				await this.E.editReply("その音源はないよ<:blod_sad:1155039115709005885>");
			} else {
				try {
					const VCC = this.E.member.voice.channel;
					if (VCC) {
						if (VCC.joinable) {
							//VCに参加するよ
							const CON = joinVoiceChannel({
								guildId: VCC.guild.id,
								channelId: VCC.id,
								adapterCreator: VCC.guild.voiceAdapterCreator,
								selfDeaf: false,
								selfMute: false
							});

							//音源のパスを指定するよ
							const mp3FilePath = "./DATA/MUSIC/" + FILE.replaceAll("../", "").replaceAll("./", ""); // MP3ファイルのパス

							//音楽流すやつだと思う多分
							const player = createAudioPlayer({
								behaviors: {
									noSubscriber: NoSubscriberBehavior.Pause
								}
							});

							//エラー時の処理
							player.on("error", async error => {
								await this.E.editReply(error);
							});

							//VCへの接続に音楽プレイヤーをサブすくさせる
							CON.subscribe(player);

							//なんこれ
							const resource = createAudioResource(mp3FilePath, {
								inputType: StreamType.Arbitrary
							});

							//再生
							player.play(resource);
							await this.E.editReply("再生開始したかもしれないし、そうじゃないかもしれない");
						} else {
							await this.E.editReply("権限的に参加できないよ（泣）");
						}
					} else {
						await this.E.editReply("どこのVCだよ");
					}
				} catch (EX) {
					console.log(EX);
				}
			}
		});
	}
}
