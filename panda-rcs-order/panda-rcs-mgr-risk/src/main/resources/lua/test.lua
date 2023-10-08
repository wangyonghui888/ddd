-- 赛事账务日
local dateExpect = "2021-03-11";
-- 足球-盘口ID，篮球-位置ID
local marketId = "2006122_38_1";

-- 跳盘 投注额 累计key
local jumpMarketBetKey = string.format("rcs:odds:jumpMarket:%s:%s:bet{%s}", dateExpect, marketId, marketId);
-- 跳盘 投注/赔付混合 累计key
local jumpMarketMixKey = string.format("rcs:odds:jumpMarket:%s:%s:mix{%s}", dateExpect, marketId, marketId);

print(jumpMarketBetKey);
print(jumpMarketMixKey);

local suffixKey = "{" .. marketId .. "}";
-- 跳盘 投注额 累计key
local jumpMarketBetKey2 = string.format("rcs:odds:jumpMarket:%s:%s:bet", dateExpect, marketId) .. suffixKey;
-- 跳盘 投注/赔付混合 累计key
local jumpMarketMixKey2 = string.format("rcs:odds:jumpMarket:%s:%s:mix", dateExpect, marketId) .. suffixKey;
print(jumpMarketBetKey2);
print(jumpMarketMixKey2);

print(jumpMarketBetKey==jumpMarketBetKey2);
print(jumpMarketMixKey==jumpMarketMixKey2);

-- 累计跳盘限额/一级累计跳盘限额
local jumpMarketOneLimit = 100;
-- 单枪跳盘限额/二级累计跳盘限额
local jumpMarketTwoLimit = 200;

-- 所有投注项累计
local jumpMarketTotal = 1010;
-- 当前投注项累计
local jumpMarketCurrent = 800;

-- 跳盘差值
local jumpMarketDiff = jumpMarketCurrent - (jumpMarketTotal - jumpMarketCurrent);

-- 是否跳盘，0-不跳盘，1-跳盘
local isJumpMarket = 0;
-- 累计跳盘/一级跳盘 次数
local oneLevelTimes = 0;
-- 单枪跳盘/二级跳盘 次数
local twoLevelTimes = 0;

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

print("===================================");
print(isJumpMarket);
print(oneLevelTimes);
print(twoLevelTimes);

print(-1 * oneLevelTimes * jumpMarketOneLimit);
print(-1 * twoLevelTimes * jumpMarketTwoLimit);

local incrValue = -1 * (twoLevelTimes * jumpMarketTwoLimit + oneLevelTimes * jumpMarketOneLimit);
print(incrValue);