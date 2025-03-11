import { _decorator, Component, EditBox, Node, randomRangeInt } from 'cc';
import { network } from '../model/RequestData';
import { socket } from '../game/App';
import loginModel from '../model/loginModel';
const { ccclass, property } = _decorator;

const randomPool = {
    name:[
        'John',
        'Mary',
        'Michael',
        'Sarah',
        'David',
        'Emily',
        'James',
        'Jessica',
        'Robert',
        'Elizabeth',
        'William',
        'Jennifer',
        'Richard',
        'Laura',
        'Thomas',
        'Megan',
        'Charles',
        'Amanda',
        'Christopher',
        'Ashley',
    ],
    occupation: [
        'Doctor',
        'Nurse',
        'Teacher',
        'Engineer',
        'Lawyer',
        'Accountant',
        'Programmer',
        'Police',
        'Firefighter',
        'Builder',
        'Salesperson',
        'Chef',
        'Plumber',
        'Driver',
        'Electrician',
        'Mechanic',
        'Journalist',
        'Writer',
        'Scientist',
        'Designer',
    ],
    password: [
        'Kind',
        'Generous',
        'Enthusiastic',
        'Compassionate',
        'Empathetic',
        'Altruistic',
        'Charitable',
        'Warm-hearted',
        'Benevolent',
        'Thoughtful',
        'Supportive',
        'Considerate',
        'Loving',
        'Sympathetic',
        'Gracious',
        'Philanthropic',
        'Affectionate',
        'Nurturing',
        'Friendly',
        'Tender',
        'Caring',
        'Forgiving',
        'Selfless',
    ]
}

const treePos = -480
const cloudPos = -620
const cloudAdapt = [
  1.5,
  1,
  1.75,
  0.5
]

const adapt_x = -60
const adapt_cow_x = -80
const cow_x =[
    0,
    -94,
    -191,
    -285
]

const treeAdapt = [
    2.75,
    2.75,
    2.5,
    2.5,
    2,
    2,
  ]

const rabbit_x =[
    0,
    -66,
    -133,
    -200,
]
@ccclass('TownCreateRole')
export class TownCreateRole extends Component {

    @property(EditBox)
    public edit_name: EditBox = null;

    @property(EditBox)
    public edit_password: EditBox = null;

    @property(EditBox)
    public edit_occupation: EditBox = null;

    @property(Node)
    public randomName: Node = null;

    @property(Node)
    public randomPassword: Node = null;

    @property(Node)
    public randomOccupation: Node = null;

    @property(Node)
    public roleView: Node = null;

    @property(Node)
    public infoView: Node = null;

    @property(Node)
    public roleCreate: Node = null;

    @property(Node)
    public flower: Node = null;
    @property(Node)
    public flower1: Node = null;
    @property(Node)
    public cow: Node = null;

    @property({type:[Node]})
    public cloud:Node[] = [];

    @property(Node)
    public tileMap: Node = null;

    flower_cow_index = 0

    rabbit_index = 0

    addFrame = 0
    addFrame1 = 0
    addFrame2 = 0

    edits = {
        name: this.edit_name,
        occupation: this.edit_occupation,
        password: this.edit_password
    }

    addFrameRabbit = 0
    start() {
        // this.edits = {
        //     name: this.edit_name,
        //     occupation: this.edit_occupation,
        //     password: this.edit_password
        // }
    
        // if(loginModel.character){
        //     this.roleCreate.active = false
        //     // let json = new network.GetAllNPCRequest();
        //     // json.command = 10002;
        //     // json.type = 1;
        //     // socket.sendWebSocketBinary(json);
        //     this.tileMap.active = true
        // }else{
        //     this.roleCreate.active = true
        //     this.tileMap.active = false
        //     //this.bgs.active = true
        // }
    }

    getRandomOccupation(event: any,key: string){
        this.edits[key].string = randomPool[key][randomRangeInt(0, randomPool[key].length)]
    }


    infoNextPart(){
        if(this.edit_name.string.length && this.edit_password.string.length && this.edit_occupation){
            this.roleView.active = true
            this.infoView.active = false
        }
    }

    roleNextPart(){
        let json = new network.CreateRoleRequest();
        json.command = 10001;
        json.type = 1;
        json.data.name = this.edit_name.string;
        json.data.career = this.edit_occupation.string;
        json.data.keyword = this.edit_password.string;
        json.data.model = 900
        json.data.hair = loginModel.hair.id
        json.data.top = loginModel.shirt.id
        json.data.bottoms = loginModel.pants.id
        socket.sendWebSocketBinary(json);
        this.roleCreate.active = false
        this.tileMap.active = true
    }

    update(deltaTime: number) {
        this.addFrame = this.addFrame + deltaTime
        this.addFrame1 = this.addFrame1 + deltaTime
        this.addFrame2 = this.addFrame2 + deltaTime
        this.addFrameRabbit = this.addFrameRabbit + deltaTime
        if(this.addFrame >= 0.15){
            this.flower.setPosition(this.flower_cow_index * adapt_x, 0)
            this.flower1.setPosition(this.flower_cow_index * adapt_x, 0)
            //this.cow.setPosition(cow_x[this.flower_cow_index], 0)
            this.flower_cow_index = this.flower_cow_index >= 3 ? 0 : this.flower_cow_index + 1

            this.addFrame = 0
        }
        if(this.addFrame2 > 0.16){
            // this.flower.setPosition(this.flower_cow_index * adapt_x, 0)
            // this.flower1.setPosition(this.flower_cow_index * adapt_x, 0)
            this.cow.setPosition(cow_x[this.flower_cow_index], 0)
            //this.flower_cow_index = this.flower_cow_index >= 3 ? 0 : this.flower_cow_index + 1
            this.addFrame2 = 0
        }

        // if(this.addFrameRabbit >= 0.10){

        //     this.rabbit.setPosition(rabbit_x[this.rabbit_index], 0)
        //     //this.cow.setPosition(cow_x[this.flower_cow_index], 0)
        //     this.rabbit_index = this.rabbit_index >= 3 ? 0 : this.rabbit_index + 1

        //     this.addFrameRabbit = 0
        // }

        this.cloud.forEach((currentCloud, index)=>{
            if(currentCloud.getPosition().x < cloudPos){
              currentCloud.setPosition(-cloudPos, currentCloud.getPosition().y)
            }
            currentCloud.setPosition(currentCloud.getPosition().x - cloudAdapt[index], currentCloud.getPosition().y)
          })

        //   this.treesBg.forEach((tree, index)=>{
        //     if(tree.getPosition().x > -treePos){
        //         tree.setPosition(treePos, tree.getPosition().y)
        //     }
        //     tree.setPosition(tree.getPosition().x + treeAdapt[index], tree.getPosition().y)
        //   })

    }
}

