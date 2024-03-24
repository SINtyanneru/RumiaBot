//import { HTTP_SERVER } from "../HTTP/HTTP_SERVER.js";
//import { WS_SERVER } from "../HTTP/WS_SERVER.js";
import { SQL } from "./SQL.js";
import { pws_main } from "./PROCESS_WS.js";

//let HTTP_SERVER_OBJ = new HTTP_SERVER();
//const WS_SERVER_OBJ = new WS_SERVER();
export const SQL_OBJ = new SQL();

pws_main();