--[[
    Simple Wookcutting Script written in Lua

    Notes:
        1. [Important] This entire script is iterated on every onLoop
        2. Adjust frequency of script with Executing Interval to make it faster or slower
        3. Can be ran anywhere. Will chop trees nearby until inventory is full or is in combat
                and will run to closest bank if either criteria is met
        4. No Antiban present. Figure that shit out yourselves.
--]]
local Players = luajava.bindClass("org.dreambot.api.methods.interactive.Players")
local Walking = luajava.bindClass("org.dreambot.api.methods.walking.impl.Walking")
local MethodProvider = luajava.bindClass("org.dreambot.api.methods.MethodProvider")
local NPCs = luajava.bindClass("org.dreambot.api.methods.interactive.NPCs")
local Inventory = luajava.bindClass("org.dreambot.api.methods.container.impl.Inventory")
local Bank = luajava.bindClass("org.dreambot.api.methods.container.impl.bank.Bank")
local GameObjects = luajava.bindClass("org.dreambot.api.methods.interactive.GameObjects")

--[[
    Lets setup Main Variables to adjust script
--]]
local bankers = { "Banker" }
local maxRadius = 10; -- Max Travel distance around starting tile
local trees = { "Tree", "Oak Tree", "Willow" } -- Trees to Cut Down
local logs = { "Log", "Oak logs", "Willow logs" } -- Logs to deposit in bank

local startingArea = nil --You can specify a certain area or the script will get it onStart

--[[
    Start Scripting Here
--]]
function isInStartingAreaOrNearBank()
    if NPCs.closest(bankers) then return true end

    if not (startingArea:contains(Players:localPlayer():getTile())) then
        Walking:walk(startingArea:getRandomTile())
        return false
    end

    return true

end

script.onStart(function()
    if not startingArea then
        startingArea = Players:localPlayer():getTile():getArea(maxRadius)
    end
end)

script.onLoop(function()
    if not (isInStartingArea()) then return end

    local me = Players:localPlayer()

    if (Inventory:isFull() or me:isInCombat()) then
        MethodProvider:log("Inventory full or in Combat")

        local closestBankLocation = Bank:getClosestBankLocation()
        local bankArea = closestBankLocation:getArea(2)

        local banker = NPCs:closest(bankers)

        if not banker then
            Walking:walk(bankArea:getRandomTile())
        else
            if (Bank:isOpen() == false) then
                Bank:open()
                MethodProvider:sleep(100, 1000)
            end

            for _, item in ipairs(logs) do
                Bank:depositAll(item)
                MethodProvider:sleep(25, 500)
            end

            Bank:close()
        end
    else
        MethodProvider:log("Inventory not full or in combat")

        if (me:isInteractedWith() or me:isAnimating()) then
            -- Do nothing if we are chopping or animating ?? asuming this fires if you are currently
            -- chopping
        else
            if (startingArea:contains(me:getTile())) then
                local treeObject = GameObjects:closest(trees)

                if (treeObject) then -- Only run if a tree is found
                    treeObject:interact("Chop down")
                    MethodProvider:sleep(500, 1000)
                end
            else if not (NPCs:closest(bankers)) then
                Walking:walk(startingArea:getRandomTile())
            end
            end
        end
    end
end)