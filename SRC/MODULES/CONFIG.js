import FS from "fs";

const DATA = FS.readFileSync("./Config.json", "utf8");
export const CONFIG = JSON.parse(DATA);