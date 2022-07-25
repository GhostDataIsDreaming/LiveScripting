local Players = luajava.bindClass("org.dreambot.api.methods.interactive.Players")
local Walking = luajava.bindClass("org.dreambot.api.methods.walking.impl.Walking")
local MethodProvider = luajava.bindClass("org.dreambot.api.methods.MethodProvider")
local NPCs = luajava.bindClass("org.dreambot.api.methods.interactive.NPCs")

local me = Players:localPlayer()

MethodProvider:log("Executing")

while (me:isInCombat() or me:isInteractedWith()) do
   MethodProvider:log("Is In Combat or Being Interacted With")
   MethodProvider:sleep(2000)
end

local enemyIds = { "Goblin" }
local enemy = NPCs:closest(enemyIds)

if (enemy) then
   MethodProvider:log("Enemy Found")

--[[ For Some reason this always returns false
--   local canAttack = enemy:hasAction("Attack")
--   MethodProvider:log(canAttack)
--]]

   local action = enemy:getActions()[2]
   MethodProvider:log(action)

   if (action == "Attack") then
      MethodProvider:log("Can Attack Enemy")
      enemy:interact("Attack")
   end
else
   MethodProvider:log("No Enemies Found")
end
