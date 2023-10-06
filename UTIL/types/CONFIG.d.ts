/**
 * @type {Config}
 */
export const CONFIG: Config;
export type Config = {
    TOKEN: string;
    ID: string;
    SQL_HOST?: string;
    SQL_USER?: string;
    SQL_PASS?: string;
    GOOGLE_API_KEY?: string;
    GOOGLE_API_ENGINE_ID?: string;
    SNS?: SNS[];
    ADMIN_ID: string;
    ADMIN_PREFIX: string;
    DISABLE?: ("vxtwitter" | "search" | "automod" | "ws" | "ip" | "httpcat")[];
};
export type SNS = {
    ID: string;
    NAME: string;
    DOMAIN: string;
    API: string;
};
