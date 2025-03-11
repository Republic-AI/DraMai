interface NumberCounterConfig {
    startNumber: number;
    endNumber: number;
    duration: number;
    onUpdate?: (currentNumber: number) => void;
    onComplete?: () => void;
}

export class NumberCounter {
    private currentNumber: number;
    private startNumber: number;
    private endNumber: number;
    private timer: number;
    private isIncreasing: boolean;
    private duration: number;
    private onUpdate?: (currentNumber: number) => void;
    private onComplete?: () => void;

    constructor(config: NumberCounterConfig) {
        this.currentNumber = config.startNumber;
        this.startNumber = config.startNumber;
        this.endNumber = config.endNumber;
        this.timer = 0;
        this.isIncreasing = config.endNumber > config.startNumber;
        this.duration = config.duration;
        this.onUpdate = config.onUpdate;
        this.onComplete = config.onComplete;
    }

    update(dt: number) {
        if (this.timer < this.duration) {
            const delta = Math.abs(this.endNumber - this.startNumber) * dt / this.duration;
            this.currentNumber += this.isIncreasing ? delta : -delta;
            this.timer += dt;
            if ((this.isIncreasing && this.currentNumber > this.endNumber) ||
                (!this.isIncreasing && this.currentNumber < this.endNumber)) {
                this.currentNumber = this.endNumber;
            }
            if (this.onUpdate) {
                this.onUpdate(this.currentNumber);
            }
            if ((this.isIncreasing && this.currentNumber === this.endNumber) ||
                (!this.isIncreasing && this.currentNumber === this.startNumber)) {
                if (this.onComplete) {
                    this.onComplete();
                }
                this.isIncreasing = !this.isIncreasing;
                this.timer = 0;
            }
        }
    }
}