--[[
    Simple Mining Script written in Lua identical to the woodcutting one

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
local Calculations = luajava.bindClass("org.dreambot.api.methods.Calculations")

--[[
    Lets setup Main Variables to adjust script
--]]
local bankers = { "Banker" }
local maxRadius = 10; -- Max Travel distance around starting tile
local deposits = { 11362, 113611, 11360, 10943, 11161 } -- Low Level ore deposits.. Clay, Tin, Copper ids
local items = { "Clay", "Tin ore", "Copper ore" } -- Ores to deposit in bank

--[[
    Variables for script, Do not modify. Script uses them
--]]
local startingArea = nil
local bankLocation = nil
local bankArea = nil

--[[
    Define functions script can use
--]]
function isAtBank()
    if not bankLocation or not bankArea then
        bankLocation = Bank:getClosestBankLocation()
        bankArea = bankLocation:getArea(2)
    end

    if not NPCs:closest(bankers) then return false end
    return true
end

function isAtStartingArea()
    if not startingArea then
        startingArea = Players:localPlayer():getTile():getArea(maxRadius)
    end

    if not startingArea:contains(Players:localPlayer():getTile()) then return false end
    return true
end

--[[
    Actual Script
--]]
script.onStart(function()
    -- Grab Variables onStart
    isAtStartingArea()
    isAtBank()
end)

script.onLoop(function()
    local min = Calculations:random(100, 1000)
    local max = Calculations:random(1001, 5000)

    --MethodProvider:log("Waiting between " .. min .. " and " .. max);
    --MethodProvider:log("Inventory Full: " .. tostring(Inventory:isFull()));
    --MethodProvider:log("Is at Bank (" .. bankLocation:name() .. ")? " .. tostring(isAtBank()));
    --MethodProvider:log("Is at Starting Area? " .. tostring(isAtStartingArea()));

    if Inventory:isFull() then
        if (isAtBank()) then
            local banker = NPCs:closest(deposits)

            if not Bank:isOpen() then
                Bank:open()
                MethodProvider:sleep(100, 1000)
            end

            for _, item in ipairs(items) do
                Bank:depositAll(item)
                MethodProvider:sleep(25, 500)
            end

            Bank:close()
        else
            --MethodProvider:log("Walking to Bank Area");
            Walking:walk(bankArea:getRandomTile())
        end
    else if isAtStartingArea() then
        local me = Players:localPlayer()

        --MethodProvider:log("Is Interacted With? " .. tostring(me:isInteractedWith()));
        --MethodProvider:log("Is Animating? " .. tostring(me:isAnimating()));
        --MethodProvider:log("Animating = " .. tostring(me:getAnimation()));

        if me:getAnimation() == -1 then
            --MethodProvider:log("Mine");
            local depsotiObject = GameObjects:closest(deposits)

            if (depsotiObject) then
                depsotiObject:interact("Mine")
            end
        end
    else
        --MethodProvider:log("Walking to Starting Area");
        Walking:walk(startingArea:getRandomTile())
    end
    end

    return Calculations:random(min, max)
end)
