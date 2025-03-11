import { _decorator, Component, Node, TiledMap, Vec2 } from 'cc';
import { AStarStep } from './AStarStep'; // 假设有一个 AStarStep 类处理每个步伐
const { ccclass, property } = _decorator;

@ccclass('AStar')
export class AStar extends Component {

    @property(TiledMap)
    public map: TiledMap = null;

    _open = [];
    _closed = [];
    _layerBarrier: any;

    protected onLoad(): void {
        if (this.map) {
            this._layerBarrier = this.map.getLayer('building');
            if (this._layerBarrier)
                console.log("Layer size: " + JSON.stringify(this._layerBarrier.layerSize));
        }
    }

    start() {
    }

    update(deltaTime: number) {
    }

    // A* 算法
    moveToward(start, finish) {
        this._closed = [];
        this._open = [];
        let paths = [];

        console.log("Start:", start);
        console.log("Finish:", finish);
        this._open.push(new AStarStep(start));  // 把起点加入 open 列表
        let pathFound = false;
        
        do {
            // 从 open 列表中取出 f 值最小的节点
            let currentStep = this._open.shift();
            this._closed.push(currentStep);

            // 如果当前节点是终点，路径找到了
            if (currentStep.position.equals(finish)) {
                pathFound = true;
                let tmpStep = currentStep;
                do {
                    paths.unshift(tmpStep.position);
                    tmpStep = tmpStep.last;
                } while (tmpStep !== null);

                break;
            }

            // 获取当前节点的相邻节点（上、下、左、右）
            let borderPositions = this._borderMovablePoints(currentStep.position);
            for (let i = 0; i < borderPositions.length; ++i) {
                let borderPosition = borderPositions[i];

                // 检查是否已经在 closed 列表中
                if (this._indexOfStepArray(borderPosition, this._closed) != -1) {
                    continue;
                }

                // 创建新的 AStarStep 节点
                let step = new AStarStep(borderPosition);
                let moveCost = this._costToMove(borderPosition, finish);
                let index = this._indexOfStepArray(borderPosition, this._open);

                if (index == -1) {
                    step.last = currentStep;
                    step.g = currentStep.g + moveCost;
                    let distancePoint = new Vec2(borderPosition.x - finish.x, borderPosition.y - finish.y);
                    step.h = Math.abs(distancePoint.x) + Math.abs(distancePoint.y);
                    step.f = step.g + step.h;
                    this._insertToOpen(step);
                } else {
                    step = this._open[index];
                    if (currentStep.g + moveCost < step.g) {
                        step.g = currentStep.g + moveCost;
                        this._open.splice(index, 1);
                        this._insertToOpen(step);
                    }
                }
            }
        } while (this._open.length > 0);

        return pathFound ? paths : null;  // 如果找到了路径，返回路径；否则返回 null
    }

    // 计算到目标点的成本
    _costToMove(positionLeft, positionRight) {
        return 1;  // 假设每一步的移动成本为 1
    }

    _borderMovablePoints(position) {
        let results = [];
        let directions = [
            new Vec2(0, -1),  // 上
            new Vec2(0, 1),   // 下
            new Vec2(-1, 0),  // 左
            new Vec2(1, 0)    // 右
        ];
    
        for (let dir of directions) {
            let newPos = new Vec2(position.x + dir.x, position.y + dir.y);
    
            // 确保不越界并且该位置的瓦片不是障碍物（假设 1 是障碍物）
            if (this._isValidPosition(newPos)) {
                results.push(newPos);
            }
        }
    
        console.log("borderMovablePoints results:", results); // Log to check if positions are correctly returned
        return results;
    }
    
    // 检查位置是否有效（不越界且不为障碍物）
    _isValidPosition(position) {
        if (position.x < 0 || position.y < 0 || 
            position.x >= this._layerBarrier.layerSize.width || 
            position.y >= this._layerBarrier.layerSize.height) {
            return false; // 越界
        }
    
        // 确保 getTileGIDAt() 返回值不是 1（假设 1 是障碍物）
        return this._layerBarrier.getTileGIDAt(position.x, position.y) === 0;
    }
    

    // 返回给定位置的 AStarStep 在列表中的索引
    _indexOfStepArray(value, stepArray) {
        for (let i = 0; i < stepArray.length; ++i) {
            if (value.equals(stepArray[i].position)) {
                return i;
            }
        }
        return -1;
    }

    // 将步伐插入 open 列表，按照 f 值从小到大排序
    _insertToOpen(step) {
        let stepF = step.f;
        let length = this._open.length;
        let i = 0;
        for (; i < length; ++i) {
            if (stepF <= this._open[i].f) {
                break;
            }
        }
        this._open.splice(i, 0, step);
    }

    checkBuildingData(position){
        console.log("check=====" +  this._layerBarrier.getTileGIDAt(position.x, position.y))
    }
}
