import { Observer } from "../../utils/Observer";

export default class BaseModel extends Observer {
    public static netRsp;
    constructor() {
        super();
        //netRsp.on(this);
    }
}