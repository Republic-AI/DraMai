import { NPCPartDisplay, RolePartIcon } from "../StaticUtils/NPCConfig";

class loginModel {
  body: { id: number; iconValue: string; roleValue } = {
    id: 900,
    iconValue: RolePartIcon.body[0],
    roleValue: NPCPartDisplay.body[0],
  };
  hair: { id: number; iconValue: string; roleValue } = {
    id: 100,
    iconValue: RolePartIcon.hair.man[0],
    roleValue: NPCPartDisplay.hair.man[0],
  };
  pants: { id: number; iconValue: string; roleValue } = {
    id: 300,
    iconValue: RolePartIcon.pants[0],
    roleValue: NPCPartDisplay.pants[0],
  };
  shirt: { id: number; iconValue: string; roleValue } = {
    id: 200,
    iconValue: RolePartIcon.shirt[0],
    roleValue: NPCPartDisplay.shirt[0],
  };
  currentType: { id: number; iconValue: string; roleValue } = {
    id: 100,
    iconValue: RolePartIcon.hair.man[0],
    roleValue: NPCPartDisplay.hair.man[0],
  };
  currentName = "hair";
  character: number;
}
export default new loginModel();
