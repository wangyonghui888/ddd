local orderNo = KEYS[1];
local userMatchPlayMarketKey = KEYS[2];
local userMatchPlayKey = KEYS[3]
local userMatchKey = KEYS[4]
local userKey = KEYS[5]
local singleMatchKey = KEYS[6]

local dateExpect = ARGV[2];
local busId = ARGV[3];
local matchId = ARGV[4];
local matchType = ARGV[5];
local playId = ARGV[6];
local playType = ARGV[7];
local betAmount = ARGV[8];
local odds = ARGV[9];
local isRecType = tonumber(ARGV[10]);
local userId = ARGV[11];
local optionId = ARGV[12];
local marketId = ARGV[13];
local currentPaidMoney = tonumber(ARGV[14]);
local sportId = ARGV[15];
local recData = loadstring("return " .. ARGV[16])();
local recLength = tonumber(ARGV[17]);

local userMatchPlayPaidMoneyByDB = tonumber(ARGV[18]);
local userMatchAllPaidAmountByDB = tonumber(ARGV[19]);
local userDayAllPaidAmountByDB = tonumber(ARGV[20]);
local singleMatchAllPaidAmountByDB = tonumber(ARGV[21]);

local code = tonumber(ARGV[22]);

if code == 1 then 
	code = -100;
end;

if code >= -1 then 
	return {-1,'订单已存在异常不做处理'};
end;

local result = redis.call('GET', orderNo);

local isSuccess = -1;
local errMsg = nil;
if result then	
 	errMsg = "订单已回滚，不在做回滚操作";
	return {-1,errMsg};
end;

redis.call('SETEX', orderNo , 10 , '1');

local diffVal = 0;

local optionMaxPaidMoney = redis.call("HINCRBYFLOAT",userMatchPlayMarketKey,optionId,currentPaidMoney * -1);
local allOrderBetMoney = redis.call("HINCRBYFLOAT",userMatchPlayMarketKey,'allOrderMoney',betAmount * -1);
local redisMaxOptionPaid = redis.call("hget",userMatchPlayMarketKey,'maxOptionPaid');

local userMatchPlayMarketKeyList = redis.call('HGETALL', userMatchPlayMarketKey);
local currentOptionMaxPaidMoney = 0;
for i = 1,#userMatchPlayMarketKeyList,2 do
	local tempKey = userMatchPlayMarketKeyList[i];
	if not (tempKey == 'allOrderMoney' or tempKey == 'maxOptionPaid' or tempKey == 'MAX_PLAY_PAID') then 
		if currentOptionMaxPaidMoney < tonumber(userMatchPlayMarketKeyList[i + 1]) then 
			currentOptionMaxPaidMoney = tonumber(userMatchPlayMarketKeyList[i + 1]);
		end;
	end;
end;

redisMaxOptionPaid = currentOptionMaxPaidMoney;
redis.call("hset",userMatchPlayMarketKey,'maxOptionPaid',redisMaxOptionPaid);

local maxPlayPaid = tonumber(redisMaxOptionPaid) - tonumber(allOrderBetMoney);
local redisMaxPlayPaid = redis.call("hget",userMatchPlayMarketKey,'MAX_PLAY_PAID');
redis.call("hset",userMatchPlayMarketKey,'MAX_PLAY_PAID' , maxPlayPaid);

diffVal = maxPlayPaid - redisMaxPlayPaid;

local secondKey = playId .. '_' .. matchType .. '_' .. playType;
redis.call("HINCRBYFLOAT",userMatchKey,secondKey,diffVal);

if code >= -2 then 
	return {-2,'用户玩法赔付限额已做回滚'};
end;

if isRecType == 0 then 
	local length = recLength;
	
	local min = nil;
	for i = 1,length do
		for j = 1 , length do
			local key = string.format("%03d",(i -1)) .. string.format("%03d",(j -1)); 
			local result = redis.call("HINCRBY",userMatchKey,key,recData[key] * -1);
			if min == nil then 
				min = result;
			end;
			if min > result then
				min = result;
			end;
		end;
	end;
	local recMaxPaid = redis.call("hget",userMatchKey,"REC_MAX_PAID");
	if not recMaxPaid then
		recMaxPaid = 0;
	end; 
	
	diffVal = recMaxPaid - min;
	
	redis.call("hset",userMatchKey,"REC_MAX_PAID" ,min);
end;

redis.call("HINCRBYFLOAT",userMatchKey,"USER_MATCH_ALL_PAID",diffVal);
if code >= -3 then 
	return {-3,'用户赛事限额已做回滚'};
end;

redis.call("INCRBYFLOAT",userKey,diffVal);
if code >= -4 then 
	return {-4,'用户单日限额已做回滚'};
end;

redis.call("INCRBYFLOAT",singleMatchKey,diffVal);
if code >= -5 then 
	return {-5,'单场赛事限额已做回滚'};
end;

return {1,'回滚成功'};