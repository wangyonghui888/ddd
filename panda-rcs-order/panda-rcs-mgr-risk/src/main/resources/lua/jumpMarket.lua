-- 1-两项盘，2-三项盘
local isTwoMarket = tonumber(ARGV[1]);
-- 投注金额，单位元
local betAmount = tonumber(ARGV[2]);
-- 欧赔赔率
local odds = tonumber(ARGV[3]);
-- 投注项类型
local oddsType = ARGV[4];
-- 赛种
local sportId = tonumber(ARGV[5]);
-- 赛事账务日
local dateExpect = ARGV[6];
-- 足球-盘口ID，篮球-位置ID
local marketId = ARGV[7];
-- 累计差额计算方式，0-投注额差值，1-投注/赔付混合差值
local balanceOption = tonumber(ARGV[8]);
-- 跳分机制，0-累计/单枪跳分，1-累计差值跳分
local oddChangeRule = tonumber(ARGV[9]);
-- 累计跳盘限额/一级累计跳盘限额
local jumpMarketOneLimit = tonumber(ARGV[10]);
-- 单枪跳盘限额/二级累计跳盘限额
local jumpMarketTwoLimit = tonumber(ARGV[11]);
-- 是否开启跳盘，0-否，1-是
local isOpenJumpMarket = tonumber(ARGV[12]);
-- 是否倍数跳盘，0-否，1-是
local isMultipleJumpMarket = tonumber(ARGV[13]);

-- 跳盘 投注额 累计key
local jumpMarketBetKey = string.format("rcs:odds:jumpMarket:%s:%s:bet{%s}", dateExpect, marketId, marketId);
-- 跳盘 投注/赔付混合 累计key
local jumpMarketMixKey = string.format("rcs:odds:jumpMarket:%s:%s:mix{%s}", dateExpect, marketId, marketId);

-- 投注/赔付混合值
local mixValue = betAmount;
if odds > 2 then
    mixValue = betAmount * (odds - 1);
end
-- 跳盘 同时累计 投注额 和 投注/赔付混合值
redis.call("HINCRBYFLOAT", jumpMarketBetKey, oddsType, betAmount);
redis.call("HINCRBYFLOAT", jumpMarketMixKey, oddsType, mixValue);

local jumpMarketKey = jumpMarketBetKey;
-- 0-投注额差值，1-投注/赔付混合差值
if balanceOption == 1 then
    jumpMarketKey = jumpMarketMixKey;
end
local jumpMarketInfo = redis.call('HGETALL', jumpMarketKey);

-- 所有投注项累计
local jumpMarketTotal = 0;
-- 当前投注项累计
local jumpMarketCurrent = 0;
for i = 1, #jumpMarketInfo, 2 do
    -- key 投注项类型
    local k = jumpMarketInfo[i];
    -- value 累计值
    local v = tonumber(jumpMarketInfo[i + 1]);
    jumpMarketTotal = jumpMarketTotal + v;
    if k == oddsType then
        jumpMarketCurrent = v;
    end
end
-- 跳盘差值
local jumpMarketDiff = jumpMarketCurrent - (jumpMarketTotal - jumpMarketCurrent);

-- 是否跳盘，0-否，1-是
local isJumpMarket = 0;
-- 累计跳盘/一级跳盘 次数
local oneLevelTimes = 0;
-- 单枪跳盘/二级跳盘 次数
local twoLevelTimes = 0;

-- 是否开启跳盘，0-否，1-是
if isOpenJumpMarket == 0 then
    return { isJumpMarket, oneLevelTimes, twoLevelTimes, tostring(jumpMarketCurrent), tostring(jumpMarketTotal) };
end

-- 1-两项盘，2-三项盘
if isTwoMarket == 1 then
    -- 0-累计/单枪跳分，1-累计差值跳分
    if oddChangeRule == 0 then
        -- 累计/单枪跳分
        if betAmount >= jumpMarketTwoLimit and jumpMarketTwoLimit >= 100 then
            -- 单枪跳盘
            isJumpMarket = 1;
            twoLevelTimes = 1;
        elseif jumpMarketDiff >= jumpMarketOneLimit and jumpMarketOneLimit >= 100 then
            -- 累计跳盘
            isJumpMarket = 1;
            oneLevelTimes = 1;
        end
    else
        -- 累计差值跳分
        -- 是否倍数跳盘，0-否，1-是
        if isMultipleJumpMarket == 0 then
            if jumpMarketDiff >= jumpMarketTwoLimit and jumpMarketTwoLimit >= 100 then
                -- 二级跳盘
                isJumpMarket = 1;
                twoLevelTimes = 1;
            elseif jumpMarketDiff >= jumpMarketOneLimit and jumpMarketOneLimit >= 100 then
                -- 一级跳盘
                isJumpMarket = 1;
                oneLevelTimes = 1;
            end
        else
            -- 倍数跳盘
            while (jumpMarketDiff >= jumpMarketTwoLimit and jumpMarketTwoLimit >= 100)
            do
                isJumpMarket = 1;
                twoLevelTimes = twoLevelTimes + 1;
                jumpMarketDiff = jumpMarketDiff - jumpMarketTwoLimit;
            end
            while (jumpMarketDiff >= jumpMarketOneLimit and jumpMarketOneLimit >= 100)
            do
                isJumpMarket = 1;
                oneLevelTimes = oneLevelTimes + 1;
                jumpMarketDiff = jumpMarketDiff - jumpMarketOneLimit;
            end
        end
    end
end

if isJumpMarket == 1 then
    -- 只清当前位置的平衡值，按照限额的倍数清，剩下的零头保留
    if oddChangeRule == 1 and isMultipleJumpMarket == 1 then
        local incrValue = -1 * (twoLevelTimes * jumpMarketTwoLimit + oneLevelTimes * jumpMarketOneLimit);
        redis.call("HINCRBYFLOAT", jumpMarketBetKey, oddsType, incrValue);
        redis.call("HINCRBYFLOAT", jumpMarketMixKey, oddsType, incrValue);
        redis.call("expire", jumpMarketBetKey, 1000 * 60 * 60)
    end
end

return { isJumpMarket, oneLevelTimes, twoLevelTimes, tostring(jumpMarketCurrent), tostring(jumpMarketTotal), tostring(jumpMarketDiff) };