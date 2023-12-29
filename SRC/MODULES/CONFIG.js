// @ts-check
import CONFIG from "../../Config.json" assert { type: "json" };
// import { readFileSync } from "fs";
/*
 * @typedef {Object} Config
 * @prop {DISCORD} [DISCORD]
 * @prop {SQL} [SQL]
 * @prop {GOOGLE_SEARCH} [GOOGLE_SEARCH]
 * @prop {SNS[]} [SNS]
 * @prop {ADMIN} [ADMIN]
 *
 * @typedef {Object} DISCORD
 * 	@prop {string} TOKEN
 * @typedef {Object} SQL
 * 	@prop {boolean} SQL_CONNECT
 * 	@prop {string} SQL_HOST
 * 	@prop {string} SQL_USER
 * 	@prop {string} SQL_PASS
 * @typedef {Object} GOOGLE_SEARCH
 * 	@prop {string} GOOGLE_API_KEY
 * 	@prop {string} GOOGLE_API_ENGINE_ID
 * @typedef {Object} SNS
 * 	@prop {string} ID
 * 	@prop {string} NAME
 * 	@prop {string} DOMAIN
 * 	@prop {string} API
 * @typedef {Object} ADMIN
 * 	@prop {string[]} ADMIN_ID
 * 	@prop {string} ADMIN_PREFIX
 * 	@prop {("vxtwitter" | "search" | "automod" | "ws" | "ip" | "httpcat" | "locknick" | "calc" | "ping")[]} [DISABLE]
 * 	@prop {string[]} BLOCK
 */
// const DATA = readFileSync("Config.json", "utf8");
/*
 * @type {Config}
 */
export { CONFIG };
