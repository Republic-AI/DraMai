import { Button, Sprite } from "cc";

export default class ComponentsUtils {


  static copyObject(orig) {
    var copy = Object.create(Object.getPrototypeOf(orig));
    this.copyOwnPropertiesFrom(copy, orig);
    return copy;
  }

  static copyOwnPropertiesFrom(target, source) {
    Object.getOwnPropertyNames(source).forEach(function (propKey) {
      var desc = Object.getOwnPropertyDescriptor(source, propKey);
      Object.defineProperty(target, propKey, desc);
    });
    return target;
  }

  public static setGray(node, state) {
    let btn = node.getComponent(Button);
    if (btn) {
      btn.enabled = !state;
    }
    var s: Sprite[] = node.getComponentsInChildren(Sprite);
    for (var i = 0; i < s.length; i++) {
      s[i].grayscale = state;
    }
  }
}
