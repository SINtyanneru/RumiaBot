import { readFileSync } from "fs";
/**
 * @typedef {Object} Config
 * @prop {string} TOKEN
 * @prop {string} ID
 * @prop {string} [SQL_HOST]
 * @prop {string} [SQL_USER]
 * @prop {string} [SQL_PASS]
 * @prop {boolean} [SQL_CONNECT]
 * @prop {string} [GOOGLE_API_KEY]
 * @prop {string} [GOOGLE_API_ENGINE_ID]
 * @prop {SNS[]} [SNS]
 * @prop {string[]} ADMIN_ID
 * @prop {string} ADMIN_PREFIX
 * @prop {("vxtwitter" | "search" | "automod" | "ws" | "ip" | "httpcat" | "locknick" | "calc" | "ping")[]} [DISABLE]
 * @prop {string[]} BLOCK
 *
 * @typedef {Object} SNS
 * 	@prop {string} ID
 * 	@prop {string} NAME
 * 	@prop {string} DOMAIN
 * 	@prop {string} API
 */
const DATA = readFileSync("Config.json", "utf8");
/**
 * @type {Config}
 */
export const CONFIG = JSON.parse(DATA);
