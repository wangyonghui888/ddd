local matchPlayMarginKey = KEYS[1];

local majorMarketValue = tonumber(ARGV[1]);
local marketValueNearDiff = tonumber(ARGV[2]);
local marketCount = tonumber(ARGV[3]);
local malaySpread = tonumber(ARGV[4]);
local oddsValueDiff = tonumber(ARGV[5]);
local marketValueDiff = tonumber(ARGV[6]);
local flag = tonumber(ARGV[7]);

local marketValues = {}
marketValues[1] = string.format("%0.2f", majorMarketValue + marketValueDiff);
if flag == 1 then
    marketValueNearDiff = 0 - marketValueNearDiff
end
--if tonumber(marketValues[1]) < 0 then
--    marketValueNearDiff = 0 - marketValueNearDiff
--end
for i = 2, marketCount, 1 do
    if math.floor(i % 2) == 0 then
        marketValues[i] = string.format("%0.2f", tonumber(marketValues[1]) + math.floor(i / 2) * marketValueNearDiff );
    else
        marketValues[i] = string.format("%0.2f", tonumber(marketValues[1]) - math.floor(i / 2) * marketValueNearDiff );
    end
end

local values = {};
local odds = {}
local mv = 1 - malaySpread / 2
odds[1] = { marketValues[1], string.format("%0.2f", mv), string.format("%0.2f", mv) };
values[1] = odds[1];
for i = 2, marketCount, 1 do
    if math.floor(i % 2) == 0 then
        local overOdds = mv + math.floor(i / 2) * oddsValueDiff;
        local underOdds = mv - math.floor(i / 2) * oddsValueDiff;
        --if flag == 1 and tonumber(marketValues[1]) > 0 then
        --    underOdds = mv + math.floor(i / 2) * oddsValueDiff;
        --    overOdds = mv - math.floor(i / 2) * oddsValueDiff;
        --end
        values[i] = { marketValues[i], string.format("%0.2f", overOdds), string.format("%0.2f", underOdds) };
    else
        local overOdds = mv - math.floor(i / 2) * oddsValueDiff;
        local underOdds = mv + math.floor(i / 2) * oddsValueDiff;
        --if flag == 1 and tonumber(marketValues[1]) > 0 then
        --    underOdds = mv - math.floor(i / 2) * oddsValueDiff;
        --    overOdds = mv + math.floor(i / 2) * oddsValueDiff;
        --end
        values[i] = { marketValues[i], string.format("%0.2f", overOdds), string.format("%0.2f", underOdds) };
    end
end
return values;







