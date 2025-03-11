
const api = '//api.baidu.com/open/monitor/report?';
const LensApm = window["LensApm"];
const h5utils:any = null;
function send(data, source, subSource) {
    const sender = new Image();
    data.map(item => {
        item.sub_product = subSource;
        return item;
    });
    data.sub_product = subSource;
    sender.src = `${api}source=${source}&log=${encodeURIComponent(JSON.stringify(data))}`;
}
export default {
     lens : {},
    send:(apmParams)=> {
		
	},
    sendSystem:(apmParams)=> {
        const { game = 'starpartyinggame', page} = apmParams;

        console.log('getsystem----')

        
    },
    logger:async (params)=> {//北极星埋点
        console.log(params);
        LensApm.track.report(params.logger, params.extra)
    }
    
};
