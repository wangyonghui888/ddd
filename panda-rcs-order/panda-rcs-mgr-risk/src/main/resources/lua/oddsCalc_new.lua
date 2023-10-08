local orderAmountKey = KEYS[1];
local orderAmountPlusKey = KEYS[2];
local orderCountKey = KEYS[3];
local orderLockKey = KEYS[4];

local isTwoMarket = tonumber(ARGV[1]);
local betAmount = tonumber(ARGV[2]);
local odds = tonumber(ARGV[3]);
local homeOneLevel = tonumber(ARGV[4]);
local homeSecondLevel = tonumber(ARGV[5]);
local playOptionId = ARGV[6];
--1上盘/3下盘
local isHome = tonumber(ARGV[7]);
--平衡值计算选型 0 投注额差值 1 投注额/赔付组合差值
local balanceOption = tonumber(ARGV[8]);
local oddChangeRule = tonumber(ARGV[9]);


local lockVal = redis.call('GET',orderLockKey);

if not lockVal then 
	lockVal = 0;
end;
--赔率正在变化中,下单失败
if tonumber(lockVal) == 1 then 
	return {2};
end;
--同时保存投注额 和 赔付值
redis.call("HINCRBYFLOAT",orderAmountKey,playOptionId,betAmount);
if odds > 2 then
    local calculatedValue = betAmount*(odds-1)
    redis.call("HINCRBYFLOAT",orderAmountPlusKey,playOptionId,calculatedValue);
else
    redis.call("HINCRBYFLOAT",orderAmountPlusKey,playOptionId,betAmount);
end

local info = redis.call('HGETALL', orderAmountKey);
if balanceOption == 1 then
    info = redis.call('HGETALL', orderAmountPlusKey);
end
local allAmount = 0;
local currentOptionAmount = 0 ;
for i = 1,#info,2 do
	allAmount = allAmount + info[i + 1];
	if info[i] == playOptionId then
		currentOptionAmount = info[i + 1]
	end;
end;

local orderCountValue = redis.call("INCR",orderCountKey);

local isChangeOdds = 0;
local changeLevel = 0;
currentOptionAmount = tonumber(currentOptionAmount);
local diff = currentOptionAmount - (allAmount - currentOptionAmount);
if isTwoMarket == 1 then
    --跳分机制 0 累计/单枪 1 差额累计
    if oddChangeRule == 0 then
        --单枪/累计
        if betAmount > homeOneLevel and homeOneLevel >= 100  then
            isChangeOdds = 1;
            changeLevel = 1;
        elseif diff > homeSecondLevel and homeSecondLevel >= 100 then
            isChangeOdds = 1;
            changeLevel = 2;
        end
    else
        --1,2级
        if diff > homeSecondLevel and homeSecondLevel >= 100 then
            isChangeOdds = 1;
            changeLevel = 2;
        elseif diff > homeOneLevel and homeOneLevel >= 100  then
            isChangeOdds = 1;
            changeLevel = 1;
        end
    end
else
    --3项盘
	if  isChangeOdds == 0 and homeOneLevel>=100 and diff > homeOneLevel then
		isChangeOdds = 1;
		changeLevel = 1;
	end;
end;

if isChangeOdds == 1 then
    --赔率变化中，持续30s，不接受注单
	redis.call('SETEX',orderLockKey,30,1);
	lockVal = 1;
end;
return {lockVal,isChangeOdds,changeLevel,tostring(currentOptionAmount),tostring(allAmount),isHome};

