import { WebSocketServer } from "ws";

export class WS_SERVER {
	constructor() {
		this.PORT = 3001;
		this.SERVER = undefined;
		this.SOCKETS = [];
	}

	main() {
		/*
		this.SERVER = new WebSocketServer({ port: this.PORT });

		this.SERVER.on("connection", SOCKET => {
			console.log("[ INFO ][ WS_SERVER ]Connected");
			this.SOCKETS.push(SOCKET);

			SOCKET.on("close", () => {
				console.log("[ INFO ][ WS_SERVER ]Disconnected");
			});
		});
		*/
	}
}
