-- 商户维度每个投注项投注额累计key
local merchantBetKey = KEYS[1];
-- 商户维度每个投注项期望赔付累计key
local merchantPaymentKey = KEYS[2];
-- 用户维度每个投注项投注额累计key
local userBetKey = KEYS[3];
-- 用户维度每个投注项期望赔付累计key
local userPaymentKey = KEYS[4];

-- 商户ID
local tenantId = ARGV[1];
-- 信用代理ID
local creditId = ARGV[2];
-- 用户ID
local userId = ARGV[3];

-- 赛事ID
local matchId = ARGV[4];
-- 盘口ID
local marketId = ARGV[5];
-- 投注项ID
local optionId = ARGV[6];

-- 投注额
local betAmount = tonumber(ARGV[7]);
-- 期望赔付
local payment = tonumber(ARGV[8]);

-- 商户玩法赔付限额
local merchantPlayLimit = tonumber(ARGV[9]);
-- 用户玩法累计赔付限额
local userPlayLimit = tonumber(ARGV[10]);
-- 用户单注投注赔付限额
local userSingleBetLimit = tonumber(ARGV[11]);
-- 用户单项赔付限额
local userOptionLimit = tonumber(ARGV[12]);

local merchantBet = redis.call("HINCRBY", merchantBetKey, optionId, betAmount);
local merchantPayment = redis.call("HINCRBY", merchantPaymentKey, optionId, payment);
local userBet = redis.call("HINCRBY", userBetKey, optionId, betAmount);
local userPayment = redis.call("HINCRBY", userPaymentKey, optionId, payment);
redis.call("expire", merchantBetKey, 180 * 24 * 60 * 60);
redis.call("expire", merchantPaymentKey, 180 * 24 * 60 * 60);
redis.call("expire", userBetKey, 180 * 24 * 60 * 60);
redis.call("expire", userPaymentKey, 180 * 24 * 60 * 60);

-- 获取其它投注项投注额之和
local function getOtherOptionBetSum(betKey, currentOptionId)
    local otherOptionBetSum = 0;
    local betMap = redis.call('HGETALL', betKey);
    for i = 1, #betMap, 2 do
        -- 投注项ID
        local hashKey = betMap[i];
        -- 投注额累计
        local hashValue = tonumber(betMap[i + 1]);
        if hashKey ~= currentOptionId then
            otherOptionBetSum = otherOptionBetSum + hashValue;
        end
    end
    return otherOptionBetSum;
end

if userPayment > userOptionLimit then
    local msg = string.format("冠军玩法额度查询，用户单项限额拒单，limit=%s,payment=%s", userOptionLimit, userPayment);
    return { -21103, msg, tostring(merchantBet), tostring(merchantPayment), tostring(userBet), tostring(userPayment) };
end

local userPlayOtherOptionBetSum = getOtherOptionBetSum(userBetKey, optionId);
if userPayment > userPlayLimit + tonumber(userPlayOtherOptionBetSum) then
    local msg = string.format("冠军玩法额度查询，用户玩法限额拒单，limit=%s+%s,payment=%s", userPlayLimit, userPlayOtherOptionBetSum, userPayment);
    return { -21101, msg, tostring(merchantBet), tostring(merchantPayment), tostring(userBet), tostring(userPayment) };
end

local merchantPlayOtherOptionBetSum = getOtherOptionBetSum(merchantBetKey, optionId);
if merchantPayment > merchantPlayLimit + tonumber(merchantPlayOtherOptionBetSum) then
    local msg = string.format("冠军玩法额度查询，商户玩法限额拒单，limit=%s+%s,payment=%s", merchantPlayLimit, merchantPlayOtherOptionBetSum, merchantPayment);
    return { -21001, msg, tostring(merchantBet), tostring(merchantPayment), tostring(userBet), tostring(userPayment) };
end

return { 0, "冠军玩法订单校验成功", tostring(merchantBet), tostring(merchantPayment), tostring(userBet), tostring(userPayment) };
