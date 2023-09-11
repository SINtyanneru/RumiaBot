import fs from "fs";
import path from "path";

//フォルダのパスと出力ファイル名を指定
const folderPath = "./SRC";
const outputFile = "./BIN/rumiabot.js";
console.log("[ *** ]BUILDING...");

//フォルダ内のファイルを再帰的に結合する関数
function combineFilesInFolder(folderPath, outputFile) {
	try {
		const files = fs.readdirSync(folderPath);

		let combinedContent = "";

		files.forEach(file => {
			const filePath = path.join(folderPath, file);
			const stat = fs.statSync(filePath);

			if (stat.isDirectory()) {
				// サブフォルダの場合は再帰的に処理
				combinedContent += combineFilesInFolder(filePath, outputFile);
			} else {
				const fileContent = fs.readFileSync(filePath, "utf-8");
				combinedContent += fileContent + "\n";
			}
		});

		return combinedContent;
	} catch (EX) {
		console.log("[ ERR ]BUILD ERR:" + EX);
	}
}

//フォルダ内のファイルを結合して出力ファイルに書き込む
const combinedContent = combineFilesInFolder(folderPath, outputFile);
fs.writeFileSync(outputFile, combinedContent, "utf-8");
console.log("[ OK ]BUILD SUCSESFULL!");