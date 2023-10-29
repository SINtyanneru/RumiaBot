
type APIFailure = {
	STATUS:false,
	REASON?:string
}
type ApiResponse<T>  = T | APIFailure