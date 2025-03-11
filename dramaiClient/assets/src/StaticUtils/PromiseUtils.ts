export class PromiseUtils {
    static parallel<T extends Promise<any>[]>(promises: T) {
        return Promise.all(promises);
    }
    static race<T>(promises: Promise<T>[]) {
        return Promise.race(promises);
    }
    static wait(time: number): Promise<void> {
        return new Promise<void>((resolve, _) => {
            setTimeout(resolve, time);
        });
    }
}