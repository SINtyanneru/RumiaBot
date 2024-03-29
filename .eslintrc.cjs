/** @type {import("eslint").ESLint.ConfigData} */
module.exports = {
	"env": {
		"es2021": true,
		"node": true
	},
	"extends": ["eslint:recommended", "plugin:@typescript-eslint/recommended"],
	"overrides": [
		{
			"env": {
				"node": true
			},
			"files": [".eslintrc.{js,cjs}"],
			"parserOptions": {
				"sourceType": "script"
			}
		}
	],
	"parser": "@typescript-eslint/parser",
	"parserOptions": {
		"ecmaVersion": "latest",
		"sourceType": "module"
	},
	"plugins": ["@typescript-eslint"],
	"rules": {
		"indent": ["error", "tab", { "SwitchCase": 1 }],

		"linebreak-style": ["error", "unix"],
		"quotes": ["error", "double", { avoidEscape: true }],
		"semi": ["error", "always"],
		"no-empty": ["warn"],
		"no-unused-vars": ["warn"]
	}
};
