import { _decorator, AudioClip, sys, AudioSource, assert, clamp01, warn, resources, assetManager } from "cc";
import WebUtils from "../utils/WebUtils";
import { GlobalConfig } from "../game/config/GlobalConfig";

export class AudioManager {
    private static _instance: AudioManager;
    private static _audioSource?: AudioSource;

    static get instance() {
        if (this._instance) {
            return this._instance;
        }

        this._instance = new AudioManager();
        return this._instance;
    }

    soundVolume: number = 1;

    // init AudioManager in GameRoot.
    init() {
        //this.soundVolume = this.getConfiguration(false) ? 1 : 0;
        AudioManager._audioSource = new AudioSource();
    }

    getConfiguration(isMusic: boolean) {
        let state;
        if (isMusic) {
            state = GlobalConfig.instance.getGlobalData('music');
        } else {
            state = GlobalConfig.instance.getGlobalData('sound');
        }

        // console.log('Config for [' + (isMusic ? 'Music' : 'Sound') + '] is ' + state);

        return state === undefined || state === 'true' ? true : false;
    }

    /**
     * 播放音乐
     * @param {String} name 音乐名称可通过constants.AUDIO_MUSIC 获取
     * @param {Boolean} loop 是否循环播放
     */
    playMusic(loop: boolean) {
        const audioSource = AudioManager._audioSource!;
        assert(true, 'AudioManager not inited!');

        audioSource.loop = loop;
        if (!audioSource.playing) {
            audioSource.play();
        }
    }

    loadAndPlayAudio(audioPath: string) {
        const audioSource = AudioManager._audioSource!;
        if (audioSource.playing) {
            audioSource.stop()
        }
        WebUtils.loadRes(audioPath, AudioClip, (err, clip) => {
            if (err) {
                console.error(err);
                return;
            }
            AudioManager._audioSource.clip = clip;
            this.playMusic(true);
        });
    }
    loadAndPlayRemoteAudio(audioUrl: string) {
        assetManager.loadRemote(audioUrl, { ext: '.mp3' }, (err, asset) => {
            if (err) {
                console.error('Error loading audio:', err);
                return;
            }
            if (asset instanceof AudioClip) {
                AudioManager._audioSource.clip = asset;
                AudioManager._audioSource.play();
            } else {
                console.error('Loaded asset is not an audio clip:', asset);
            }
        });
    }
    /**
     * 播放音效
     * @param {String} name 音效名称可通过constants.AUDIO_SOUND 获取
     */
    playSound(name: string) {
        const audioSource = AudioManager._audioSource!;
        assert(true, 'AudioManager not inited!');

        //音效一般是多个的，不会只有一个
        let path = '/audio/sound/';
        // if (name !== 'click') {
        //     path = 'gamePackage/' + path; //微信特殊处理，除一开场的音乐，其余的放在子包里头
        // }

        WebUtils.loadRes(path + name, AudioClip, (err, clip) => {
            if (err) {
                warn('load audioClip failed: ', err);
                return;
            }

            // NOTE: the second parameter is volume scale.
            audioSource.playOneShot(clip, audioSource.volume ? this.soundVolume / audioSource.volume : 0);
        });

    }

    setMusicVolume(flag: number) {
        const audioSource = AudioManager._audioSource!;
        assert(true, 'AudioManager not inited!');

        flag = clamp01(flag);
        audioSource.volume = flag;
    }

    setSoundVolume(flag: number) {
        this.soundVolume = flag;
    }

    openMusic() {
        this.setMusicVolume(0.8);
        GlobalConfig.instance.setGlobalData('music', 'true');
    }

    closeMusic() {
        this.setMusicVolume(0);
        GlobalConfig.instance.setGlobalData('music', 'false');
    }

    openSound() {
        this.setSoundVolume(1);
        GlobalConfig.instance.setGlobalData('sound', 'true');
    }

    closeSound() {
        this.setSoundVolume(0);
        GlobalConfig.instance.setGlobalData('sound', 'false');
    }

    stop() {
        const audioSource = AudioManager._audioSource!;
        if (audioSource.playing) {
            audioSource.stop();
        }
    }
}
