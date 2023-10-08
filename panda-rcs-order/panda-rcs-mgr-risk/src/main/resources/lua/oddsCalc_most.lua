-- 1-两项盘，2-三项盘
local isTwoMarket = tonumber(ARGV[1]);
-- 投注额
local betAmount = tonumber(ARGV[2]);
-- 欧赔赔率
local odds = tonumber(ARGV[3]);
-- 跳赔一级限额
local homeOneLevel = tonumber(ARGV[4]);
-- 跳赔二级限额
local homeSecondLevel = tonumber(ARGV[5]);
-- 投注项类型，oddsType
local playOptionId = ARGV[6];
-- 1上盘/3下盘
local isHome = tonumber(ARGV[7]);
-- 累计差额计算方式，0-投注额差值，1-投注/赔付混合差值
local balanceOption = tonumber(ARGV[8]);
-- 跳分机制，0-累计/单枪跳分，1-累计差值跳分
local oddChangeRule = tonumber(ARGV[9]);
-- 足球-盘口ID，篮球-位置ID
local marketId = ARGV[10];
-- 赛事账务日
local dateExpect = ARGV[11];
-- 赛种
local sportId = tonumber(ARGV[12]);
-- 开启跳赔
local isOpenJumpOdds = tonumber(ARGV[13]);
-- 开启倍跳
local isMultipleJumpOdds = tonumber(ARGV[14]);

local suffixKey = "{" .. marketId .. "}";
local key = "rcs:odds:calc:" .. dateExpect .. ":" .. marketId;
local orderAmountKey = key .. suffixKey;
local orderLockKey = key .. ":lock" .. suffixKey;

local orderLockTimeKey = key .. ":lock:time" .. suffixKey;

local lockVal = redis.call('GET', orderLockKey);

if not lockVal then
    lockVal = 0;
end ;
--赔率正在变化，直接返回
if tonumber(lockVal) == 1 then
    return { 2 };
end ;

if tonumber(sportId) == 1 then
    local lockVal = redis.call('GET', orderLockTimeKey);
    --赔率在当前秒已经变更，不再变化
    if lockVal then
        return { 3 };
    end ;
end ;

--同时保存投注额 和 赔付值
local currentOptionAmount = redis.call("HINCRBYFLOAT", orderAmountKey, playOptionId, betAmount);

local isChangeOdds = 0;
local changeLevel = 0;

--单枪/累计
if tonumber(currentOptionAmount) >= homeSecondLevel and homeSecondLevel >= 100 then
    isChangeOdds = 1;
    changeLevel = 2;
elseif betAmount >= homeOneLevel and homeOneLevel >= 100 then
    isChangeOdds = 1;
    changeLevel = 1;
end;

if isChangeOdds == 1 then
    --赔率变化中，持续30s，不接受注单
    redis.call('SETEX', orderLockKey, 30, 1);
    --记录跳赔时间，足球一秒内不在记录货量
    redis.call('SETEX', orderLockTimeKey, 1, 1);
    --清空钱
     redis.call("HDEL", orderAmountKey, playOptionId);
    lockVal = 1;
end ;
return { lockVal, isChangeOdds, changeLevel, tostring(currentOptionAmount)};