import * as FS from "node:fs";
import { joinVoiceChannel, entersState, VoiceConnectionStatus, createAudioResource, StreamType, createAudioPlayer, AudioPlayerStatus, NoSubscriberBehavior, generateDependencyReport } from "@discordjs/voice";

export class VC_MUSIC {
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}
	async main() {
		const FILE = this.E.options.getString("file");
		FS.access("./DATA/MUSIC/" + FILE.replaceAll("../", "").replaceAll("./", ""), FS.constants.F_OK, async (ERR) => {
			if (ERR) {
				await this.E.editReply("その音源はないよ<:blod_sad:1155039115709005885>");
			} else {
				try {
					const VCC = this.E.member.voice.channel;
					if (VCC) {
						if (VCC.joinable) {
							const CON = joinVoiceChannel({
								guildId: VCC.guild.id,
								channelId: VCC.id,
								adapterCreator: VCC.guild.voiceAdapterCreator,
								selfDeaf: false,
								selfMute: false
							});
							const mp3FilePath = "./DATA/MUSIC/" + FILE.replaceAll("../", "").replaceAll("./", ""); // MP3ファイルのパス

							const player = createAudioPlayer({
								behaviors: {
									noSubscriber: NoSubscriberBehavior.Pause,
								}
							});

							player.on('error', async (error) => {
								await this.E.editReply(error);
							});

							CON.subscribe(player);

							const resource = createAudioResource(mp3FilePath, {
								inputType: StreamType.Arbitrary,
							});

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