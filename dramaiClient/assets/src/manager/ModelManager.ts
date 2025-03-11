import StaticConfigModel from "../model/StaticConfig/StaticConfigModel";
import TownModel from "../town/TownModel";



export default class ModelManager {
    /**主界面数据类 */
    public configModel: StaticConfigModel;
    public townModel: TownModel;

    constructor() {
        let s = this;
        s.register();
    }
    private register(): void {
        let s = this;
        s.townModel = new TownModel();
        s.configModel = new StaticConfigModel();


    }
    public init(){
        this.townModel.init();
    }
}