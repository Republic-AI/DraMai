import { GlobalConfig } from "../game/config/GlobalConfig"

class Log{
    debug(){

    }

    log(TAG: string, ...messages: any[]){
        if(GlobalConfig.instance.isProduction){
            console.debug(`${TAG}`, ...messages)
        }else{
            console.log(`${TAG}`, ...messages)
        }

    }
}
export default new Log()