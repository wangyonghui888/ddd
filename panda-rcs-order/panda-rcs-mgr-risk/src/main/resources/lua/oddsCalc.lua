local orderAmountKey = KEYS[1];
local orderCountKey = KEYS[2];
local orderLockKey = KEYS[3];

local isTwoMarket = tonumber(ARGV[1]);
local betAmount = tonumber(ARGV[2]);
local odds = tonumber(ARGV[3]);
local homeOneLevel = tonumber(ARGV[4]);
local homeSecondLevel = tonumber(ARGV[5]);
local playOptionId = ARGV[6];
local awayOneLevel = tonumber(ARGV[7]);
local awaySecondLevel = tonumber(ARGV[8]);
local isHome = tonumber(ARGV[9]);

local lockVal = redis.call('GET',orderLockKey);

if not lockVal then 
	lockVal = 0;
end;

if tonumber(lockVal) == 1 then 
	return {2};
end;

redis.call("HINCRBY",orderAmountKey,playOptionId,betAmount);

local info = redis.call('HGETALL', orderAmountKey);
local allAmount = 0;
local currentOptionAmount = 0 ;
for i = 1,#info,2 do
	allAmount = allAmount + info[i + 1];
	if info[i] == playOptionId then
		currentOptionAmount = info[i + 1];
	end;
end;

local orderCountValue = redis.call("INCR",orderCountKey);

local isChangeOdds = 0;
local changeLevel = 0;
currentOptionAmount = tonumber(currentOptionAmount);
local diff = currentOptionAmount - (allAmount - currentOptionAmount);
if isTwoMarket == 1 then
    if isChangeOdds == 0 and isHome == 1 then
        if diff > homeSecondLevel and homeSecondLevel > 0 then
            isChangeOdds = 1;
            changeLevel = 2;
        elseif diff > homeOneLevel and homeOneLevel > 0 then
            isChangeOdds = 1;
            changeLevel = 1;
        end
    end;

    if isChangeOdds == 0 and isHome == 3  then
        if diff > awaySecondLevel and awaySecondLevel > 0 then
            isChangeOdds = 1;
            changeLevel = 2;
        elseif diff > awayOneLevel and awayOneLevel > 0 then
            isChangeOdds = 1;
            changeLevel = 1;
        end
    end;
else
	if  isChangeOdds == 0 and homeOneLevel>0 and currentOptionAmount * odds - allAmount > homeOneLevel then
		isChangeOdds = 1;
		changeLevel = 1;
	end;
end;

if isChangeOdds == 1 then
    --赔率变化中，持续30s，不接受注单
	redis.call('SETEX',orderLockKey,30,1);
	lockVal = 1;
end;
--local diff = currentOptionAmount - (allAmount - currentOptionAmount);
return {lockVal,isChangeOdds,changeLevel,currentOptionAmount,allAmount,isHome};

